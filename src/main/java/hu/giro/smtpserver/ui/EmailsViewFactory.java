package hu.giro.smtpserver.ui;

import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
import com.vaadin.data.ValidationException;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;
import hu.giro.smtpserver.model.EmailSearchDTO;
import hu.giro.smtpserver.model.EmailService;
import hu.giro.smtpserver.model.entity.EmailObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class EmailsViewFactory {
  static private Log log = LogFactory.getLog(EmailsViewFactory.class);
  static private SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy.MM.dd/HH:mm:ss");
  @Autowired
  private EmailService emailService;

  public com.vaadin.ui.Component create() {
    return new EmailsView();
  }

  public class EmailsView extends EmailsDesign implements View {
    private Binder<EmailSearchDTO> binder = new Binder<>(EmailSearchDTO.class);
    private EmailSearchDTO searchDTO = new EmailSearchDTO();
    private Registration dfRegistration;
    private Registration gridSelectRegistration;
    private FileDownloader fileDownloader;

    public EmailsView() {
      super();
      // reload();
      init();
      reload();

      addAttachListener(e -> {
        getUI().setPollInterval(10000);
        getUI().addPollListener(pollEvent -> reload());
//                Page.getCurrent().getStyles().add(".bold { font-weight: bold;}");
      });


    }

    private void reload() {
      log.debug("Reloading");
      EmailObject selected = getSelectedEmail();
      removeEmailSelector();
      emailGrid.setItems(emailService.findAll(searchDTO));
      if (selected != null) {
        emailGrid.select(selected);
      }
      addEmailSelector();

      updateDomains();

    }

    private void removeEmailSelector() {
      if (gridSelectRegistration != null) {
        gridSelectRegistration.remove();
      }
    }

    private void addEmailSelector() {
      gridSelectRegistration = emailGrid.addSelectionListener(selectionEvent -> viewEmailContent());
    }

    private void updateDomains() {
      dfRegistration.remove();
      Optional<String> selected = domainFilter.getSelectedItem();
      domainFilter.setItems(emailService.getDomains());
      if (selected.isPresent() && emailService.getDomains().contains(selected.get())) {
        domainFilter.setSelectedItem(selected.get());
      }
      dfRegistration = domainFilter.addValueChangeListener(this::doSearch);
    }

    private void init() {

      Column<EmailObject, Date> dateColumn = (Column<EmailObject, Date>) emailGrid.getColumn("receivedDate");
      dateColumn.setRenderer(new DateRenderer(timeFormat));
      dateColumn.setExpandRatio(1);

      emailGrid.getColumn("subject").setExpandRatio(10);
      //emailGrid.addColumn(EmailObject::toString).setCaption("Plusszadat");

      readFilter.withCaptions("Olvasott", "Olvasatlan", "Mind");
      binder.readBean(searchDTO);
      binder.bind(readFilter, EmailSearchDTO::getReadFilter, EmailSearchDTO::setReadFilter);
      binder.bind(textFilter, "textFilter");


      //markRead.addClickListener(this::markRead);

      emailGrid.setStyleGenerator(item -> item.isRead() ? "" : "bold");

      domainFilter.setEmptySelectionAllowed(true);
      domainFilter.setEmptySelectionCaption("Minden");
      domainFilter.addValueChangeListener(valueChangeEvent -> {
        deleteButton.setEnabled(StringUtils.hasText(valueChangeEvent.getValue()) && !"MINDEN".equalsIgnoreCase(valueChangeEvent.getValue()));
      });
      binder.bind(domainFilter, EmailSearchDTO::getDomainFilter, EmailSearchDTO::setDomainFilter);

      readFilter.addValueChangeListener(this::doSearch);
      textFilter.addValueChangeListener(this::doSearch);
      dfRegistration = domainFilter.addValueChangeListener(this::doSearch);
      emailGrid.setSelectionMode(SelectionMode.SINGLE);
      addEmailSelector();
      emailGrid.addShortcutListener(new ShortcutListener("READED", ShortcutAction.KeyCode.DELETE, null) {
        @Override
        public void handleAction(Object sender, Object target) {
          if ((target != null) && (target instanceof Grid)) {

            Grid<EmailObject> targetGrid = (Grid) target;
            EmailObject selected = getSelectedEmail();
            if (selected != null && !selected.isRead()) {
              selected.setRead(true);
              emailService.save(selected);
              emailGrid.getDataProvider().refreshItem(selected);
              selectNextMail();
              //TODO save nem kell ?
            }
          }
        }
      });

      saveButton.setEnabled(false);
      fileDownloader = new FileDownloader(new DummyResource());
      fileDownloader.extend(saveButton);
      deleteButton.setEnabled(false);
      deleteButton.addClickListener(clickEvent -> {
        emailService.deleteAll(domainFilter.getValue());
        doSearch(null);
      });
           /*int index = getComponentIndex(emailGrid);
            TableSelectionModel<EmailObject> selectionModel = new TableSelectionModel<>();
            selectionModel.setMode(TableSelectionState.TableSelectionMode.SHIFT);
            try {
                Method setter = Grid.class.getDeclaredMethod("setSelectionModel", GridSelectionModel.class);
                setter.setAccessible(true);
                setter.invoke(emailGrid,selectionModel);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
    }

    private void selectNextMail() {
      EmailObject selected = getSelectedEmail();
      if (selected == null) {
        return;
      }
      List<EmailObject> items =
          (List<EmailObject>) ((ListDataProvider<EmailObject>) emailGrid.getDataProvider()).getItems();
      int index = items.indexOf(selected);
      if (index < items.size() - 1) {
        emailGrid.select(items.get(index + 1));
      }
    }


    private void markRead(Button.ClickEvent clickEvent) {
      if (!emailGrid.getSelectedItems().isEmpty()) {
        emailGrid.getSelectedItems().forEach(emailObject -> {
          emailObject.setRead(true);
          emailService.save(emailObject);
        });
      }
      reload();
    }

    private void doSearch(HasValue.ValueChangeEvent event) {
      //log.info(searchDTO);
      try {
        binder.writeBean(searchDTO);
      } catch (ValidationException e) {
        e.printStackTrace();
      }
      //log.info(searchDTO);
      reload();
      viewEmailContent();


    }

    private void viewEmailContent() {
      emailLayout.removeAllComponents();

      EmailObject selected = getSelectedEmail();
      saveButton.setEnabled(selected != null);

      if (selected == null) {
        return;
      }

      fileDownloader.setFileDownloadResource(
          new StreamResource(new StreamResource.StreamSource() {
            @Override
            public InputStream getStream() {
              return new ByteArrayInputStream(emailService.getEmailContent(selected));
            }
          }, "mailtester_" + selected.getId() + ".eml"));

      try {
        emailLayout.addComponent(new EmailDisplay(emailService.getEmail(selected)));
      } catch (Exception ex) {
        ex.printStackTrace();
        return;
      }
    }

    public EmailObject getSelectedEmail() {
      return emailGrid.getSelectedItems().stream().findAny().orElse(null);
    }
  }

}
