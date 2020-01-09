package com.increff.assure.controller;

import com.increff.assure.dto.OrderItemDto;
import com.increff.assure.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import model.data.OrderItemData;
import model.form.OrderItemForm;
import model.form.OrderItemValidationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api
@RestController
public class OrderItemController {
    @Autowired
    private OrderItemDto orderItemDto;

    @ApiOperation(value = "Adds an Order-Item")
    @RequestMapping(path = "/api/orderItem", method = RequestMethod.POST)
    public void add(@Valid @RequestBody OrderItemForm form) throws ApiException {
        orderItemDto.add(form);
    }

    @ApiOperation(value = "Gets an Order-Item by ID")
    @RequestMapping(path = "/api/orderItem/{id}", method = RequestMethod.GET)
    public OrderItemData get(@PathVariable long id) throws ApiException {
        return orderItemDto.get(id);
    }

    @ApiOperation(value = "Gets list of all Order-Items")
    @RequestMapping(path = "/api/orderItem", method = RequestMethod.GET)
    public List<OrderItemData> getAll() throws ApiException {
        return orderItemDto.getAll();
    }

    @ApiOperation(value = "Validate Order Details")
    @RequestMapping(path = "/api/orderitem/validate", method = RequestMethod.POST)
    public void validateOrderItem(@RequestBody OrderItemValidationForm validationForm) throws ApiException {
        orderItemDto.validateOrderItemForm(validationForm);
    }
}
