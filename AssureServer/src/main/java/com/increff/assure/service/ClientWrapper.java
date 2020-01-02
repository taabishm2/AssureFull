package com.increff.assure.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.increff.assure.pojo.OrderPojo;
import com.increff.assure.util.ConvertUtil;
import model.data.OrderData;
import model.data.OrderReceiptData;
import model.form.OrderForm;
import model.form.OrderItemForm;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import static com.increff.assure.util.ConvertUtil.convert;

public class ClientWrapper {
    public static void fetchInvoiceFromChannel(OrderReceiptData orderInvoiceData) throws ApiException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = mapper.writeValueAsString(orderInvoiceData);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<String>(jsonStr, headers);
        String response = restTemplate.postForObject("http://localhost:7070/channel/api/order/invoice", request, String.class);
    }
}
