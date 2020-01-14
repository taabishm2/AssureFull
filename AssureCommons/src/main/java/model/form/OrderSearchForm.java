package model.form;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
public class OrderSearchForm {
    private Long clientId;

    private Long customerId;

    private Long channelId;

    private String fromDate;

    private String toDate;
}
