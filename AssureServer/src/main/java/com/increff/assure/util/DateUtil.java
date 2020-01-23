package com.increff.assure.util;

import com.increff.assure.service.ApiException;

import java.time.ZonedDateTime;
import java.util.Objects;

public class DateUtil {
    public static void checkDateFilters(ZonedDateTime fromDate, ZonedDateTime toDate) throws ApiException {
        if (Objects.nonNull(fromDate))
            if (fromDate.isAfter(ZonedDateTime.now()))
                throw new ApiException("Invalid \"From\" Date");

        if (Objects.nonNull(toDate))
            if (fromDate.isAfter(ZonedDateTime.now()))
                throw new ApiException("Invalid \"To\" Date");

        if (Objects.nonNull(fromDate) && Objects.nonNull(toDate))
            if (fromDate.isAfter(toDate))
                throw new ApiException("\"From\" date cannot be after \"To\" date");
    }
}
