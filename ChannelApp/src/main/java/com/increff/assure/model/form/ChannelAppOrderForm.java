package com.increff.assure.model.form;

import lombok.Getter;
import lombok.Setter;
import model.form.OrderItemForm;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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

    @NotNull
    @Size(min=1, max=255)
    private String channelOrderId;

    @NotNull
    @NotEmpty
    private List<ChannelOrderItemForm> orderItemList;
}
