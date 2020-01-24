package com.increff.assure.util;

import com.increff.assure.service.ApiException;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class DateUtil {
    private static final String dateFormat = "MM/dd/yyyy HH:mm:ss";

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

        if (ChronoUnit.MONTHS.between(toDate, fromDate) > 1)
            throw new ApiException("Date range should be lesser than 1 month");
    }

    public static String getDateFormat() {
        return dateFormat;
    }

    public static ZonedDateTime[] setStartEndDates(ZonedDateTime startDate, ZonedDateTime endDate) {
        if (Objects.isNull(startDate) && Objects.isNull(endDate)) {
            endDate = ZonedDateTime.now();
            startDate = endDate.minusMonths(1L);
        } else if (Objects.isNull(startDate) ^ Objects.isNull(endDate)) {
            if (Objects.nonNull(startDate)) {
                endDate = startDate.plusMonths(1L);
            } else {
                startDate = endDate.minusMonths(1L);
            }
        }
        return new ZonedDateTime[]{startDate, endDate};
    }
}
