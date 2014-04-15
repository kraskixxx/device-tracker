package org.dt;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.dt.domain.DeviceUser;
import org.dt.domain.Location;
import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LeafletClickEvent;
import org.vaadin.addon.leaflet.LeafletClickListener;
import org.vaadin.addon.leaflet.shared.Bounds;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.Popover;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnHeaderMode;

public class MapView extends NavigationView implements Component, ClickListener {

    private DeviceTrackerMap map = new DeviceTrackerMap();

    private Button refresh = new Button("Refresh", this);
    private Button users = new Button("Users", this);

    private Map<DeviceUser, LMarker> userToMarker = new HashMap<DeviceUser, LMarker>();

    private UserSelector userSelector;

    private class UserSelector extends Popover {
        public UserSelector() {
            NavigationView navigationView = new NavigationView("Select visible users");
            final JPAContainer<DeviceUser> c = DeviceTracker.get()
                    .getUserContainer();
            final Table table = new Table();
            table.addValueChangeListener(new ValueChangeListener() {
                
                @Override
                public void valueChange(ValueChangeEvent event) {
                    @SuppressWarnings("unchecked")
                    Collection<Object> value = new HashSet<Object>((Collection<Object>) table.getValue());
                    Collection<DeviceUser> orphaned = new HashSet<DeviceUser>(userToMarker.keySet());
                    for (Object itemId : value) {
                        final DeviceUser entity = c.getItem(itemId).getEntity();
                        if(!userToMarker.containsKey(entity)) {
                            LMarker lMarker = new LMarker(entity.getLat(), entity
                                    .getLon());
                            lMarker.setIcon(new ThemeResource("../dt/user-pin.png"));
                            lMarker.setIconSize(new Point(40, 40));
                            lMarker.setIconAnchor(new Point(10, 40));
                            lMarker.addClickListener(new LeafletClickListener() {
                                @Override
                                public void onClick(LeafletClickEvent event) {
                                    Notification.show("That's " + entity.getName());
                                }
                            });
                            userToMarker.put(entity, lMarker);
                            map.addComponent(lMarker);                           
                        }
                    }
                    for (DeviceUser du : orphaned) {
                        LMarker remove = userToMarker.remove(du);
                        map.removeComponent(remove);
                    }
                }
            });
            table.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
            table.setContainerDataSource(c);
            table.setVisibleColumns(new Object[] { "name" });
            table.setMultiSelect(true);
            table.setSelectable(true);
            table.setImmediate(true);
            table.setSizeFull();
            navigationView.setContent(table);
            setContent(navigationView);
            setWidth("50%");
            setHeight("50%");
        }
    }

    public MapView() {
        setCaption("Map");
        setRightComponent(refresh);
        setLeftComponent(users);

    }

    @Override
    public void attach() {
        super.attach();
        buildView();
    }

    private void buildView() {
        setContent(map);
        refresh();
    }

    private void refresh() {
        addLocations();
        userSelector = null;
        userToMarker.clear();
    }

    private void addLocations() {
        map.removeAllComponents();
        Bounds bounds = null;
        List<Location> locations = DeviceTracker.get().getLocations();
        for (final Location location : locations) {
            if (location.getLat() != null) {
                LMarker lMarker = new LMarker(location.getLat(),
                        location.getLon());
                lMarker.setIcon(new ThemeResource("../dt/location-pin.png"));
                lMarker.setIconSize(new Point(40, 40));
                lMarker.setIconAnchor(new Point(10, 40));
                map.addComponent(lMarker);
                lMarker.addClickListener(new LeafletClickListener() {
                    @Override
                    public void onClick(LeafletClickEvent event) {
                        Notification.show(location.toString());
                    }
                });
                if (bounds == null) {
                    bounds = new Bounds(lMarker.getPoint());
                }
                bounds.extend(lMarker.getPoint());
            }
        }
        map.zoomToExtent(bounds);
        map.setZoomLevel(2);
    }

    @Override
    public void buttonClick(ClickEvent event) {
        if(event.getButton() == refresh) {
            refresh();
        } else if(event.getButton() == users) {
            if(userSelector == null) {
                userSelector = new UserSelector();
            }
            userSelector.showRelativeTo(users);
        }
    }

}
