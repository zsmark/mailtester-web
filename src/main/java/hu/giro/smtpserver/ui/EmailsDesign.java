package hu.giro.smtpserver.ui;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;

/**
 * !! DO NOT EDIT THIS FILE !!
 * <p>
 * This class is generated by Vaadin Designer and will be overwritten.
 * <p>
 * Please make a subclass with logic and additional interfaces as needed,
 * e.g class LoginView extends LoginDesign implements View { }
 */
@DesignRoot
@AutoGenerated
@SuppressWarnings("serial")
public class EmailsDesign extends VerticalLayout {
    protected ComboBox<String> domainFilter;
    protected TextField textFilter;
    protected TrueFalseAllGroup readFilter;
    protected Button markRead;
    protected Button trashButton;
    protected Grid<hu.giro.smtpserver.model.entity.EmailObject> emailGrid;
    protected HorizontalLayout emailLayout;

    public EmailsDesign() {
        Design.read(this);
    }
}
