package com.increff.assure.dto;

import com.increff.assure.model.data.ChannelAppOrderData;
import com.increff.assure.model.data.ChannelOrderItemReceiptData;
import com.increff.assure.model.data.ChannelOrderReceiptData;
import com.increff.assure.model.form.ChannelAppOrderForm;
import com.increff.assure.service.ApiException;
import com.increff.assure.service.ClientWrapper;
import com.increff.assure.util.PdfGenerateUtil;
import com.increff.assure.util.XmlGenerateUtil;
import model.data.*;
import model.form.ChannelOrderForm;
import model.form.OrderItemValidationForm;
import model.form.OrderValidationForm;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class OrderDto {

    public void add(@RequestBody ChannelAppOrderForm orderForm) throws ApiException {
        ChannelOrderForm serverOrderForm = ClientWrapper.convert(orderForm);
        ClientWrapper.hitAddOrderApi(serverOrderForm);
    }

    public ChannelAppOrderData get(@PathVariable Long id) throws ApiException {
        return ClientWrapper.hitGetOrderApi(id);
    }

    public void generateReceipt(OrderReceiptData orderReceiptData) throws ApiException {
        ChannelOrderReceiptData orderReceipt = ClientWrapper.convert(orderReceiptData);

        XmlGenerateUtil.generate(orderReceipt);
        PdfGenerateUtil.generate(orderReceipt.getOrderId());
    }

    public void validateOrder(OrderValidationForm validationForm) throws ApiException {
        ClientWrapper.hitOrderValidationApi(validationForm);
    }

    public void validateOrderItemForm(OrderItemValidationForm validationForm) throws ApiException {
        ClientWrapper.hitOrderItemValidationApi(validationForm);
    }

    public List<ConsumerData> getAllClients() {
        return ClientWrapper.hitGetClientsApi();
    }

    public List<ChannelData> getAllChannels() {
        return ClientWrapper.hitGetChannelsApi();
    }

    public List<OrderData> getByChannel(Long channelId) throws ApiException {
        return ClientWrapper.hitGetOrdersByChannelApi(channelId);
    }

    public List<OrderItemData> getByOrderId(Long orderId) {
        return ClientWrapper.hitGetOrderItemsApi(orderId);
    }
}
