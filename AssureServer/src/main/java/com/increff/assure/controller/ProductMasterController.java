package com.increff.assure.controller;

import com.increff.assure.dto.ProductMasterDto;
import com.increff.assure.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import model.data.MessageData;
import model.data.ProductMasterData;
import model.form.ProductMasterForm;
import model.form.ProductUpdateForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api
@RestController
public class ProductMasterController {
    @Autowired
    private ProductMasterDto productMasterDto;

    @ApiOperation(value = "Add a List of Products")
    @RequestMapping(path = "/api/product/list/{clientId}", method = RequestMethod.POST)
    public void addList(@PathVariable Long clientId, @Valid @RequestBody List<ProductMasterForm> formList) throws ApiException {
        productMasterDto.addList(formList, clientId);
    }

    @ApiOperation(value = "Get a product by ID")
    @RequestMapping(path = "/api/product/{id}", method = RequestMethod.GET)
    public ProductMasterData get(@PathVariable Long id) throws ApiException {
        return productMasterDto.get(id);
    }

    @ApiOperation(value = "Get a product by ID")
    @RequestMapping(path = "/api/product/{clientId}/{clientSkuId}", method = RequestMethod.GET)
    public ProductMasterData get(@PathVariable Long clientId, @PathVariable String clientSkuId) throws ApiException {
        return productMasterDto.getByClientAndClientSku(clientId, clientSkuId);
    }

    @ApiOperation(value = "Get list of all products")
    @RequestMapping(path = "/api/product", method = RequestMethod.GET)
    public List<ProductMasterData> getAll() throws ApiException {
        return productMasterDto.getAll();
    }

    @ApiOperation(value = "Update a product entry")
    @RequestMapping(path = "/api/product/{clientId}/{clientSku}", method = RequestMethod.PUT)
    public void update(@PathVariable Long clientId, @PathVariable String clientSku, @Valid @RequestBody ProductUpdateForm form) throws ApiException {
        productMasterDto.update(clientId, clientSku, form);
    }

    @ApiOperation(value = "Get list of all products by clientId")
    @RequestMapping(path = "/api/product/client/{clientId}", method = RequestMethod.GET)
    public List<ProductMasterData> getByClient(@PathVariable Long clientId) throws ApiException {
        return productMasterDto.getByClientId(clientId);
    }

    @ApiOperation(value = "Validate products master list.")
    @RequestMapping(path = "/api/product/validate/{clientId}", method = RequestMethod.POST)
    public void validateList(@PathVariable Long clientId, @Valid @RequestBody List<ProductMasterForm> formList) throws ApiException {
        productMasterDto.validateList(formList, clientId);
    }
}