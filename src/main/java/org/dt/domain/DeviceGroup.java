package org.dt.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class DeviceGroup extends AbstractEntity {
    
    private String name;
    
    @OneToMany(mappedBy="deviceGroup")
    private List<Device> devices = new ArrayList<Device>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }
    
    @Override
    public String toString() {
        return name;
    }

}
