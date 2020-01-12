package com.increff.assure.model.form;

import lombok.Getter;
import lombok.Setter;
import model.OrderStatus;
import model.form.OrderItemForm;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class ChannelAppOrderForm {
    @NotNull
    private Long clientId;

    @NotNull
    private Long customerId;

    @NotNull
    private Long channelId;

    @NotEmpty
    private String channelOrderId;

    @NotNull
    private List<OrderItemForm> orderItemList;
}