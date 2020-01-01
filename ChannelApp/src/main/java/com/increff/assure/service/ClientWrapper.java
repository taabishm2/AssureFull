package com.increff.assure.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.increff.assure.model.data.ChannelOrderReceiptData;
import com.increff.assure.model.data.ChannelSideOrderData;
import com.increff.assure.model.form.ChannelSideOrderForm;
import com.increff.assure.util.ConvertUtil;
import model.data.OrderReceiptData;
import model.form.OrderForm;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class ClientWrapper {
    public static void hitAddOrderApi(OrderForm serverSideOrderForm) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = mapper.writeValueAsString(serverSideOrderForm);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<String>(jsonStr, headers);
        String personResultAsJsonStr = restTemplate.postForObject("http://localhost:6060/assure/api/order", request, String.class);
    }

    public static ChannelSideOrderData hitGetOrderApi(Long id) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = "http://localhost:6060/assure/api/order";
        return restTemplate.getForEntity(fooResourceUrl + "/" + id, ChannelSideOrderData.class).getBody();
    }

    public static void sendOrderInvoice(Long orderId) {
    }

    public static OrderForm convert(ChannelSideOrderForm channelOrderForm) throws ApiException {
        return ConvertUtil.convert(channelOrderForm, OrderForm.class);
    }

    public static ChannelOrderReceiptData convert(OrderReceiptData orderReceiptData) throws ApiException {
        return ConvertUtil.convert(orderReceiptData, ChannelOrderReceiptData.class);
    }
}
