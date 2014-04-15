package org.dt.util;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;

public class BeanBinder {
    
    @SuppressWarnings("unchecked")
    public static FieldGroup bind(Object bean, Object fieldSource) {
        @SuppressWarnings("rawtypes")
        BeanFieldGroup beanFieldGroup = new BeanFieldGroup(bean.getClass());
        beanFieldGroup.setBuffered(false);
        beanFieldGroup.setItemDataSource(bean);
        beanFieldGroup.buildAndBindMemberFields(fieldSource);
        return beanFieldGroup;
    }

}
