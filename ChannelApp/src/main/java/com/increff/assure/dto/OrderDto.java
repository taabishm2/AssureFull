package com.increff.assure.dto;

import com.increff.assure.model.data.ChannelOrderData;
import com.increff.assure.model.data.ChannelOrderReceiptData;
import com.increff.assure.service.ApiException;
import com.increff.assure.service.ClientWrapper;
import com.increff.assure.util.PdfGenerateUtil;
import com.increff.assure.util.XmlGenerateUtil;
import model.data.*;
import model.form.ChannelOrderForm;
import model.form.OrderItemValidationForm;
import model.form.OrderValidationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class OrderDto {
    @Autowired
    private ClientWrapper clientWrapper;

    public void add(@RequestBody ChannelOrderForm orderForm) throws ApiException {
        clientWrapper.addOrder(orderForm);
    }

    public byte[] generateReceipt(OrderReceiptData orderReceiptData) throws ApiException {
        ChannelOrderReceiptData orderReceipt = clientWrapper.convert(orderReceiptData);
        return PdfGenerateUtil.generate(XmlGenerateUtil.generate(orderReceipt), orderReceipt.getOrderId());
    }

    public void validateOrderForm(OrderValidationForm validationForm) throws ApiException {
        clientWrapper.validateOrder(validationForm);
    }

    public void validateOrderItemForm(OrderItemValidationForm validationForm) throws ApiException {
        clientWrapper.validateOrderItem(validationForm);
    }

    public List<ConsumerData> getAllClients() {
        return clientWrapper.getClients();
    }

    public List<ChannelData> getAllChannels() {
        return clientWrapper.getChannels();
    }

    public List<OrderData> getByChannel(Long channelId) throws ApiException {
        return clientWrapper.getOrdersByChannel(channelId);
    }

    public List<OrderItemData> getByOrderId(Long orderId) {
        return clientWrapper.getOrderItems(orderId);
    }
}
