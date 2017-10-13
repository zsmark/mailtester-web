package hu.giro.smtpserver.ui;


import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

@Theme("valo")
@SpringUI
//@Viewport("width=device-width,initial-scale=1.0,user-scalable=no")
@Title("Mailtester web")
public class WebUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        setContent(new Button("Click me", e -> Notification.show("Hello Spring+Vaadin user!")));
    }
}

