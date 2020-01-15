package com.increff.assure.dto;

import com.increff.assure.pojo.BinSkuPojo;
import com.increff.assure.pojo.InventoryPojo;
import com.increff.assure.service.*;
import com.increff.assure.util.FileWriteUtil;
import model.data.BinSkuData;
import model.data.MessageData;
import model.form.BinSkuForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static com.increff.assure.util.ConvertUtil.convert;

@Service
public class BinSkuDto extends AbstractDto {
    @Autowired
    private BinService binService;
    @Autowired
    private BinSkuService binSkuService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private ProductMasterService productService;

    @Transactional(rollbackFor = ApiException.class)
    public void add(BinSkuForm binSkuForm) throws ApiException {
        checkValid(binSkuForm);
        validateProductAndBin(binSkuForm);

        BinSkuPojo binSkuPojo = convert(binSkuForm, BinSkuPojo.class);
        binSkuService.addOrUpdate(binSkuPojo);

        addOrUpdateInventory(binSkuPojo);
    }

    @Transactional(readOnly = true)
    public BinSkuData get(Long id) throws ApiException {
        return convert(binSkuService.getCheckId(id), BinSkuData.class);
    }

    @Transactional(rollbackFor = ApiException.class)
    public void addList(List<BinSkuForm> formList) throws ApiException {
        checkValid(formList);

        for (BinSkuForm form : formList) {
            validateProductAndBin(form);

            BinSkuPojo binSkuPojo = convert(form, BinSkuPojo.class);
            binSkuService.addOrUpdate(binSkuPojo);
            addOrUpdateInventory(binSkuPojo);
        }
    }

    @Transactional(readOnly = true)
    public List<BinSkuData> getAll() throws ApiException {
        return convert(binSkuService.getAll(), BinSkuData.class);
    }

    private void validateProductAndBin(BinSkuForm binSkuForm) throws ApiException {
        productService.getCheckId(binSkuForm.getGlobalSkuId());
        binService.getCheckId(binSkuForm.getBinId());
    }

    private void addOrUpdateInventory(BinSkuPojo binSkuPojo) {
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setAvailableQuantity(binSkuPojo.getQuantity());
        inventoryPojo.setAllocatedQuantity(0L);
        inventoryPojo.setFulfilledQuantity(0L);
        inventoryPojo.setGlobalSkuId(binSkuPojo.getGlobalSkuId());

        inventoryService.addOrUpdate(inventoryPojo);
    }

    @Transactional(readOnly = true)
    public List<BinSkuData> getSearchByBinAndProduct(Long binId, Long globalSkuId) throws ApiException {
        if (Objects.nonNull(binId))
            binService.getCheckId(binId);

        if (Objects.nonNull(globalSkuId))
            productService.getCheckId(globalSkuId);

        return convert(binSkuService.getSearchByBinAndProduct(binId, globalSkuId), BinSkuData.class);
    }

    @Transactional(readOnly = true)
    public void validateFormList(List<BinSkuForm> formList) throws ApiException {
        List<MessageData> errorMessages = new ArrayList<>();
        HashSet<String> binSkuSet = new HashSet<>();

        for (int index = 0; index < formList.size(); index++) {
            try {
                BinSkuForm form = formList.get(index);
                checkValid(form);
                validateProductAndBin(form);

                if (binSkuSet.contains(form.getBinId() + "," + form.getGlobalSkuId()))
                    throw new ApiException("Duplicate Bin, Product Entry");
                binSkuSet.add(form.getBinId() + "," + form.getGlobalSkuId());

            } catch (ApiException e) {
                MessageData errorMessage = new MessageData();
                errorMessage.setMessage("Error in Line: " + index + ": " + e.getMessage() + "\n");
                errorMessages.add(errorMessage);
            }
        }

        if (errorMessages.size() != 0)
            throw new ApiException(FileWriteUtil.writeErrorsToFile("inventoryError" + formList.hashCode(), errorMessages));
    }
}