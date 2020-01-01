package com.increff.assure.service;

import com.increff.assure.util.NullAwarePropertyCopy;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

@Service
public class AbstractService {
    public void checkNull(Object object, String message) throws ApiException {
        if (Objects.nonNull(object))
            throw new ApiException(message);
    }

    public void checkNotNull(Object object, String message) throws ApiException {
        if (Objects.isNull(object))
            throw new ApiException(message);
    }

    public void copySourceToDestination(Object destinationObject, Object sourceObject) throws ApiException {
        BeanUtilsBean beanUtil = new NullAwarePropertyCopy();
        try {
            beanUtil.copyProperties(destinationObject, sourceObject);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new ApiException("Error in copying properties using [NullAwarePropertyCopy]");
        }
    }
}
