package org.dt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.dt.domain.Device;
import org.dt.domain.DeviceUser;
import org.dt.domain.Loan;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.HorizontalAlign;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.PlotOptionsColumn;
import com.vaadin.addon.charts.model.Stacking;
import com.vaadin.addon.charts.model.VerticalAlign;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.Popover;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class ChartView extends Popover {

    public ChartView(JPAContainer<Loan> container, Button chartButton) {
        this.showRelativeTo(chartButton);
        
        Chart chart = new Chart(ChartType.COLUMN);
        Configuration configuration = chart.getConfiguration();
       configuration.setTitle((String) null);
        configuration.getyAxis().setTitle("Loans");
        PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
        plotOptionsColumn.setStacking(Stacking.NORMAL);
        configuration.setPlotOptions(plotOptionsColumn);
        configuration.setTitle("");
        
        Labels l = new Labels();
        l.setRotation(90);
        l.setAlign(HorizontalAlign.LEFT);
        configuration.getxAxis().setLabels(l);

        configuration.getLegend().setVerticalAlign(VerticalAlign.LOW);
        
        List<Device> devices = DeviceTracker.get().getDevices();
        List<String> categories = new ArrayList<String>();
        for (Device device : devices) {
            categories.add(device.getName() + " @" + device.getHomeLocation().getName());
        }
        configuration.getxAxis().setCategories(categories.toArray(new String[0]));
        List<DeviceUser> users = DeviceTracker.get().getUsers();
        HashMap<DeviceUser, Integer[]> userToDeviceLoans = new HashMap<DeviceUser, Integer[]>();
        
        for (DeviceUser deviceUser : users) {
            Integer[] a =  new Integer[devices.size()];
            for (int i = 0; i < a.length; i++) {
                a[i] = 0;
            }
            userToDeviceLoans.put(deviceUser, a);
        }
        Object itemId = container.firstItemId();
        while(itemId != null) {
            Loan entity = container.getItem(itemId).getEntity();
            Device device = entity.getDevice();
            Integer[] integers = userToDeviceLoans.get(entity.getLoaner());
            integers[devices.indexOf(device)]++;
            itemId = container.nextItemId(itemId);
        }
        
        for (Entry<DeviceUser, Integer[]> deviceUser : userToDeviceLoans.entrySet()) {
            DataSeries dataSeries = new DataSeries();
            dataSeries.setName(deviceUser.getKey().getName());
            dataSeries.setData(deviceUser.getValue());
            configuration.addSeries(dataSeries);
        }
        
        chart.setWidth( (30*devices.size() + 200) +"px");
        chart.setHeight("100%");
        
        
        NavigationView content = new NavigationView(chart);
        content.setCaption("Device usage");
        setContent(content);
        
        Button close = new Button("X");
        content.setRightComponent(close);
        close.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                removeFromParent();
            }
        });
        
        setSizeFull();

    }

}
