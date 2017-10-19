package hu.giro.smtpserver.ui;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.NullInputStream;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.codec.DecodeMonitor;
import org.apache.james.mime4j.message.DefaultBodyDescriptorBuilder;
import org.apache.james.mime4j.parser.ContentHandler;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.apache.james.mime4j.stream.BodyDescriptorBuilder;
import org.apache.james.mime4j.stream.MimeConfig;
import org.vaadin.alump.labelbutton.LabelButton;
import tech.blueglacier.email.Attachment;
import tech.blueglacier.email.Email;
import tech.blueglacier.parser.CustomContentHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class EmailDisplay extends VerticalLayout {
    private List<Attachment> attachments;
    private Attachment calendar;
    private Attachment htmlBody;
    private Attachment plainText;
    String to, cc, from;
    private String body= "Üres";
    private boolean htmlType;
    private GridLayout headerLayout;
    private Label hLine;

    public EmailDisplay(byte[] emailContent) throws IOException, MimeException {
        parseMail(emailContent);
        setSpacing(false);
        setSizeFull();
        setMargin(false);
        setId("emailDisplay");
        init();
    }

    private void init() {
        createHeaderLayout();
        Label html = new Label(body);
        html.setContentMode(ContentMode.HTML);
        Panel panel = new Panel();
        html.setSizeUndefined();
        
        panel.setContent(html);
        panel.setSizeFull();

        addComponent(panel);
        setExpandRatio(panel,1);
    }

    private void createHeaderLayout() {

        headerLayout = new GridLayout(2,4);
        Label fromLabel = new Label("Feladó: ");
        Label fromText = new Label(from);
        Label toLabel = new Label("Címzettek: ");
        Label toText = new Label(to);
        Label copyLabel = new Label("Másolat: ");
        Label copyText = new Label(cc);
        Label attachmentLabel = new Label("Csatolmányok: ");
        headerLayout.addComponent(fromLabel,0,0);
        headerLayout.addComponent(fromText,1,0);
        headerLayout.addComponent(toLabel,0,1);
        headerLayout.addComponent(toText,1,1);
        headerLayout.addComponent(copyLabel,0,2);
        headerLayout.addComponent(copyText,1,2);
        headerLayout.addComponent(attachmentLabel,0,3);
        headerLayout.addComponent(createAttachmentButtons(),1,3);
        headerLayout.setSpacing(true);
        for (Component comp: Arrays.asList(fromLabel,toLabel,copyLabel,attachmentLabel))
            headerLayout.setComponentAlignment(comp,Alignment.MIDDLE_RIGHT);
        headerLayout.setWidth(100,Unit.PERCENTAGE);
        headerLayout.setColumnExpandRatio(1,1);
        addComponent(headerLayout);
        setComponentAlignment(headerLayout,Alignment.MIDDLE_LEFT);
        hLine = new Label("<HR/>");
        hLine.setContentMode(ContentMode.HTML);
        hLine.setWidth(100,Unit.PERCENTAGE);
        hLine.setHeight(3,Unit.PIXELS);
        headerLayout.setWidth("100%");
        addComponent(hLine);

    }

    private Component createAttachmentButtons() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        if(attachments != null) {
            for (Attachment attachment : attachments) {
                Button button = new Button(attachment.getAttachmentName());
                button.setIcon(VaadinIcons.DOWNLOAD);
                button.addStyleName(ValoTheme.BUTTON_BORDERLESS );
                button.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
                /*button.setStyleName("nude");
                Label l = new Label("F");
*/
                FileDownloader fileDownloader = new FileDownloader( new StreamResource((StreamResource.StreamSource) () -> {
                    try {
                        return new ByteArrayInputStream(IOUtils.toByteArray(attachment.getIs()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return new NullInputStream(0);
                }, attachment.getAttachmentName()));
                fileDownloader.extend(button);
                horizontalLayout.addComponent(button);
            }
        }
        return horizontalLayout;
    }

    private String convertToString(Attachment attachment) throws IOException {
        return IOUtils.toString(attachment.getIs(),attachment.getBd().getCharset());
    }

    private void parseMail(byte[] emailContent) throws MimeException, IOException {
        String x = new String(emailContent,"UTF8");
        System.out.println(x);
        ContentHandler contentHandler = new CustomContentHandler();

        MimeConfig mime4jParserConfig = MimeConfig.DEFAULT;
        BodyDescriptorBuilder bodyDescriptorBuilder = new DefaultBodyDescriptorBuilder();
        MimeStreamParser mime4jParser = new MimeStreamParser(mime4jParserConfig, DecodeMonitor.SILENT, bodyDescriptorBuilder);
        mime4jParser.setContentDecoding(true);
        mime4jParser.setContentHandler(contentHandler);

        InputStream mailIn = new ByteArrayInputStream(emailContent);
        mime4jParser.parse(mailIn);

        Email email = ((CustomContentHandler) contentHandler).getEmail();

        attachments = email.getAttachments();

        calendar = email.getCalendarBody();
        htmlBody = email.getHTMLEmailBody();

        plainText = email.getPlainTextEmailBody();

        if (htmlBody!=null) {
            body = convertToString(htmlBody);
            htmlType=true;
        }
        else if (plainText!=null)
        {   body = convertToString(plainText);
            htmlType=false;
        }

        to = email.getToEmailHeaderValue();
        cc = email.getCCEmailHeaderValue();
        from = email.getFromEmailHeaderValue();

    }


}
