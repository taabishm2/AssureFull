package com.increff.assure.model.data;

import com.increff.assure.model.form.ChannelAppOrderForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelOrderData extends ChannelAppOrderForm {
    private Long id;

    private String createdAt;
}
