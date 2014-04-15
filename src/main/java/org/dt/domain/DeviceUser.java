package org.dt.domain;

import javax.persistence.Entity;

@Entity
public class DeviceUser extends AbstractLocation {
    
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
