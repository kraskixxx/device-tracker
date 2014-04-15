package org.dt.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2013-02-22T10:05:19.798+0200")
@StaticMetamodel(AbstractLocation.class)
public class AbstractLocation_ extends AbstractEntity_ {
	public static volatile SingularAttribute<AbstractLocation, String> name;
	public static volatile SingularAttribute<AbstractLocation, Double> lat;
	public static volatile SingularAttribute<AbstractLocation, Double> lon;
}
