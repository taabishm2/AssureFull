package com.increff.assure.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.increff.assure.model.data.ChannelOrderReceiptData;
import com.increff.assure.model.data.ChannelSideOrderData;
import com.increff.assure.model.form.ChannelSideOrderForm;
import com.increff.assure.service.AbstractService;
import com.increff.assure.service.ApiException;
import com.increff.assure.service.ClientWrapper;
import com.increff.assure.util.PdfGenerateUtil;
import com.increff.assure.util.XmlGenerateUtil;
import model.data.OrderReceiptData;
import model.form.OrderForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class OrderDto {

    public void add(@RequestBody ChannelSideOrderForm orderForm) throws ApiException, JsonProcessingException {
        OrderForm serverSideOrderForm = ClientWrapper.convert(orderForm);
        //Autowire ClientWrapper
        ClientWrapper.hitAddOrderApi(serverSideOrderForm);
    }

    public ChannelSideOrderData get(@PathVariable Long id) throws ApiException {
        return ClientWrapper.hitGetOrderApi(id);
    }

    public void generateReceipt(OrderReceiptData orderReceiptData) throws ApiException {

        ChannelOrderReceiptData orderReceipt = ClientWrapper.convert(orderReceiptData);
        XmlGenerateUtil.generate(orderReceipt);
        PdfGenerateUtil.generate(orderReceipt.getOrderId());

        ClientWrapper.sendOrderInvoice(orderReceipt.getOrderId());
    }
}
