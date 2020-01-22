package com.increff.assure.util;

import com.increff.assure.service.ApiException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

public class DateTimeUtil {
    public static <T> T convert(Object sourceObject, Class<T> destinationClass) throws ApiException {
        try {
            T destinationObject = destinationClass.getDeclaredConstructor().newInstance();
            copyProperties(destinationObject, sourceObject);
            return destinationObject;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new ApiException("Conversion Error in [ConvertUtil.java]");
        }
    }

    public static <T> List <T> convert(List<?> sourceObjectList, Class<T> destinationClass) throws ApiException {
        List<T> destinationObjectList = new ArrayList<>();
        for (Object sourceObject : sourceObjectList) {
            destinationObjectList.add(convert(sourceObject, destinationClass));
        }
        return destinationObjectList;
    }
}