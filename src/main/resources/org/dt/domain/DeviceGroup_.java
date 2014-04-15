package org.dt.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-02-22T10:05:19.811+0200")
@StaticMetamodel(DeviceGroup.class)
public class DeviceGroup_ extends AbstractEntity_ {
	public static volatile SingularAttribute<DeviceGroup, String> name;
	public static volatile ListAttribute<DeviceGroup, Device> devices;
}
