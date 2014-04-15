package org.dt.domain;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Entity
public class Location extends AbstractLocation {
    
    private DeviceUser responsible;
    
    @OneToMany(mappedBy = "homeLocation", fetch = FetchType.EAGER)
    private List<Device> devices = new ArrayList<Device>();
    
    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public DeviceUser getResponsible() {
        return responsible;
    }

    public void setResponsible(DeviceUser responsible) {
        this.responsible = responsible;
    }

}
