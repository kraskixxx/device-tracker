package org.dt.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-03-01T11:06:18.152+0200")
@StaticMetamodel(Device.class)
public class Device_ extends AbstractEntity_ {
	public static volatile SingularAttribute<Device, String> name;
	public static volatile SingularAttribute<Device, String> notes;
	public static volatile SingularAttribute<Device, Loan> loan;
	public static volatile SingularAttribute<Device, DeviceGroup> deviceGroup;
	public static volatile SingularAttribute<Device, Location> homeLocation;
}
