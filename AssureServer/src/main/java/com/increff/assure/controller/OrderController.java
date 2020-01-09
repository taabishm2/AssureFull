package com.increff.assure.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.increff.assure.dto.OrderDto;
import com.increff.assure.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import model.data.OrderData;
import model.form.OrderForm;
import model.form.OrderItemValidationForm;
import model.form.OrderValidationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api
@RestController
public class OrderController {

    @Autowired
    private OrderDto orderDto;

    @ApiOperation(value = "Adds an Order")
    @RequestMapping(path = "/api/order", method = RequestMethod.POST)
    public void add(@Valid @RequestBody OrderForm form) throws ApiException {
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
    public void generateReceipt(@PathVariable Long id) throws ApiException, JsonProcessingException {
        orderDto.fulfillOrder(id);
    }

    @ApiOperation(value = "Validate Order Details")
    @RequestMapping(path = "/api/order/validate", method = RequestMethod.POST)
    public void validateOrder(@RequestBody OrderValidationForm validationForm) throws ApiException {
        orderDto.validateOrderForm(validationForm);
    }
}

