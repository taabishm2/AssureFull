package com.increff.assure.service;

import model.form.ChannelOrderForm;
import model.form.ChannelOrderItemForm;

import java.util.List;

public class TestForm {
    public static ChannelOrderForm getChannelOrderForm(Long channelId, Long clientId, Long customerId, String channelOrderId,
                                                       List<ChannelOrderItemForm> orderItemList){
        ChannelOrderForm form = new ChannelOrderForm();
        form.setChannelId(channelId);
        form.setClientId(clientId);
        form.setCustomerId(customerId);
        form.setChannelOrderId(channelOrderId);
        form.setOrderItemList(orderItemList);
        return form;
    }

    public static ChannelOrderItemForm getChannelOrderItemForm(String channelSkuId, Long orderedQuantity){
        ChannelOrderItemForm form = new ChannelOrderItemForm();
        form.setChannelSkuId(channelSkuId);
        form.setOrderedQuantity(orderedQuantity);
        return form;
    }
}