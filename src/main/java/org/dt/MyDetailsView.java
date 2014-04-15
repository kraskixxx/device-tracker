package org.dt;

import java.util.List;

import org.dt.domain.DeviceUser;
import org.dt.domain.Loan;

import com.vaadin.addon.touchkit.ui.EmailField;
import com.vaadin.addon.touchkit.ui.NavigationButton;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickEvent;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickListener;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class MyDetailsView extends NavigationView implements Component,
        ValueChangeListener, ClickListener {

    private TextField name = new TextField("Name");
    private EmailField email = new EmailField("Email");
    private Button saveButton = new Button("Save", this);
    private Button resetButton = new Button("Cancel", this);
    private NavigationButton showAllLoansButton = new NavigationButton(
            "My loan history...");
    private VerticalComponentGroup loans;

    public MyDetailsView() {
        setCaption("My Details");
        setRightComponent(saveButton);
        setLeftComponent(resetButton);
        CssLayout cssLayout = new CssLayout();

        VerticalComponentGroup details = new VerticalComponentGroup(
                "My details");
        details.addComponents(name, email);
        name.addValueChangeListener(this);
        name.setImmediate(true);
        email.setImmediate(true);
        email.addValueChangeListener(this);

        cssLayout.addComponent(details);

        Button logout = new Button("Logout");
        logout.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                UI.getCurrent().getPage()
                        .setLocation(UI.getCurrent().getPage().getLocation());
                VaadinSession.getCurrent().close();
            }
        });
        cssLayout.addComponent(logout);


        loans = new VerticalComponentGroup("Current loans");
        showAllLoansButton
                .addClickListener(new NavigationButtonClickListener() {
                    @Override
                    public void buttonClick(NavigationButtonClickEvent event) {
                        findAncestor(MainView.class).loanView(
                                DeviceTracker.get().getUser());
                    }
                });
        cssLayout.addComponent(loans);
        setContent(cssLayout);

        bindFields();
    }
    
    @Override
    public void attach() {
        super.attach();
        updateActiveLoans();
    }

    private void updateActiveLoans() {
        loans.removeAllComponents();
        List<Loan> myLoans = DeviceTracker.get().getMyActiveLoans();
        if (myLoans.isEmpty()) {
            loans.addComponent(new Label("No active loans"));
        } else {
            for (Loan loan : myLoans) {
                String cap = String.format("%s, from %s", loan.getDevice()
                        .getName(), loan.getStartDate());
                NavigationButton button = new NavigationButton(cap);
                button.addClickListener(new NavigationButtonClickListener() {
                    @Override
                    public void buttonClick(NavigationButtonClickEvent event) {
                        // TODO Auto-generated method stub
                    }
                });
                loans.addComponent(button);
            }
        }
        loans.addComponent(showAllLoansButton);
    }

    private void setButtonStates(boolean b) {
        saveButton.setEnabled(b);
        resetButton.setEnabled(b);
    }

    private void bindFields() {
        FieldGroup fieldGroup = new FieldGroup();
        fieldGroup.setBuffered(false);
        DeviceUser user = DeviceTracker.get().getUser();
        fieldGroup.setItemDataSource(new BeanItem<DeviceUser>(user));
        fieldGroup.bindMemberFields(this);
        setButtonStates(false);
    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        setButtonStates(true);
    }

    @Override
    public void buttonClick(ClickEvent event) {
        if (saveButton == event.getButton()) {
            DeviceTracker.get().saveUser();
            bindFields();
        } else if (resetButton == event.getButton()) {
            DeviceTracker.get().refreshUser();
            bindFields();
        }
    }

}
