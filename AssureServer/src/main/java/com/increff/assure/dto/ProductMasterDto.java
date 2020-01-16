package com.increff.assure.dto;

import com.increff.assure.pojo.ProductMasterPojo;
import com.increff.assure.service.ApiException;
import com.increff.assure.service.ConsumerService;
import com.increff.assure.service.ProductMasterService;
import com.increff.assure.util.ConvertUtil;
import com.increff.assure.util.NormalizeUtil;
import model.ConsumerType;
import model.data.ProductMasterData;
import model.form.ProductMasterForm;
import model.form.ProductUpdateForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductMasterDto extends AbstractDto {
    @Autowired
    private ProductMasterService productService;
    @Autowired
    private ConsumerService consumerService;

    public ProductMasterData get(Long id) throws ApiException {
        return ConvertUtil.convert(productService.getCheckId(id), ProductMasterData.class);
    }

    public List<ProductMasterData> getAll() throws ApiException {
        List<ProductMasterPojo> allProductMasterPojo = productService.getAll();
        return ConvertUtil.convert(allProductMasterPojo, ProductMasterData.class);
    }

    @Transactional(rollbackFor = ApiException.class)
    public void addList(List<ProductMasterForm> productFormList, Long clientId) throws ApiException {
        consumerService.getCheckClient(clientId);
        List<ProductMasterPojo> productMasterPojoList = new ArrayList<>();

        for (ProductMasterForm productForm : productFormList) {
            NormalizeUtil.normalize(productForm);
            checkValid(productForm);

            productMasterPojoList.add(convertFormToPojo(productForm, clientId));
        }
        productService.addList(productMasterPojoList);
    }

    public void update(Long clientId, String clientSku, ProductUpdateForm form) throws ApiException {
        productService.update(clientId, clientSku, form);
    }

    public ProductMasterData getByClientAndClientSku(Long clientId, String clientSkuId) throws ApiException {
        return ConvertUtil.convert(productService.getByClientAndClientSku(clientId, clientSkuId), ProductMasterData.class);
    }

    public List<ProductMasterData> getByClientId(Long clientId) throws ApiException {
        return ConvertUtil.convert(productService.getByClientId(clientId), ProductMasterData.class);
    }

    public void validateFormList(List<ProductMasterForm> formList, Long clientId) throws ApiException {
        consumerService.getCheckClient(clientId);
        StringBuilder errorDetailString = new StringBuilder();

        for (int index = 0; index < formList.size(); index++) {
            try {
                checkValid((formList.get(index)));
            } catch (ApiException e) {
                errorDetailString.append("Error in Line: ").append(index+1).append(": ").append(e.getMessage()).append("<br \\>");
            }
        }

        if (errorDetailString.length() > 0)
            throw new ApiException(errorDetailString.toString());
    }

    private ProductMasterPojo convertFormToPojo(ProductMasterForm productForm, Long clientId) throws ApiException {
        ProductMasterPojo productPojo = ConvertUtil.convert(productForm, ProductMasterPojo.class);
        productPojo.setClientId(clientId);
        return productPojo;
    }
}