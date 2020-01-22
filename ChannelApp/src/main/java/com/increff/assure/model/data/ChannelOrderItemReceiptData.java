package com.increff.assure.model.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelOrderItemReceiptData {
    Long orderItemId;
    String channelSkuId;
    Long quantity;
    Double mrp;
    Long total;
}