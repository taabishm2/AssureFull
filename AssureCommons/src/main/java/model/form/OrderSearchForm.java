package model.form;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;

@Getter
@Setter
public class OrderSearchForm {
    private Long clientId;

    private Long customerId;

    private Long channelId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private ZonedDateTime fromDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private ZonedDateTime toDate;
}
