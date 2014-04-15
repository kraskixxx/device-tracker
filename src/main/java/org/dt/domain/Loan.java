package org.dt.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Loan extends AbstractEntity {
            
    private Device device;

    private DeviceUser loaner;
    
    private String comment;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate = new Date();
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
        device.setLoan(this);
    }

    public DeviceUser getLoaner() {
        return loaner;
    }

    public void setLoaner(DeviceUser loaner) {
        this.loaner = loaner;
    }
    
}
