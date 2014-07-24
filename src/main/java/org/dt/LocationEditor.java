package org.dt;

import org.dt.domain.DeviceUser;
import org.dt.domain.Location;
import org.dt.util.BeanBinder;

import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.addon.touchkit.ui.HorizontalButtonGroup;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;

public class LocationEditor extends NavigationView implements ClickListener,
        ValueChangeListener, TextChangeListener {
    private Location location;

    private TextField name = new TextField("Name");
    private TextField lon = new TextField("Lon");
    private TextField lat = new TextField("Lat");
    private NativeSelect responsible = new NativeSelect("Responsible");
    private Button ok = new Button("Save", this);
    private Button cancel = new Button("Cancel", this);

    private Button setLocationFromMap = new Button("Define location from map", this);

    private HorizontalButtonGroup editButtons;

    private VerticalComponentGroup verticalComponentGroup;

    public LocationEditor(Location l) {
        this.location = l;
        setCaption("Edit location");

        editButtons = new HorizontalButtonGroup();
        cancel.setStyleName("red");
        ok.setStyleName("green");
        editButtons.addComponents(cancel, ok);
        setRightComponent(editButtons);

        verticalComponentGroup = new VerticalComponentGroup();
        configureFieldAndAttach(name, responsible, lon, lat);
        setLocationFromMap.setWidth("100%");
        verticalComponentGroup.addComponent(setLocationFromMap);
        setContent(verticalComponentGroup);

        responsible.setIcon(FontAwesome.USER);
        responsible.setContainerDataSource(DeviceTracker.get()
                .getUserContainer());
        responsible.setConverter(new SingleSelectConverter<DeviceUser>(
                responsible));
        responsible.setItemCaptionPropertyId("name");
        bindForm();
    }

    private void configureFieldAndAttach(Field<?>... fs) {
        for (Field<?> f : fs) {
            f.setWidth("100%");
            f.addValueChangeListener(this);
            if (f instanceof AbstractField) {
                AbstractField<?> af = (AbstractField<?>) f;
                af.setImmediate(true);
            }
            if (f instanceof TextField) {
                TextField tf = (TextField) f;
                tf.addTextChangeListener(this);
            }
        }
        verticalComponentGroup.addComponents(fs);
    }

    @Override
    public void buttonClick(ClickEvent event) {
        if (event.getButton() == setLocationFromMap) {
            getUI().addWindow(new LocationPicker(lon,lat));
            return;
        }
        if (event.getButton() == ok) {
            try {
                location = DeviceTracker.get().saveOrPersist(location);
            } catch (Exception e) {
                e.printStackTrace();
                Notification.show("Update failed!", Type.ERROR_MESSAGE);
                location = DeviceTracker.get().refresh(location);
            }
        } else {
            location = DeviceTracker.get().refresh(location);
        }
        bindForm();
        LocationView previousComponent = (LocationView) getNavigationManager()
                .getPreviousComponent();
        previousComponent.updateLocation();
    }

    private void bindForm() {
        BeanBinder.bind(location, this);
        setFormEdited(false);
    }

    private void setFormEdited(boolean b) {
        editButtons.setVisible(b);
        getNavigationBar().getLeftComponent().setEnabled(!b);
    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        setFormEdited(true);
    }

    @Override
    public void textChange(TextChangeEvent event) {
        setFormEdited(true);
    }

}
