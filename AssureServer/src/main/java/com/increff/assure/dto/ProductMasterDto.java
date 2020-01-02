package com.increff.assure.dto;

import com.increff.assure.pojo.ProductMasterPojo;
import com.increff.assure.service.ApiException;
import com.increff.assure.service.ConsumerService;
import com.increff.assure.service.ProductMasterService;
import model.ConsumerType;
import model.data.ProductMasterData;
import model.form.ProductMasterForm;
import model.form.ProductUpdateForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.increff.assure.util.ConvertUtil.convert;

@Service
public class ProductMasterDto {
    @Autowired
    private ProductMasterService productService;
    @Autowired
    private ConsumerService consumerService;

    public ProductMasterData get(Long id) throws ApiException {
        ProductMasterPojo productPojo = productService.getCheckId(id);
        return convert(productPojo, ProductMasterData.class);
    }

    public void add(ProductMasterForm productForm) throws ApiException {
        ProductMasterPojo productPojo = convert(productForm, ProductMasterPojo.class);
        validateClient(productPojo.getClientId());
        productService.add(productPojo);
    }

    private void validateClient(Long clientId) throws ApiException {
        if (!consumerService.getCheckId(clientId).getType().equals(ConsumerType.CLIENT))
            throw new ApiException("Client (ClientID:" + clientId + ") not registered.");
    }

    public List<ProductMasterData> getAll() throws ApiException {
        List<ProductMasterPojo> allProductMasterPojo = productService.getAll();
        return convert(allProductMasterPojo, ProductMasterData.class);
    }

    public void addList(List<ProductMasterForm> formList) throws ApiException {
        List<ProductMasterPojo> productMasterPojoList = convert(formList, ProductMasterPojo.class);
        productService.addList(productMasterPojoList);
    }

    public void update(Long clientId, String clientSku, ProductUpdateForm form) throws ApiException {
        productService.update(clientId, clientSku, form);
    }
}