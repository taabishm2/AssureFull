package com.increff.assure.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.increff.assure.dto.OrderDto;
import com.increff.assure.model.data.ChannelAppOrderData;
import com.increff.assure.model.form.ChannelAppOrderForm;
import com.increff.assure.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import model.data.OrderReceiptData;
import model.form.OrderItemValidationForm;
import model.form.OrderValidationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api
@RestController
public class OrderApiController {

    @Autowired
    private OrderDto orderDto;

    @ApiOperation(value = "Adds an Order")
    @RequestMapping(path = "/api/order", method = RequestMethod.POST)
    public void add(@Valid @RequestBody ChannelAppOrderForm form) throws ApiException, JsonProcessingException {
        orderDto.add(form);
    }

    @ApiOperation(value = "Gets all Orders for channel")
    @RequestMapping(path = "/api/order/{channelId}", method = RequestMethod.GET)
    public List<ChannelAppOrderData> getAll(@PathVariable Long channelId) throws ApiException {
        return orderDto.getAll(channelId);
    }

    @ApiOperation(value = "Gets an Order by ID")
    @RequestMapping(path = "/api/order/{id}", method = RequestMethod.GET)
    public ChannelAppOrderData get(@PathVariable Long id) throws ApiException {
        return orderDto.get(id);
    }

    @ApiOperation(value = "Validate Order Details")
    @RequestMapping(path = "/api/order/validate", method = RequestMethod.POST)
    public void validate(@RequestBody OrderValidationForm validationForm) throws ApiException {
        orderDto.validateOrder(validationForm);
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
}
