package model.form;

import lombok.Getter;
import lombok.Setter;
import model.OrderStatus;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.*;
import java.util.List;

@Getter
@Setter
public class OrderForm {
    @NotNull
    private Long clientId;

    @NotNull
    private Long customerId;

    @NotNull
    private Long channelId;

    @Size(min=1, max=255)
    private String channelOrderId;

    @NotNull
    private List<OrderItemForm> orderItemList;
}
