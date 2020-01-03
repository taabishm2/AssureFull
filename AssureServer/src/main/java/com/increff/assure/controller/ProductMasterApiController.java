package com.increff.assure.controller;

import com.increff.assure.dto.ProductMasterDto;
import com.increff.assure.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import model.data.ProductMasterData;
import model.form.ProductMasterForm;
import model.form.ProductUpdateForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api
@RestController
public class ProductMasterApiController {
    @Autowired
    private ProductMasterDto productMasterDto;

    @ApiOperation(value = "Add a Product")
    @RequestMapping(path = "/api/product", method = RequestMethod.POST)
    public void add(@Valid @RequestBody ProductMasterForm form) throws ApiException {
        productMasterDto.add(form);
    }

    @ApiOperation(value = "Add a List of Products")
    @RequestMapping(path = "/api/product/list", method = RequestMethod.POST)
    public void addList(@Valid @RequestBody List<ProductMasterForm> formList) throws ApiException {
        productMasterDto.addList(formList);
    }

    @ApiOperation(value = "Get a product by ID")
    @RequestMapping(path = "/api/product/{id}", method = RequestMethod.GET)
    public ProductMasterData get(@PathVariable Long id) throws ApiException {
        return productMasterDto.get(id);
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
}