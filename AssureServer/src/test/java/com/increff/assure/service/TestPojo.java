package com.increff.assure.service;

import com.increff.assure.pojo.*;
import model.ConsumerType;
import model.InvoiceType;

public class TestPojo {
    public static BinPojo getBinPojo() {
        return new BinPojo();
    }

    public static BinSkuPojo getBinInventoryPojo(Long globalSku, Long binId, Long quantity) {
        BinSkuPojo binSkuPojo = new BinSkuPojo();
        binSkuPojo.setGlobalSkuId(globalSku);
        binSkuPojo.setBinId(binId);
        binSkuPojo.setQuantity(quantity);
        return binSkuPojo;
    }

    public static ChannelListingPojo getChannelListingPojo(Long globalSku, Long channelId, String channelSkuId, Long clientId) {
        ChannelListingPojo channelListing = new ChannelListingPojo();
        channelListing.setGlobalSkuId(globalSku);
        channelListing.setChannelId(channelId);
        channelListing.setChannelSkuId(channelSkuId);
        channelListing.setClientId(clientId);
        return channelListing;
    }

    public static ChannelPojo getChannelPojo(String name, InvoiceType type) {
        ChannelPojo channel = new ChannelPojo();
        channel.setName(name);
        channel.setInvoiceType(type);
        return channel;
    }

    public static ConsumerPojo getConsumerPojo(String name, ConsumerType type) {
        ConsumerPojo consumer = new ConsumerPojo();
        consumer.setName(name);
        consumer.setType(type);
        return consumer;
    }

    public static InventoryPojo getConstructInventory(Long globalSku, Long availableQty, Long allocatedQty, Long fulfilledQty) {
        InventoryPojo inventory = new InventoryPojo();
        inventory.setGlobalSkuId(globalSku);
        inventory.setAvailableQuantity(availableQty);
        inventory.setAllocatedQuantity(allocatedQty);
        inventory.setFulfilledQuantity(fulfilledQty);
        return inventory;
    }

    public static OrderItemPojo getConstructOrderItem(Long globalSku, Long orderId, Long orderedQty, Long allocatedQty, Long fulfilledQty) {
        OrderItemPojo orderItemPojo = new OrderItemPojo();
        orderItemPojo.setGlobalSkuId(globalSku);
        orderItemPojo.setOrderId(orderId);
        orderItemPojo.setOrderedQuantity(orderedQty);
        orderItemPojo.setAllocatedQuantity(allocatedQty);
        orderItemPojo.setFulfilledQuantity(fulfilledQty);
        return orderItemPojo;
    }

    public static OrderPojo getOrderPojo(Long customerId, Long clientId, Long channelId, String channelOrderId) {
        OrderPojo order = new OrderPojo();
        order.setCustomerId(customerId);
        order.setClientId(clientId);
        order.setChannelId(channelId);
        order.setChannelOrderId(channelOrderId);
        return order;
    }

    public static ProductMasterPojo getProductPojo(String name, Long clientId, String brandId, Double mrp, String clientSkuId, String description) {
        ProductMasterPojo product = new ProductMasterPojo();
        product.setName(name);
        product.setClientId(clientId);
        product.setBrandId(brandId);
        product.setMrp(mrp);
        product.setClientSkuId(clientSkuId);
        product.setDescription(description);
        return product;
    }
}