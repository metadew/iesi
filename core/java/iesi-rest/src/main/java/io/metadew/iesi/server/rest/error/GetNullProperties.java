package io.metadew.iesi.server.rest.error;

import org.springframework.stereotype.Repository;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.TreeMap;

@Repository
public class GetNullProperties {
	
	public Map<String, Object> getNullProperties(final Object object) {
	    final Map<String, Object> NullProperties = new TreeMap<String, Object>();
	    try {
	        final BeanInfo beanInfo = Introspector.getBeanInfo(object
	                .getClass());
	        for (final PropertyDescriptor descriptor : beanInfo
	                .getPropertyDescriptors()) {
	            try {
	                final Object propertyValue = descriptor.getReadMethod()
	                        .invoke(object);
	                if (propertyValue == null) {
	                	throw new SqlNotFoundException();
	                }
	            } catch (final IllegalArgumentException e) {
	            	e.printStackTrace();
	            } catch (final IllegalAccessException e) {
	            	e.printStackTrace();
	            } catch (final InvocationTargetException e) {
	            	e.printStackTrace();
	            }
	        }
	    } catch (final IntrospectionException e) {
	    	e.printStackTrace();
	    }
	    return NullProperties;
	}
}
