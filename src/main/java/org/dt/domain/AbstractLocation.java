package org.dt.domain;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Size;

@MappedSuperclass
public class AbstractLocation extends AbstractEntity {

    @Size(min=2,max=30)
    private String name;
    private Double lat;
    private Double lon;
    
    public AbstractLocation() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return name == null ? super.toString() : name;
    }

}