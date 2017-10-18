package hu.giro.smtpserver.ui;

import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
import com.vaadin.data.ValidationException;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.Registration;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.*;
import com.vaadin.ui.components.grid.GridSelectionModel;
import com.vaadin.ui.renderers.DateRenderer;
import hu.giro.smtpserver.model.EmailSearchDTO;
import hu.giro.smtpserver.model.EmailService;
import hu.giro.smtpserver.model.entity.EmailObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.NullInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.mime4j.codec.DecodeMonitor;
import org.apache.james.mime4j.message.DefaultBodyDescriptorBuilder;
import org.apache.james.mime4j.parser.ContentHandler;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.apache.james.mime4j.stream.BodyDescriptorBuilder;
import org.apache.james.mime4j.stream.MimeConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaadin.teemusa.gridextensions.SelectGrid;
import org.vaadin.teemusa.gridextensions.client.tableselection.TableSelectionState;
import org.vaadin.teemusa.gridextensions.tableselection.TableSelectionModel;
import tech.blueglacier.email.Attachment;
import tech.blueglacier.email.Email;
import tech.blueglacier.parser.CustomContentHandler;

import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
                Page.getCurrent().getStyles().add(".bold { font-weight: bold;}");
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
            if (gridSelectRegistration != null)
                gridSelectRegistration.remove();
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
                        if (selected != null) {
                            selected.setRead(true);
                            //TODO save nem kell ?
                        }
                    }
                }
            });
            saveButton.setEnabled(false);
            fileDownloader = new FileDownloader(new DummyResource());
            fileDownloader.extend(saveButton);

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
            saveButton.setEnabled(selected!=null);
            fileDownloader.setFileDownloadResource(
                    new StreamResource(new StreamResource.StreamSource() {
                @Override
                public InputStream getStream() {
                    return new ByteArrayInputStream(emailService.getEmailContent(selected));
                }
            },"mailtester_"+selected.getId()+".mht"));
            if (selected == null) return;
            try {
                emailLayout.addComponent(new EmailDisplay(emailService.getEmailContent(selected)));
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
        }

        public EmailObject getSelectedEmail() {
            return emailGrid.getSelectedItems().stream().findAny().orElse(null);
        }
    }

    public com.vaadin.ui.Component create() {
        return new EmailsView();
    }

}
