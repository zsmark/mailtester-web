package hu.giro.smtpserver.ui;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;

@SpringComponent
//@PrototypeScope
public class AccessDeniedView/* extends AccessDeniedDesign*/ implements View {

    @Override
    public void enter(ViewChangeEvent event) {
        // Nothing to do, just show the view
    }

}