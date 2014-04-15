package org.dt.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

@Entity
public class Device extends AbstractEntity {
    
    @Size(max=30)
    private String name;
    
    @Size(max=1024)
    private String notes;
    
    private Loan loan;
    
    @ManyToOne
    private DeviceGroup deviceGroup;
    
    @ManyToOne
    private Location homeLocation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeviceGroup getDeviceGroup() {
        return deviceGroup;
    }

    public void setDeviceGroup(DeviceGroup deviceGroup) {
        if(this.deviceGroup != null) {
            this.deviceGroup.getDevices().remove(this);
        }
        this.deviceGroup = deviceGroup;
        if(deviceGroup != null) {
            deviceGroup.getDevices().add(this);
        }
    }

    public Location getHomeLocation() {
        return homeLocation;
    }

    public void setHomeLocation(Location homeLocation) {
        if(this.homeLocation != null) {
            this.homeLocation.getDevices().remove(this);
        }
        this.homeLocation = homeLocation;
        if(homeLocation != null) {
            homeLocation.getDevices().add(this);
        }
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Override
    public String toString() {
        return name + " @ " +  homeLocation;
    }

}
