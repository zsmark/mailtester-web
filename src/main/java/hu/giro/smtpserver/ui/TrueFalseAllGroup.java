package hu.giro.smtpserver.ui;


import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.RadioButtonGroup;
import hu.giro.smtpserver.model.TrueFalseAllEnum;


import static hu.giro.smtpserver.model.TrueFalseAllEnum.*;

public class TrueFalseAllGroup extends CustomField<TrueFalseAllEnum> {

    RadioButtonGroup<TrueFalseAllEnum> group;
    private TrueFalseAllEnum value;

    public TrueFalseAllGroup() {
        this("Igen", "Nem", "Mind");
    }

    public TrueFalseAllGroup(String yesText, String noText, String allText) {
        group = new RadioButtonGroup<TrueFalseAllEnum>();
        group.setItems(TrueFalseAllEnum.values());

        doSetValue(ALL);
        group.addValueChangeListener(e -> {
            super.setValue(group.getValue());
            value= group.getValue();
        });
        withCaptions(yesText, noText, allText);
    }

    public TrueFalseAllGroup withCaptions(String yesText, String noText, String allText) {
        group.setItemCaptionGenerator(value -> {
            if (value == TRUE) return yesText;
            if (value == FALSE) return noText;
            return allText;
        });
        return this;
    }

    @Override
    protected Component initContent() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.addComponent(group);
        group.addStyleName("horizontal");
        group.setId("timefilter_group");
        return layout;
    }


    @Override
    protected void doSetValue(TrueFalseAllEnum value) {

        this.value = value==null?ALL:value;

        group.setValue(this.value);

    }

    @Override
    public TrueFalseAllEnum getValue() {
        return value;

    }

    @Override
    public TrueFalseAllEnum getEmptyValue() {
        return ALL;
    }
}


