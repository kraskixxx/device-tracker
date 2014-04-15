package org.dt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import net.glxn.qrgen.QRCode;

import org.apache.commons.io.IOUtils;
import org.dt.domain.Device;
import org.dt.domain.DeviceGroup;
import org.dt.domain.Loan;
import org.dt.domain.Location;
import org.dt.util.BeanBinder;

import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.addon.touchkit.ui.HorizontalButtonGroup;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public class DeviceDetailView extends NavigationView implements Component,
        ClickListener, ValueChangeListener {

    private Label statusLabel = new Label();
    private Button loanButton = new Button("Loan this device", this);
    private Device device;
    private LocationView parent;

    private TextField name = new TextField("Name");
    private TextArea notes = new TextArea("Notes");
    private NativeSelect deviceGroup = new NativeSelect("Group");
    private NativeSelect homeLocation = new NativeSelect("Location");
    private Button saveButton = new Button("Save", this);
    private Button cancelButton = new Button("Cancel", this);
    private Button delete = new Button("Delete this device");
    private HorizontalButtonGroup commitButtons;
    private VerticalComponentGroup detailsForm;
    private VerticalComponentGroup status;

    Image img = new Image("QRCode to this page", new StreamResource(
            new StreamSource() {

                @Override
                public InputStream getStream() {
                    return new ByteArrayInputStream(QRCode.from("Hello World")
                            .stream().toByteArray());
                }
            }, "qrcode.png"));

    public DeviceDetailView(Device d, LocationView parent) {
        this.parent = parent;
        this.device = DeviceTracker.get().refresh(d);
        setCaption(d.getName());

        CssLayout l = new CssLayout();

        l.addComponent(new Image("QRCode to this page", new StreamResource(
                new StreamSource() {

                    @Override
                    public InputStream getStream() {
                        return new ByteArrayInputStream(QRCode
                                .from("Hello World").stream().toByteArray());
                    }
                }, "qrcode.png")));

        buildStatus(l);

        buildForm();

        l.addComponent(detailsForm);

        VerticalComponentGroup vcg = new VerticalComponentGroup();
        vcg.setCaption("Loan history");
        vcg.addComponent(new Label("TODO graph + last n loans"));
        l.addComponent(vcg);

        delete.setStyleName("red");
        l.addComponent(delete);

        setContent(l);
    }

    private void buildStatus(CssLayout l) {
        status = new VerticalComponentGroup();
        status.setCaption("Status");
        status.addComponent(statusLabel);
        l.addComponent(status);
        l.addComponent(loanButton);
        updateStatus();
    }

    private void buildForm() {
        detailsForm = new VerticalComponentGroup();
        detailsForm.setCaption("Device details");
        name.addValueChangeListener(this);
        name.setImmediate(true);
        name.setNullRepresentation("");
        name.setInputPrompt("type name here");
        name.setWidth("100%");
        notes.addValueChangeListener(this);
        notes.setImmediate(true);
        notes.setNullRepresentation("");
        notes.setWidth("100%");
        homeLocation.setNullSelectionAllowed(false);
        homeLocation.addValueChangeListener(this);
        homeLocation.setImmediate(true);
        homeLocation.setWidth("100%");
        homeLocation.setContainerDataSource(DeviceTracker.get()
                .getLocationsContainer());
        homeLocation.setConverter(new SingleSelectConverter<Location>(
                homeLocation));
        homeLocation.setItemCaptionPropertyId("name");
        deviceGroup.addValueChangeListener(this);
        deviceGroup.setImmediate(true);
        deviceGroup.setWidth("100%");
        deviceGroup.setContainerDataSource(DeviceTracker.get()
                .getDeviceGroupContainer());
        deviceGroup.setConverter(new SingleSelectConverter<DeviceGroup>(
                deviceGroup));
        deviceGroup.setItemCaptionPropertyId("name");
        CssLayout photo = new CssLayout();
        photo.setCaption("Photo");
        photo.addComponent(new Label("TODO"));
        detailsForm
                .addComponents(name, photo, homeLocation, deviceGroup, notes);

        commitButtons = new HorizontalButtonGroup();
        saveButton.addStyleName("green");
        cancelButton.addStyleName("red");
        commitButtons.addComponents(cancelButton, saveButton);
        setRightComponent(commitButtons);

        bindForm();
    }

    private void bindForm() {
        BeanBinder.bind(device, this);
        commitButtons.setVisible(false);
        getNavigationBar().getLeftComponent().setEnabled(true);
    }

    private void updateStatus() {
        status.setVisible(device.getId() != null);
        Loan loan = device.getLoan();
        if (loan == null) {
            loanButton.setCaption("Loan this device");
            statusLabel.setValue("Device is free");
        } else {
            statusLabel.setValue(String.format(
                    "@ %s since %s, TODO editor for loan details", loan
                            .getLoaner().getName(), device.getLoan()
                            .getStartDate()));
            loanButton.setCaption("Mark as returned");
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        if (event.getButton() == loanButton) {
            updateLoan();
        } else if (event.getButton() == saveButton) {
            saveOrUpdate();
        } else if (event.getButton() == cancelButton) {
            refresh();
        } else if (event.getButton() == delete) {
            Notification.show("TODO!");
        }
    }

    private void refresh() {
        device = DeviceTracker.get().refresh(device);
        bindForm();
    }

    private void saveOrUpdate() {
        try {
            device = DeviceTracker.get().saveOrPersist(device);
            parent.update(device);
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Saving failed!", Type.ERROR_MESSAGE);
            refresh();
        }
        updateStatus();
        bindForm();
    }

    private void updateLoan() {
        try {
            if (device.getLoan() == null) {
                DeviceTracker.get().loan(device);
            } else {
                DeviceTracker.get().endLoan(device);
            }
        } catch (Exception e) {
            Notification.show("Loan status change failed!", Type.ERROR_MESSAGE);
            refresh();
        }
        updateStatus();
        parent.update(device);
    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        commitButtons.setVisible(true);
        getNavigationBar().getLeftComponent().setEnabled(false);
    }

}
