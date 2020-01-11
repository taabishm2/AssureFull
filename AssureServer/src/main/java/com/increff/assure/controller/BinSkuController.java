package com.increff.assure.controller;

import com.increff.assure.dto.BinSkuDto;
import com.increff.assure.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import model.data.BinSkuData;
import model.form.BinSkuForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api
@RestController
public class BinSkuController {
    @Autowired
    private BinSkuDto binSkuDto;

   @ApiOperation(value = "Add a Bin Inventory List")
    @RequestMapping(path = "/api/binSku/list/{clientId}", method = RequestMethod.POST)
    public void addList(@PathVariable Long clientId, @Valid @RequestBody List<BinSkuForm> formList) throws ApiException {
        binSkuDto.addList(formList, clientId);
    }

    @ApiOperation(value = "Get a Bin-wise-Inventory item by ID")
    @RequestMapping(path = "/api/binSku/{id}", method = RequestMethod.GET)
    public BinSkuData get(@PathVariable Long id) throws ApiException {
        return binSkuDto.get(id);
    }

    @ApiOperation(value = "Get list of all Bin-wise-Inventories")
    @RequestMapping(path = "/api/binSku", method = RequestMethod.GET)
    public List<BinSkuData> getAll() throws ApiException {
        return binSkuDto.getAll();
    }

    @ApiOperation(value = "Get list of all Bin-wise-Inventories")
    @RequestMapping(path = "/api/binSku/search", method = RequestMethod.POST)
    public List<BinSkuData> getSearchByBinAndProduct(@RequestBody BinSkuForm form) throws ApiException {
        return binSkuDto.getSearchByBinAndProduct(form.getBinId(), form.getClientSkuId());
    }
}
