package hu.giro.smtpserver.ui;

import com.vaadin.server.ConnectorResource;
import com.vaadin.server.DownloadStream;
import com.vaadin.server.Resource;

public class DummyResource implements ConnectorResource {
    @Override
    public String getMIMEType() {
        return null;
    }

    @Override
    public DownloadStream getStream() {
        return null;
    }

    @Override
    public String getFilename() {
        return null;
    }
}
