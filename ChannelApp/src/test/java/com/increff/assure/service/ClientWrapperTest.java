package com.increff.assure.service;

import model.data.OrderReceiptData;
import model.form.ChannelOrderForm;
import model.form.ChannelOrderItemForm;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class ClientWrapperTest extends AbstractUnitTest {

    @Autowired
    ClientWrapper clientWrapper;
    @Mock
    RestTemplate mockRestTemplate;

    @Test
    public void testAddOrder() {
        clientWrapper.setRestTemplate(mockRestTemplate);
        Mockito.doNothing().when(mockRestTemplate).postForObject(Mockito.anyString(), Mockito.any(HttpEntity.class), String.class);

        List<ChannelOrderItemForm> orderItems = new ArrayList<>();
        orderItems.add(TestForm.getChannelOrderItemForm("CHSKU1", 10L));
        orderItems.add(TestForm.getChannelOrderItemForm("CHSKU2", 20L));
        orderItems.add(TestForm.getChannelOrderItemForm("CHSKU3", 30L));

        TestForm.getChannelOrderForm(12L, 45L, 89L, "CHOID1", orderItems);
    }

}