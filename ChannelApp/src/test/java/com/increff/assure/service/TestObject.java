package com.increff.assure.service;

import model.OrderStatus;
import model.data.OrderData;
import model.data.OrderItemData;
import model.form.ChannelOrderForm;
import model.form.ChannelOrderItemForm;
import model.form.OrderItemForm;

import java.util.List;

public class TestObject {
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

    public static OrderData getOrderDataObject(String clientName, String customerName, String channelName,
                                               List<OrderItemForm> orderItems, String channelOrderId, OrderStatus status){
        OrderData object = new OrderData();
        object.setClientName(clientName);
        object.setCustomerName(customerName);
        object.setChannelName(channelName);
        object.setOrderItemList(orderItems);
        object.setChannelOrderId(channelOrderId);
        object.setStatus(status);
        return object;
    }

    public static OrderItemForm getOrderItemForm(String clientSkuId, Long orderedQunatity) {
        OrderItemForm form = new OrderItemForm();
        form.setClientSkuId(clientSkuId);
        form.setOrderedQuantity(orderedQunatity);
        return form;
    }
}