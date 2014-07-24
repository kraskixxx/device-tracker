package org.dt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dt.domain.Device;
import org.dt.domain.DeviceGroup;
import org.dt.domain.Location;

import com.vaadin.addon.touchkit.ui.NavigationButton;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickEvent;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickListener;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;

public class LocationView extends NavigationView implements Component,
        ClickListener {

    private Map<Device, NavigationButton> deviceToButton = new HashMap<Device, NavigationButton>();
    private Map<DeviceGroup, VerticalComponentGroup> groupToComponentGroup = new HashMap<DeviceGroup, VerticalComponentGroup>();
    private CssLayout layout;

    private Button newDevice = new Button("Add device", this);
    private Location location;
    private LocationListView parent;

    public LocationView(Location location, LocationListView parent) {
        this.parent = parent;
        setRightComponent(newDevice);
        buildView(location);
        updateLocation();
    }

    private void buildView(final Location location) {
        this.location = DeviceTracker.get().refresh(location);
        layout = new CssLayout();

        VerticalComponentGroup verticalComponentGroup = new VerticalComponentGroup();
        NavigationButton nb = new NavigationButton("Location details");
        nb.setIcon(FontAwesome.COGS);
        nb.addClickListener(new NavigationButtonClickListener() {
            @Override
            public void buttonClick(NavigationButtonClickEvent event) {
                getNavigationManager().navigateTo(new LocationEditor(location));
            }
        });
        verticalComponentGroup.addComponent(nb);
        layout.addComponent(verticalComponentGroup);

        Map<DeviceGroup, List<Device>> grouped = new HashMap<DeviceGroup, List<Device>>();
        List<Device> devices = location.getDevices();
        for (Device device : devices) {
            List<Device> list = grouped.get(device.getDeviceGroup());
            if (list == null) {
                list = new ArrayList<Device>();
                grouped.put(device.getDeviceGroup(), list);
            }
            list.add(device);
        }

        for (Entry<DeviceGroup, List<Device>> e : grouped.entrySet()) {
            DeviceGroup group = e.getKey();

            VerticalComponentGroup vcg = getVerticalComponentGroup(group);
            for (final Device d : e.getValue()) {
                NavigationButton b = createNavigationButtonToDevice(d);
                vcg.addComponent(b);
            }
        }

        setContent(layout);
    }

    private NavigationButton createNavigationButtonToDevice(final Device d) {
        NavigationButton b = new NavigationButton(d.getName());
        deviceToButton.put(d, b);
        if (d.getLoan() != null) {
            b.setDescription("@" + d.getLoan().getLoaner());
        }
        b.addClickListener(new NavigationButtonClickListener() {
            @Override
            public void buttonClick(NavigationButtonClickEvent event) {
                navigateTo(d);
            }

        });
        return b;
    }

    private void navigateTo(final Device d) {
        getNavigationManager().navigateTo(
                new DeviceDetailView(d, LocationView.this));
    }

    private VerticalComponentGroup getVerticalComponentGroup(DeviceGroup group) {
        VerticalComponentGroup vcg = groupToComponentGroup.get(group);
        if (vcg == null) {
            vcg = new VerticalComponentGroup();
            if (group == null) {
                vcg.setCaption("Others");
            } else {
                vcg.setCaption(group.getName());
            }
            layout.addComponent(vcg);
            groupToComponentGroup.put(group, vcg);
        }
        return vcg;
    }

    public void update(Device device) {
        NavigationButton navigationButton = deviceToButton.get(device);
        if (!device.getHomeLocation().equals(location)) {
            // detach if no more in this location.
            VerticalComponentGroup parent2 = (VerticalComponentGroup) navigationButton
                    .getParent();
            parent2.removeComponent(navigationButton);
        } else {
            if (navigationButton == null) {
                navigationButton = createNavigationButtonToDevice(device);
                getVerticalComponentGroup(device.getDeviceGroup())
                        .addComponent(navigationButton);
            }
            String description = device.getLoan() == null ? null : "@"
                    + device.getLoan().getLoaner();
            navigationButton.setDescription(description);
            navigationButton.setCaption(device.getName());

            // Ensure in correct category
            VerticalComponentGroup g = getVerticalComponentGroup(device
                    .getDeviceGroup());
            if (!g.equals(navigationButton.getParent())) {
                g.addComponent(navigationButton);
            }
        }
        updateLocation();
    }

    @Override
    public void buttonClick(ClickEvent event) {
        if (event.getButton() == newDevice) {
            Device device = new Device();
            device.setHomeLocation(location);
            navigateTo(device);
        }
    }

    public void updateLocation() {
        setCaption(location.getName());
        if (isConnectorEnabled()) {
            parent.updateLocation(location);
        }
    }
}
