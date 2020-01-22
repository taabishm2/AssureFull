package com.increff.assure.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.increff.assure.model.data.ChannelOrderItemReceiptData;
import com.increff.assure.model.data.ChannelOrderReceiptData;
import com.increff.assure.model.data.ChannelOrderData;
import com.increff.assure.model.form.ChannelAppOrderForm;
import com.increff.assure.util.ConvertUtil;
import model.data.*;
import model.form.ChannelOrderForm;
import model.form.OrderItemValidationForm;
import model.form.OrderValidationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ClientWrapper {
    @Autowired
    RestTemplate restTemplate;

    @Value("${assure.server.url}")
    private String assureServerUrl;

    public void addOrder(ChannelOrderForm orderForm) throws ApiException {
        ObjectMapper mapper = new ObjectMapper();

        String jsonStr;
        try {
            jsonStr = mapper.writeValueAsString(orderForm);
        } catch (JsonProcessingException e) {
            throw new ApiException(e.getMessage());
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(jsonStr, headers);
        restTemplate.postForObject(assureServerUrl + "/api/order/channel", request, String.class);
    }

    public ChannelOrderData getOrder(Long id) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = assureServerUrl +  "/api/order";
        return restTemplate.getForEntity(fooResourceUrl + "/" + id, ChannelOrderData.class).getBody();
    }

    public void sendOrderInvoice(Long orderId) {
    }

    public ChannelOrderForm convert(ChannelAppOrderForm channelOrderForm) throws ApiException {
        return ConvertUtil.convert(channelOrderForm, ChannelOrderForm.class);
    }

    public ChannelOrderReceiptData convert(OrderReceiptData orderReceiptData) throws ApiException {
        ChannelOrderReceiptData channelInvoice = ConvertUtil.convert(orderReceiptData, ChannelOrderReceiptData.class);
        List<ChannelOrderItemReceiptData> channelInvoiceItemList = new ArrayList<>();
        for(OrderItemReceiptData invoiceItem:orderReceiptData.getOrderItems()) {
            ChannelOrderItemReceiptData channelInvoiceItem = ConvertUtil.convert(invoiceItem, ChannelOrderItemReceiptData.class);
            channelInvoiceItem.setChannelSkuId(invoiceItem.getChannelSkuId());
            channelInvoiceItemList.add(channelInvoiceItem);
        }
        channelInvoice.setOrderItems(channelInvoiceItemList);
        return channelInvoice;
    }

    public void validateOrder(OrderValidationForm validationForm) throws ApiException {
        ObjectMapper mapper = new ObjectMapper();

        String jsonStr;
        try {
            jsonStr = mapper.writeValueAsString(validationForm);
        } catch (JsonProcessingException e) {
            throw new ApiException(e.getMessage());
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<String>(jsonStr, headers);
        restTemplate.postForObject(assureServerUrl + "/api/order/validate", request, String.class);
    }

    public void validateOrderItem(OrderItemValidationForm validationForm) throws ApiException {
        ObjectMapper mapper = new ObjectMapper();

        String jsonStr;
        try {
            jsonStr = mapper.writeValueAsString(validationForm);
        } catch (JsonProcessingException e) {
            throw new ApiException(e.getMessage());
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<String>(jsonStr, headers);
        restTemplate.postForObject(assureServerUrl + "/api/orderitem/channel/validate", request, String.class);
    }

    public List<OrderData> getOrdersByChannel(Long channelId) throws ApiException {
        String urlGETList = assureServerUrl + "/api/order/channel/"+channelId;
        System.out.println("SERVER:"+urlGETList);
        ResponseEntity<OrderData[]> responseEntity = restTemplate.getForEntity(urlGETList, OrderData[].class);
        OrderData[] objects = responseEntity.getBody();
        MediaType contentType = responseEntity.getHeaders().getContentType();
        HttpStatus statusCode = responseEntity.getStatusCode();
        List<OrderData> orderDataList = Arrays.asList(objects);
        return ConvertUtil.convert(orderDataList, OrderData.class);
    }

    public List<ConsumerData> getClients() {
        String urlGETList = assureServerUrl + "/api/consumer/clients";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ConsumerData[]> responseEntity = restTemplate.getForEntity(urlGETList, ConsumerData[].class);
        ConsumerData[] objects = responseEntity.getBody();
        MediaType contentType = responseEntity.getHeaders().getContentType();
        HttpStatus statusCode = responseEntity.getStatusCode();
        return Arrays.asList(objects);
    }

    public List<ChannelData> getChannels() {
        String urlGETList = assureServerUrl + "/api/channel";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ChannelData[]> responseEntity = restTemplate.getForEntity(urlGETList, ChannelData[].class);
        ChannelData[] objects = responseEntity.getBody();
        MediaType contentType = responseEntity.getHeaders().getContentType();
        HttpStatus statusCode = responseEntity.getStatusCode();
        return Arrays.asList(objects);
    }

    public List<OrderItemData> getOrderItems(Long orderId) {
        String urlGETList = assureServerUrl + "/api/orderItem/orderId/"+orderId;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<OrderItemData[]> responseEntity = restTemplate.getForEntity(urlGETList, OrderItemData[].class);
        OrderItemData[] objects = responseEntity.getBody();
        MediaType contentType = responseEntity.getHeaders().getContentType();
        HttpStatus statusCode = responseEntity.getStatusCode();
        return Arrays.asList(objects);
    }
}