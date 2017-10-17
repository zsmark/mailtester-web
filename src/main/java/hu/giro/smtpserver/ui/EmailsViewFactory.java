package hu.giro.smtpserver.ui;

import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
import com.vaadin.data.ValidationException;
import com.vaadin.navigator.View;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.*;
import com.vaadin.ui.components.grid.GridSelectionModel;
import com.vaadin.ui.renderers.DateRenderer;
import hu.giro.smtpserver.model.EmailSearchDTO;
import hu.giro.smtpserver.model.EmailService;
import hu.giro.smtpserver.model.entity.EmailObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaadin.teemusa.gridextensions.SelectGrid;
import org.vaadin.teemusa.gridextensions.client.tableselection.TableSelectionState;
import org.vaadin.teemusa.gridextensions.tableselection.TableSelectionModel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

@Component
public class EmailsViewFactory {
    @Autowired
    private EmailService emailService;

    static private Log log = LogFactory.getLog(EmailsViewFactory.class);
    static private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public class EmailsView extends EmailsDesign implements View {
        private Binder<EmailSearchDTO> binder = new Binder<>(EmailSearchDTO.class);
        private EmailSearchDTO searchDTO = new EmailSearchDTO();

        public EmailsView() {
            super();
            // reload();
            init();
            reload();

            addAttachListener(e -> {
                getUI().setPollInterval(10000);
                getUI().addPollListener(pollEvent -> reload());
                Page.getCurrent().getStyles().add(".bold { font-weight: bold;}");
            });


        }

        private void reload() {
            log.debug("Reloading");
            Set<EmailObject> selected = emailGrid.getSelectedItems();
            emailGrid.setItems(emailService.findAll(searchDTO));
            selected.forEach(email->emailGrid.select(email));
            updateDomains();

        }

        private void updateDomains() {

           // Optional<String> selected = domainFilter.getSelectedItem();
            domainFilter.setItems(emailService.getDomains());
            /*if (selected.isPresent() && emailService.getDomains().contains(selected.get()))
            {
                domainFilter.setSelectedItem(selected.get());
            }*/
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


            markRead.addClickListener(this::markRead);

            emailGrid.setStyleGenerator(item -> item.isRead() ? "" : "bold");

            domainFilter.setEmptySelectionAllowed(true);
            domainFilter.setEmptySelectionCaption("Minden");
            binder.bind(domainFilter, EmailSearchDTO::getDomainFilter, EmailSearchDTO::setDomainFilter);

            readFilter.addValueChangeListener(this::doSearch);
            textFilter.addValueChangeListener(this::doSearch);
            domainFilter.addValueChangeListener(this::doSearch);



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

        private void deleteAll(Button.ClickEvent clickEvent) {
            emailService.truncate();
            emailService.getDomains().clear();
            domainFilter.setSelectedItem(domainFilter.getEmptySelectionCaption());
            reload();

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

        }

    }

    public com.vaadin.ui.Component create() {
        return new EmailsView();
    }

}
