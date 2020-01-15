package com.increff.assure.dto;

import com.increff.assure.pojo.ProductMasterPojo;
import com.increff.assure.service.ApiException;
import com.increff.assure.service.ConsumerService;
import com.increff.assure.service.ProductMasterService;
import com.increff.assure.util.ConvertUtil;
import com.increff.assure.util.FileWriteUtil;
import com.increff.assure.util.NormalizeUtil;
import model.ConsumerType;
import model.data.MessageData;
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

    public void normalizeAndValidateForm(ProductMasterForm productForm) throws ApiException {
        NormalizeUtil.normalize(productForm);
        checkValid(productForm);
    }

    private void validateClient(Long clientId) throws ApiException {
        if (!consumerService.getCheckId(clientId).getType().equals(ConsumerType.CLIENT))
            throw new ApiException("Client (ClientID:" + clientId + ") not registered.");
    }

    public List<ProductMasterData> getAll() throws ApiException {
        List<ProductMasterPojo> allProductMasterPojo = productService.getAll();
        return ConvertUtil.convert(allProductMasterPojo, ProductMasterData.class);
    }

    @Transactional(rollbackFor = ApiException.class)
    public void addList(List<ProductMasterForm> formList, Long clientId) throws ApiException {
        validateClient(clientId);

        List<ProductMasterPojo> productMasterPojoList = new ArrayList<>();
        for (ProductMasterForm productForm : formList) {
            normalizeAndValidateForm(productForm);

            productMasterPojoList.add(convertFormToPojo(productForm, clientId));
        }
        productService.addList(productMasterPojoList);
    }

    private ProductMasterPojo convertFormToPojo(ProductMasterForm productForm, Long clientId) throws ApiException {
        ProductMasterPojo productPojo = ConvertUtil.convert(productForm, ProductMasterPojo.class);
        productPojo.setClientId(clientId);
        return productPojo;
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

    public void validateList(List<ProductMasterForm> formList, Long clientId) throws ApiException {
        validateClient(clientId);
        List<MessageData> errorMessages = new ArrayList<>();

        for (int index = 0; index < formList.size(); index++) {
            try {
                checkValid((formList.get(index)));
            } catch (ApiException e) {
                MessageData errorMessage = new MessageData();
                errorMessage.setMessage("Error in Line: " + index + ": " + e.getMessage() + "\n");
                errorMessages.add(errorMessage);
            }
        }

        if (errorMessages.size() != 0)
            throw new ApiException(FileWriteUtil.writeErrorsToFile("productError" + formList.hashCode(), errorMessages));
    }
}