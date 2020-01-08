package com.increff.assure.controller;

import com.increff.assure.dto.ConsumerDto;
import com.increff.assure.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import model.data.ConsumerData;
import model.form.ConsumerForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api
@RestController
public class ConsumerController {
    @Autowired
    ConsumerDto consumerDto;

    @ApiOperation(value = "Add a Consumer")
    @RequestMapping(path = "/api/consumer", method = RequestMethod.POST)
    public void add(@Valid @RequestBody ConsumerForm consumerForm) throws ApiException {
        consumerDto.add(consumerForm);
    }

    @ApiOperation(value = "Gets a Consumer by ID")
    @RequestMapping(path = "/api/consumer/{id}", method = RequestMethod.GET)
    public ConsumerData get(@PathVariable Long id) throws ApiException {
        return consumerDto.get(id);
    }

    @ApiOperation(value = "Gets list of all Consumers")
    @RequestMapping(path = "/api/consumer", method = RequestMethod.GET)
    public List<ConsumerData> getAll() throws ApiException {
        return consumerDto.getAll();
    }

    @ApiOperation(value = "Gets a list of all Clients")
    @RequestMapping(path = "/api/consumer/clients", method = RequestMethod.GET)
    public List<ConsumerData> getAllClients() throws ApiException {
        return consumerDto.getAllClients();
    }
}