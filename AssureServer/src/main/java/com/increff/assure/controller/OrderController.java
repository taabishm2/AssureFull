package com.increff.assure.controller;

import com.increff.assure.dto.OrderDto;
import com.increff.assure.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import model.data.OrderData;
import model.data.OrderItemData;
import model.form.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Api
@RestController
public class OrderController {

    @Autowired
    private OrderDto orderDto;

    @ApiOperation(value = "Adds an Order")
    @RequestMapping(path = "/api/order", method = RequestMethod.POST)
    public void add(@RequestBody OrderForm form) throws ApiException {
        orderDto.add(form);
    }

    @ApiOperation(value = "Adds an Order from Channel")
    @RequestMapping(path = "/api/order/channel", method = RequestMethod.POST)
    public void addChannelOrder(@RequestBody ChannelOrderForm form) throws ApiException {
        orderDto.add(form);
    }

    @ApiOperation(value = "Gets an Order by ID")
    @RequestMapping(path = "/api/order/{id}", method = RequestMethod.GET)
    public OrderData get(@PathVariable Long id) throws ApiException {
        return orderDto.get(id);
    }

    @ApiOperation(value = "Gets list of all Orders")
    @RequestMapping(path = "/api/order", method = RequestMethod.GET)
    public List<OrderData> getAll() throws ApiException {
        return orderDto.getAll();
    }

    @ApiOperation(value = "Allocate Order")
    @RequestMapping(path = "/api/order/allocate/{id}", method = RequestMethod.POST)
    public void allocate(@PathVariable Long id) throws ApiException {
        orderDto.runAllocation(id);
    }

    @ApiOperation(value = "Generate Order Invoice")
    @RequestMapping(path = "/api/order/invoice/{id}", method = RequestMethod.POST)
    public String generateInvoice(@PathVariable Long id) throws ApiException, IOException {
        return orderDto.fulfillOrder(id);
    }

    @ApiOperation(value = "Validate Order Details")
    @RequestMapping(path = "/api/order/validate", method = RequestMethod.POST)
    public void validateOrder(@RequestBody OrderValidationForm validationForm) throws ApiException {
        orderDto.validateOrderForm(validationForm);
    }

    @ApiOperation(value = "Gets list of all Orders for given Channel")
    @RequestMapping(path = "/api/order/channel/{channelId}", method = RequestMethod.GET)
    public List<OrderData> getByChannel(@PathVariable Long channelId) throws ApiException {
        return orderDto.getByChannel(channelId);
    }

    @ApiOperation(value = "Validate Order Details")
    @RequestMapping(path = "/api/order/orderItems/validate/{clientId}/{channelId}", method = RequestMethod.POST)
    public void validateOrderItems(@PathVariable Long clientId, @PathVariable Long channelId, @RequestBody List<OrderItemForm> validationForm) throws ApiException {
        orderDto.validateList(validationForm, clientId, channelId);
    }

    @ApiOperation(value = "Search Orders")
    @RequestMapping(path = "/api/order/search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderData> getSearch(@RequestBody OrderSearchForm form) throws ApiException {
        return orderDto.getSearch(form);
    }

    @ApiOperation(value = "Validate Channel Order Details")
    @RequestMapping(path = "/api/order/orderitem/channel/validate", method = RequestMethod.POST)
    public void validateChannelOrderItem(@RequestBody OrderItemValidationForm validationForm) throws ApiException {
        orderDto.validateChannelOrderItemForm(validationForm);
    }

    @ApiOperation(value = "Gets list of all Order-Items")
    @RequestMapping(path = "/api/order/orderItem/orderId/{orderId}", method = RequestMethod.GET)
    public List<OrderItemData> getAll(@PathVariable Long orderId) throws ApiException {
        return orderDto.getByOrderId(orderId);
    }
}