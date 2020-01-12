package com.increff.assure.dto;

import com.increff.assure.pojo.*;
import model.ConsumerType;
import model.InvoiceType;
import model.form.*;

import java.util.List;

public class FormConstructor {
    public static BinPojo getConstructBin() {
        return new BinPojo();
    }

    public static BinSkuForm getConstructBinSku(Long globalSku, Long binId, Long quantity) {
        BinSkuForm binSkuForm = new BinSkuForm();
        binSkuForm.setGlobalSkuId(globalSku);
        binSkuForm.setBinId(binId);
        binSkuForm.setQuantity(quantity);
        return binSkuForm;
    }

    public static ChannelListingPojo getConstructChannelListing(Long globalSku, Long channelId, String channelSkuId) {
        ChannelListingPojo channelListing = new ChannelListingPojo();
        channelListing.setGlobalSkuId(globalSku);
        channelListing.setChannelId(channelId);
        channelListing.setChannelSkuId(channelSkuId);
        return channelListing;
    }

    public static ChannelForm getConstructChannel(String name, InvoiceType type) {
        ChannelForm channel = new ChannelForm();
        channel.setName(name);
        channel.setInvoiceType(type);
        return channel;
    }

    public static ConsumerForm getConstructConsumer(String name, ConsumerType type) {
        ConsumerForm consumer = new ConsumerForm();
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

    public static OrderItemForm getConstructOrderItem(Long globalSku, Long orderedQty) {
        OrderItemForm orderItemPojo = new OrderItemForm();
        orderItemPojo.setGlobalSkuId(globalSku);
        orderItemPojo.setOrderedQuantity(orderedQty);
        return orderItemPojo;
    }

    public static OrderForm getConstructOrder(Long customerId, Long clientId, Long channelId, String channelOrderId, List<OrderItemForm> orderItemFormList) {
        OrderForm order = new OrderForm();
        order.setCustomerId(customerId);
        order.setClientId(clientId);
        order.setChannelId(channelId);
        order.setChannelOrderId(channelOrderId);
        order.setOrderItemList(orderItemFormList);
        return order;
    }

    public static ProductMasterForm getConstructProduct(String name, Long clientId, String brandId, Double mrp, String clientSkuId, String description) {
        ProductMasterForm product = new ProductMasterForm();
        product.setName(name);
        product.setBrandId(brandId);
        product.setMrp(mrp);
        product.setClientSkuId(clientSkuId);
        product.setDescription(description);
        return product;
    }
}