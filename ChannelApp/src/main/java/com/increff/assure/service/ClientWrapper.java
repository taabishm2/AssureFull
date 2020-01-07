package com.increff.assure.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.increff.assure.model.data.ChannelOrderReceiptData;
import com.increff.assure.model.data.ChannelAppOrderData;
import com.increff.assure.model.form.ChannelAppOrderForm;
import com.increff.assure.util.ConvertUtil;
import model.data.OrderReceiptData;
import model.form.OrderForm;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ClientWrapper {
    public static void hitAddOrderApi(OrderForm orderForm) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = mapper.writeValueAsString(orderForm);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //TODO: Change to fetch order status not via String
        HttpEntity<String> request = new HttpEntity<String>(jsonStr, headers);
        restTemplate.postForObject("http://localhost:6060/assure/api/order", request, String.class);
    }

    public static ChannelAppOrderData hitGetOrderApi(Long id) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = "http://localhost:6060/assure/api/order";
        return restTemplate.getForEntity(fooResourceUrl + "/" + id, ChannelAppOrderData.class).getBody();
    }

    public static void sendOrderInvoice(Long orderId) {
    }

    public static OrderForm convert(ChannelAppOrderForm channelOrderForm) throws ApiException {
        return ConvertUtil.convert(channelOrderForm, OrderForm.class);
    }

    public static ChannelOrderReceiptData convert(OrderReceiptData orderReceiptData) throws ApiException {
        return ConvertUtil.convert(orderReceiptData, ChannelOrderReceiptData.class);
    }
}
