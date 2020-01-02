package com.increff.assure.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.increff.assure.dto.OrderDto;
import com.increff.assure.model.data.ChannelSideOrderData;
import com.increff.assure.model.form.ChannelSideOrderForm;
import com.increff.assure.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import model.OrderStatus;
import model.data.OrderReceiptData;
import model.form.OrderItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Api
@RestController
public class OrderApiController {

    @Autowired
    private OrderDto orderDto;

    @ApiOperation(value = "Adds an Order")
    @RequestMapping(path = "/api/order", method = RequestMethod.POST)
    public void add(@Valid @RequestBody ChannelSideOrderForm form) throws ApiException, JsonProcessingException {
        orderDto.add(form);
    }

    @ApiOperation(value = "Gets an Order by ID")
    @RequestMapping(path = "/api/order/{id}", method = RequestMethod.GET)
    public ChannelSideOrderData get(@PathVariable Long id) throws ApiException {
        return orderDto.get(id);
    }


    @ApiOperation(value = "Generate Order Invoice")
    @RequestMapping(path = "/api/order/invoice", method = RequestMethod.POST)
    public void generateReceipt(@RequestBody OrderReceiptData orderReceiptData) throws ApiException {
        orderDto.generateReceipt(orderReceiptData);
    }
}
