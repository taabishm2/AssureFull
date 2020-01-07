package com.increff.assure.dto;

import com.increff.assure.pojo.BinSkuPojo;
import com.increff.assure.pojo.InventoryPojo;
import com.increff.assure.service.*;
import com.increff.assure.util.CheckValid;
import model.data.BinSkuData;
import model.form.BinSkuForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.increff.assure.util.ConvertUtil.convert;

@Service
public class BinSkuDto {
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
        CheckValid.validate(binSkuForm);
        validateProductAndBin(binSkuForm);

        BinSkuPojo binSkuPojo = convert(binSkuForm, BinSkuPojo.class);
        binSkuService.addOrUpdate(binSkuPojo);

        addOrUpdateInventory(binSkuPojo);
    }

    @Transactional(readOnly = true)
    public BinSkuData get(Long id) throws ApiException {
        BinSkuPojo binSkuPojo = binSkuService.getCheckId(id);
        return convert(binSkuPojo, BinSkuData.class);
    }

    @Transactional(rollbackFor = ApiException.class)
    public void addList(List<BinSkuForm> formList) throws ApiException {
        List<BinSkuPojo> binSkuMasterPojoList = convert(formList, BinSkuPojo.class);
        binSkuService.addList(binSkuMasterPojoList);
    }

    @Transactional(readOnly = true)
    public List<BinSkuData> getAll() throws ApiException {
        List<BinSkuPojo> allBinSkuPojo = binSkuService.getAll();
        return convert(allBinSkuPojo, BinSkuData.class);
    }

    @Transactional(readOnly = true)
    public void validateProductAndBin(BinSkuForm binSkuPojo) throws ApiException {
        productService.getCheckId(binSkuPojo.getGlobalSkuId());
        binService.getCheckId(binSkuPojo.getBinId());
    }

    @Transactional(rollbackFor = ApiException.class)
    private void addOrUpdateInventory(BinSkuPojo binSkuPojo) {
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setAvailableQuantity(binSkuPojo.getQuantity());
        inventoryPojo.setAllocatedQuantity(0L);
        inventoryPojo.setFulfilledQuantity(0L);
        inventoryPojo.setGlobalSkuId(binSkuPojo.getGlobalSkuId());

        inventoryService.addOrUpdate(inventoryPojo);
    }
}