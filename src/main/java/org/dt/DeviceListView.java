package org.dt;

import com.vaadin.addon.touchkit.ui.NavigationManager;
import com.vaadin.ui.Component;

public class DeviceListView extends NavigationManager implements Component {
    
    public DeviceListView() {
        setCaption("Devices");
        setCurrentComponent(new LocationListView());
    }

}
