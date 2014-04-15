package org.dt;

import org.dt.domain.DeviceUser;
import org.dt.util.BeanBinder;

import com.vaadin.addon.touchkit.extensions.Html5InputSettings;
import com.vaadin.addon.touchkit.ui.EmailField;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TextField;

public class NewUserView extends CssLayout implements ClickListener,
        TextChangeListener {

    private DeviceUser newUser = new DeviceUser();
    private TextField name = new TextField("Name");
    private EmailField email = new EmailField("Email");
    private Button button;

    public NewUserView() {
        VerticalComponentGroup vcg = new VerticalComponentGroup();
        vcg.setCaption("Or create a new account:");
        name.setWidth("100%");
        Html5InputSettings html5InputSettings = new Html5InputSettings(name);
        html5InputSettings.setAutoComplete(false);
        html5InputSettings.setAutoCorrect(false);
        // Use browsers native "input prompt" instead
        name.setNullRepresentation("");
        name.addTextChangeListener(this);
        html5InputSettings.setPlaceholder("type username here");
        vcg.addComponent(name);
        email.setWidth("100%");
        html5InputSettings = new Html5InputSettings(email);
        email.setNullRepresentation("");
        html5InputSettings.setPlaceholder("and email plz");
        email.addTextChangeListener(this);
        vcg.addComponent(email);

        BeanBinder.bind(newUser, this);

        button = new Button("Create account", this);
        button.setWidth("100%");
        button.setEnabled(false);
        addComponents(vcg, button);

    }

    @Override
    protected String getCss(Component c) {
        return "margin-top:10px;";
    }

    @Override
    public void buttonClick(ClickEvent event) {
        DeviceTracker.get().login(newUser);
    }

    public DeviceUser getUser() {
        return newUser;
    }

    @Override
    public void textChange(TextChangeEvent event) {
        String text = event.getText();
        TextField other = event.getComponent() == name ? email : name;
        String e, u;
        if (event.getComponent() == email) {
            e = text;
            u = other.getValue();
        } else {
            u = text;
            e = other.getValue();
        }
        if (u != null && u.length() > 2 && e != null && e.length() > 5
                && e.contains("@")) {
            button.setEnabled(true);
        } else {
            button.setEnabled(false);
        }

    }

}