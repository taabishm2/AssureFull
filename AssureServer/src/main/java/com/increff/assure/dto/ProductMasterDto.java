package com.increff.assure.dto;

import com.increff.assure.pojo.ProductMasterPojo;
import com.increff.assure.service.ApiException;
import com.increff.assure.service.ConsumerService;
import com.increff.assure.service.ProductMasterService;
import com.increff.assure.util.CheckValid;
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
import java.util.Objects;

@Service
public class ProductMasterDto {
    @Autowired
    private ProductMasterService productService;
    @Autowired
    private ConsumerService consumerService;

    public ProductMasterData get(Long id) throws ApiException {
        ProductMasterPojo productPojo = productService.getCheckId(id);
        return ConvertUtil.convert(productPojo, ProductMasterData.class);
    }

    public void normalizeAndValidateForm(ProductMasterForm productForm) throws ApiException {
        NormalizeUtil.normalize(productForm);
        CheckValid.validate((productForm));
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
        System.out.println("\tValidated Client");
        List<MessageData> errorMessages = new ArrayList<>();
        System.out.println("\tArray initialized");
        for(int i=0; i<formList.size(); i++){
            try {
                System.out.println("\t\tValidating Form "+formList.get(i).getName()+" "+formList.get(i).getClientSkuId()+" "+formList.get(i).getBrandId()+" "+formList.get(i).getMrp()+" "+formList.get(i).getDescription());
                CheckValid.validate((formList.get(i)));
                System.out.println("\t\tCheck Valid Done");
            } catch(ApiException e){
                System.out.println("\t\tVALIDATION FAILURE: "+e.getMessage());
                MessageData errorMessage = new MessageData();
                errorMessage.setMessage("Error in Line: "+i+": "+e.getMessage()+"\n");
                errorMessages.add(errorMessage);
            }
        }

        if(errorMessages.size() != 0)
            throw new ApiException(FileWriteUtil.writeErrorsToFile("productError"+formList.hashCode(),errorMessages));
    }
}