package com.increff.assure.controller;

import com.increff.assure.dto.BinDto;
import com.increff.assure.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api
@RestController
public class BinApiController {
    @Autowired
    private BinDto binDto;

    //TODO: Fix Sample CSV Download resource reference
    @ApiOperation(value = "Create specified number of Bins")
    @RequestMapping(path = "/api/bin", method = RequestMethod.POST)
    public ArrayList<Long> add(@RequestBody int numberOfBins) throws ApiException {
        return binDto.add(numberOfBins);
    }

    @ApiOperation(value = "Get list of all Bin IDs")
    @RequestMapping(path = "/api/bin", method = RequestMethod.GET)
    public List<Long> getAll() throws ApiException {
        return binDto.getAll();
    }

    @ApiOperation(value = "Get list of all Bin IDs")
    @RequestMapping(path = "/api/bin/test", method = RequestMethod.GET)
    public String test() throws ApiException {
        return binDto.httpTest()+"<h2>Test is working</h2>";
    }
}
