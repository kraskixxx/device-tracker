package org.dt;

import org.dt.domain.Location;
import org.dt.util.BeanBinder;

import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.Popover;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TextField;

public class NewLocationView extends Popover implements ClickListener {
    private TextField name = new TextField();
    private Button ok = new Button("Add", this);
    private Button cancel = new Button("Cancel", this);
    private Location location;
    private LocationListView parent;

    public NewLocationView(Location l, LocationListView locationListView) {
        this.parent = locationListView;
        this.location = l;
        setWidth("320px");
        NavigationView navigationView = new NavigationView("New location");
        navigationView.setLeftComponent(cancel);
        navigationView.setRightComponent(ok);
        setContent(navigationView);
        navigationView.setContent(name);
        name.setWidth("100%");
        name.setInputPrompt("name for location");
        
        BeanBinder.bind(l, this);
    }

    @Override
    public void buttonClick(ClickEvent event) {
        removeFromParent();
        if(event.getButton() == ok) {
            location = DeviceTracker.get().saveOrPersist(location);
            parent.addAndOpenLocation(location);
        }
    }
}
