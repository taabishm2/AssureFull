package com.increff.assure.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.increff.assure.dto.OrderDto;
import com.increff.assure.model.data.ChannelOrderData;
import com.increff.assure.model.form.ChannelAppOrderForm;
import com.increff.assure.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import model.data.*;
import model.form.ChannelOrderForm;
import model.form.OrderItemValidationForm;
import model.form.OrderValidationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api
@RestController
public class ChannelOrderController {

    @Autowired
    private OrderDto orderDto;

    @ApiOperation(value = "Adds an Order")
    @RequestMapping(path = "/api/order", method = RequestMethod.POST)
    public void add(@Valid @RequestBody ChannelOrderForm form) throws ApiException, JsonProcessingException {
        orderDto.add(form);
    }


    @ApiOperation(value = "Gets all Orders for a Channel")
    @RequestMapping(path = "/api/order/channel/{channelId}", method = RequestMethod.GET)
    public List<OrderData>  getByChannel(@PathVariable Long channelId) throws ApiException {
        return orderDto.getByChannel(channelId);
    }

    @ApiOperation(value = "Validate Order Details")
    @RequestMapping(path = "/api/order/validate", method = RequestMethod.POST)
    public void validate(@RequestBody OrderValidationForm validationForm) throws ApiException {
        orderDto.validateOrderForm(validationForm);
    }

    @ApiOperation(value = "Validate Order Item Details")
    @RequestMapping(path = "/api/order/orderitem/validate", method = RequestMethod.POST)
    public void validateOrderItem(@RequestBody OrderItemValidationForm validationForm) throws ApiException {
        orderDto.validateOrderItemForm(validationForm);
    }

    @ApiOperation(value = "Generate Order Invoice")
    @RequestMapping(path = "/api/order/invoice", method = RequestMethod.POST)
    public void generateReceipt(@RequestBody OrderReceiptData orderReceiptData) throws ApiException {
        orderDto.generateReceipt(orderReceiptData);
    }

    @ApiOperation(value = "Gets a list of all Clients")
    @RequestMapping(path = "/api/order/clients", method = RequestMethod.GET)
    public List<ConsumerData> getAllClients() throws ApiException {
        return orderDto.getAllClients();
    }

    @ApiOperation(value = "Gets a list of all Channels")
    @RequestMapping(path = "/api/order/channels", method = RequestMethod.GET)
    public List<ChannelData> getAllChannels() throws ApiException {
        return orderDto.getAllChannels();
    }

    @ApiOperation(value = "Gets list of all Order-Items")
    @RequestMapping(path = "/api/order/orderId/{orderId}", method = RequestMethod.GET)
    public List<OrderItemData> getAllByOrderId(@PathVariable Long orderId) throws ApiException {
        return orderDto.getByOrderId(orderId);
    }
}