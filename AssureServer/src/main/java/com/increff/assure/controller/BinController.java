package com.increff.assure.controller;

import com.increff.assure.dto.BinDto;
import com.increff.assure.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api
@RestController
public class BinController {
    @Autowired
    private BinDto binDto;

    @ApiOperation(value = "Create specified number of Bins")
    @RequestMapping(path = "/api/bin", method = RequestMethod.POST)
    public List<Long> add(@RequestBody Integer numberOfBins) throws ApiException {
        return binDto.add(numberOfBins);
    }

    @ApiOperation(value = "Get list of all Bin IDs")
    @RequestMapping(path = "/api/bin", method = RequestMethod.GET)
    public List<Long> getAll() {
        return binDto.getAll();
    }
}
