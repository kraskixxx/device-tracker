package org.dt;

import org.vaadin.addon.leaflet.LeafletClickEvent;
import org.vaadin.addon.leaflet.LeafletClickListener;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.Popover;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TextField;

public class LocationPicker extends Popover {

    public LocationPicker(final TextField lon, final TextField lat) {
        setSizeFull();
        NavigationView navigationView = new NavigationView("Select new location");
        Button button = new Button("Cancel");
        button.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                removeFromParent();
            }
        });
        navigationView.setLeftComponent(button);
        DeviceTrackerMap deviceTrackerMap = new DeviceTrackerMap();
        navigationView.setContent(deviceTrackerMap);
        deviceTrackerMap.addClickListener(new LeafletClickListener() {
            @Override
            public void onClick(LeafletClickEvent event) {
                Point point = event.getPoint();
                lon.setValue(""+point.getLon());
                lat.setValue(""+point.getLat());
                removeFromParent();
            }
        });
        setContent(navigationView);
    }
}
