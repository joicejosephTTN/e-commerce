package com.ttn.ecommerce.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

public class FilterProperties {

    // method that creates a list of property names to ignore if they are null in source object;
    // and that uses this list to pass as parameter in BeanUtils.copyProperties
    public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for(PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        return emptyNames.toArray(new String[0]);
    }

    public static boolean comparePropertyNames(Object source1, Object source2) {
        final BeanWrapper src1 = new BeanWrapperImpl(source1);
        PropertyDescriptor[] pds1 = src1.getPropertyDescriptors();

        final BeanWrapper src2 = new BeanWrapperImpl(source2);
        PropertyDescriptor[] pds2 = src2.getPropertyDescriptors();

        if (pds1.length != pds2.length) {
            return false;
        }

        Set<String> propertyNamesSource1 = new HashSet<>();
        for(PropertyDescriptor pd : pds1) {
            Object srcValue = src1.getPropertyValue(pd.getName());
            propertyNamesSource1.add(pd.getName());
        }

        Set<String> propertyNamesSource2 = new HashSet<>();
        for(PropertyDescriptor pd : pds2) {
            Object srcValue = src2.getPropertyValue(pd.getName());
            propertyNamesSource1.add(pd.getName());
        }

        if (propertyNamesSource1.containsAll(propertyNamesSource2) == false)
            return false;

        return true;
    }
}
