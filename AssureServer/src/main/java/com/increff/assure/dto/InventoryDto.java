package com.increff.assure.dto;

import com.increff.assure.pojo.InventoryPojo;
import com.increff.assure.service.ApiException;
import com.increff.assure.service.InventoryService;
import model.data.InventoryData;
import model.form.InventoryForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.increff.assure.util.ConvertUtil.convert;

@Service
public class InventoryDto extends AbstractDto {
    @Autowired
    private InventoryService inventoryService;

    @Transactional(rollbackFor = ApiException.class)
    public void add(InventoryForm form) throws ApiException {
        inventoryService.addOrUpdate(convert(form, InventoryPojo.class));
    }
}