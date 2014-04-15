package org.dt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dt.domain.Device;
import org.dt.domain.Location;

import com.vaadin.addon.touchkit.ui.NavigationButton;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickEvent;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickListener;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;

public class LocationListView extends NavigationView implements Component,
        ClickListener {

    private Button newLocation = new Button("+", this);
    private Button refresh = new Button("Refresh", this);
    private VerticalComponentGroup verticalComponentGroup = new VerticalComponentGroup();
    private Map<Location, NavigationButton> locationToButton = new HashMap<Location, NavigationButton>();

    public LocationListView() {
        setCaption("Locations");
        setRightComponent(newLocation);
        setLeftComponent(refresh);
        setContent(verticalComponentGroup);
        listLocations();
    }

    private void listLocations() {
        locationToButton.clear();
        verticalComponentGroup.removeAllComponents();
        List<Location> homeLocations = DeviceTracker.get().getLocations();
        for (final Location location : homeLocations) {
            addLocation(location);
        }
    }

    private void addLocation(final Location location) {
        NavigationButton navigationButton = new NavigationButton();
        locationToButton.put(location, navigationButton);
        updateLocation(location);
        navigationButton.addClickListener(new NavigationButtonClickListener() {
            @Override
            public void buttonClick(NavigationButtonClickEvent event) {
                navigateTo(location);
            }
        });
        verticalComponentGroup.addComponent(navigationButton);
    }

    void addAndOpenLocation(final Location location) {
        addLocation(location);
        navigateTo(location);
    }

    private void navigateTo(final Location location) {
        getNavigationManager().navigateTo(new LocationView(location, this));
    }

    @Override
    public void buttonClick(ClickEvent event) {
        if (event.getButton() == newLocation) {
            NewLocationView locationEditorView = new NewLocationView(
                    new Location(), this);
            locationEditorView.showRelativeTo(newLocation);
        } else if (event.getButton() == refresh) {
            listLocations();
        }
    }

    public void updateLocation(Location location) {
        NavigationButton navigationButton = locationToButton.get(location);
        navigationButton.setCaption(location.getName());
        int used = 0;
        List<Device> devices = location.getDevices();
        for (Device device : devices) {
            if (device.getLoan() != null) {
                used++;
            }
        }
        navigationButton.setDescription(used + "/" + devices.size() + " ");
    }

}
