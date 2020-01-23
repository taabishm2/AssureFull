package com.increff.assure.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.OrderStatus;
import model.data.OrderData;
import model.form.ChannelOrderForm;
import model.form.ChannelOrderItemForm;
import model.form.OrderItemForm;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


public class ClientWrapperTest extends AbstractUnitTest {

    @Autowired
    ClientWrapper clientWrapper;
    @Mock
    RestTemplate mockRestTemplate;

    private ChannelOrderForm orderForm;
    private List<OrderItemForm> orderItems;
    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);

        List<ChannelOrderItemForm> channelOrderItems = new ArrayList<>();
        channelOrderItems.add(TestObject.getChannelOrderItemForm("CHSKU1", 10L));
        channelOrderItems.add(TestObject.getChannelOrderItemForm("CHSKU2", 20L));
        channelOrderItems.add(TestObject.getChannelOrderItemForm("CHSKU3", 30L));

        orderItems = new ArrayList<>();
        orderItems.add(TestObject.getOrderItemForm("OCSKU1", 19L));
        orderItems.add(TestObject.getOrderItemForm("OCSKU2", 10L));

        orderForm = TestObject.getChannelOrderForm(12L, 45L, 89L, "CHOID1", channelOrderItems);
    }

    @Test
    public void testGetHttpRequestForOrderForm() throws ApiException, IOException {
        HttpEntity<String> httpRequest = clientWrapper.getHttpRequest(orderForm);

        ChannelOrderForm form = new ObjectMapper().readValue(httpRequest.getBody(), ChannelOrderForm.class);
        assertEquals(orderForm.getChannelId(), form.getChannelId());
    }

    @Test
    public void testAddOrder() throws ApiException {
        clientWrapper.setRestTemplate(mockRestTemplate);
        Mockito.doReturn(null).when(mockRestTemplate).postForObject(Mockito.anyString(), Mockito.any(HttpEntity.class), Matchers.eq(String.class));

        clientWrapper.addOrder(orderForm);
    }

    @Test
    public void testGetOrdersByChannel() throws ApiException, JsonProcessingException {
        OrderData[] orderList = {
                TestObject.getOrderDataObject("CL","CU","CH",
                orderItems, "COID", OrderStatus.CREATED),
                TestObject.getOrderDataObject("CL1","CU1","CH",
                        orderItems, "COID1", OrderStatus.ALLOCATED)};

        clientWrapper.setRestTemplate(mockRestTemplate);
        Mockito.when(mockRestTemplate.getForEntity(Mockito.anyString(), Matchers.eq(OrderData[].class))).thenReturn(getResponseEntity(orderList));

        List<OrderData> ordersByChannel = clientWrapper.getOrdersByChannel(123L);
        assertEquals(2, ordersByChannel.size());
    }

    public static <T> ResponseEntity<T> getResponseEntity(T object) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = mapper.writeValueAsString(object);

        return new ResponseEntity<T>(object, headers, HttpStatus.CREATED);
    }
}
