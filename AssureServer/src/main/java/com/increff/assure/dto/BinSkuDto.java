package com.increff.assure.dto;

import com.increff.assure.pojo.BinSkuPojo;
import com.increff.assure.pojo.InventoryPojo;
import com.increff.assure.service.*;
import model.data.BinSkuData;
import model.form.BinSkuForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public BinSkuData get(Long id) throws ApiException {
        BinSkuPojo binSkuPojo = binSkuService.getCheckId(id);
        return convert(binSkuPojo, BinSkuData.class);
    }

    public List<BinSkuData> getAll() throws ApiException {
        List<BinSkuPojo> allBinSkuPojo = binSkuService.getAll();
        return convert(allBinSkuPojo, BinSkuData.class);
    }

    public void add(BinSkuForm binSkuForm) throws ApiException {
        validate(binSkuForm);
        BinSkuPojo binSkuPojo = convert(binSkuForm, BinSkuPojo.class);
        binSkuService.addOrUpdate(binSkuPojo);
        addOrUpdateInventory(binSkuPojo);
    }

    private void addOrUpdateInventory(BinSkuPojo binSkuPojo) {
        InventoryPojo inventoryPojo = new InventoryPojo();
        inventoryPojo.setAvailableQuantity(binSkuPojo.getAvailableQuantity());
        inventoryPojo.setAllocatedQuantity(0L);
        inventoryPojo.setFulfilledQuantity(0L);
        inventoryPojo.setGlobalSkuId(binSkuPojo.getGlobalSkuId());

        inventoryService.addOrUpdate(inventoryPojo);
    }

    public void validate(BinSkuForm binSkuPojo) throws ApiException {
        productService.getCheckId(binSkuPojo.getGlobalSkuId());
        binService.getCheckId(binSkuPojo.getBinId());
    }
}