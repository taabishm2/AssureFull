package com.increff.assure.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.increff.assure.model.data.ChannelOrderReceiptData;
import com.increff.assure.model.data.ChannelAppOrderData;
import com.increff.assure.model.form.ChannelAppOrderForm;
import com.increff.assure.util.ConvertUtil;
import model.data.*;
import model.form.ChannelOrderForm;
import model.form.OrderForm;
import model.form.OrderItemValidationForm;
import model.form.OrderValidationForm;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class ClientWrapper {
    public static void hitAddOrderApi(ChannelOrderForm orderForm) throws ApiException {
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

        System.out.println("Sending request: "+jsonStr);
        HttpEntity<String> request = new HttpEntity<>(jsonStr, headers);
        restTemplate.postForObject("http://localhost:6060/assure/api/order/channel", request, String.class);
        System.out.println("Request Completed");
    }

    public static ChannelAppOrderData hitGetOrderApi(Long id) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = "http://localhost:6060/assure/api/order";
        return restTemplate.getForEntity(fooResourceUrl + "/" + id, ChannelAppOrderData.class).getBody();
    }

    public static void sendOrderInvoice(Long orderId) {
    }

    public static ChannelOrderForm convert(ChannelAppOrderForm channelOrderForm) throws ApiException {
        return ConvertUtil.convert(channelOrderForm, ChannelOrderForm.class);
    }

    public static ChannelOrderReceiptData convert(OrderReceiptData orderReceiptData) throws ApiException {
        return ConvertUtil.convert(orderReceiptData, ChannelOrderReceiptData.class);
    }

    public static void hitOrderValidationApi(OrderValidationForm validationForm) throws ApiException {
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
        restTemplate.postForObject("http://localhost:6060/assure/api/order/validate", request, String.class);
    }

    public static void hitOrderItemValidationApi(OrderItemValidationForm validationForm) throws ApiException {
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
        restTemplate.postForObject("http://localhost:6060/assure/api/orderitem/channel/validate", request, String.class);
    }

    public static List<OrderData> hitGetOrdersByChannelApi(Long channelId) throws ApiException {
        String urlGETList = "http://localhost:6060/assure/api/order/channel/"+channelId;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<OrderData[]> responseEntity = restTemplate.getForEntity(urlGETList, OrderData[].class);
        OrderData[] objects = responseEntity.getBody();
        MediaType contentType = responseEntity.getHeaders().getContentType();
        HttpStatus statusCode = responseEntity.getStatusCode();
        List<OrderData> orderDataList = Arrays.asList(objects);
        return ConvertUtil.convert(orderDataList, OrderData.class);
    }

    public static List<ConsumerData> hitGetClientsApi() {
        String urlGETList = "http://localhost:6060/assure/api/consumer/clients";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ConsumerData[]> responseEntity = restTemplate.getForEntity(urlGETList, ConsumerData[].class);
        ConsumerData[] objects = responseEntity.getBody();
        MediaType contentType = responseEntity.getHeaders().getContentType();
        HttpStatus statusCode = responseEntity.getStatusCode();
        return Arrays.asList(objects);
    }

    public static List<ChannelData> hitGetChannelsApi() {
        String urlGETList = "http://localhost:6060/assure/api/channel";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ChannelData[]> responseEntity = restTemplate.getForEntity(urlGETList, ChannelData[].class);
        ChannelData[] objects = responseEntity.getBody();
        MediaType contentType = responseEntity.getHeaders().getContentType();
        HttpStatus statusCode = responseEntity.getStatusCode();
        return Arrays.asList(objects);
    }

    public static List<OrderItemData> hitGetOrderItemsApi(Long orderId) {
        String urlGETList = "http://localhost:6060/assure/api/orderItem/orderId/"+orderId;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<OrderItemData[]> responseEntity = restTemplate.getForEntity(urlGETList, OrderItemData[].class);
        OrderItemData[] objects = responseEntity.getBody();
        MediaType contentType = responseEntity.getHeaders().getContentType();
        HttpStatus statusCode = responseEntity.getStatusCode();
        return Arrays.asList(objects);
    }
}
