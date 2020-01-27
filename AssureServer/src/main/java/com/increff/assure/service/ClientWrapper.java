package com.increff.assure.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.data.ChannelInvoiceResponse;
import model.data.OrderReceiptData;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ClientWrapper {
    public String fetchInvoiceFromChannel(OrderReceiptData orderInvoiceData) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = mapper.writeValueAsString(orderInvoiceData);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<String>(jsonStr, headers);

        ChannelInvoiceResponse a = restTemplate.postForObject("http://localhost:7070/channel/api/order/invoice", request, ChannelInvoiceResponse.class);
        return a.getData();
    }
}