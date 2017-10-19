package hu.giro.smtpserver.ui;


import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;

@Theme("custom")
@SpringUI
@Title("Mailtester web")
public class WebUI extends UI {

    @Autowired
    private EmailsViewFactory viewFactory;

    @Override
    protected void init(VaadinRequest request) {
        setContent(viewFactory.create());
       // setContent(new ProbaViewC());
    }
}

