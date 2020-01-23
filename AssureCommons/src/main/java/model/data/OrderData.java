package model.data;

import lombok.Getter;
import lombok.Setter;
import model.OrderStatus;
import model.form.OrderForm;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;

@Getter
@Setter
public class OrderData extends OrderForm {
    private Long id;

    private OrderStatus status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime dateCreated;

    private String clientName;
    private String customerName;
    private String channelName;
}
