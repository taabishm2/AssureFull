package model.data;

import lombok.Getter;
import lombok.Setter;
import model.OrderStatus;
import model.form.OrderForm;

import java.time.ZonedDateTime;

@Getter
@Setter
public class OrderData extends OrderForm {
    private Long id;

    private OrderStatus status;

    private String dateCreated;

    private String clientName;
    private String customerName;
    private String channelName;
}
