package com.increff.assure.dto;

import com.increff.assure.pojo.InventoryPojo;
import com.increff.assure.service.ApiException;
import com.increff.assure.service.InventoryService;
import model.data.InventoryData;
import model.form.InventoryForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static com.increff.assure.util.ConvertUtil.convert;

@Service
public class InventoryDto extends AbstractDto {
    @Autowired
    private InventoryService inventoryService;

    @Transactional(rollbackFor = ApiException.class)
    public void add(InventoryForm form) throws ApiException {
        InventoryPojo inventoryPojo = convert(form, InventoryPojo.class);
        inventoryService.addOrUpdate(inventoryPojo);
    }

    @Transactional(readOnly = true)
    public InventoryData get(Long id) throws ApiException {
        InventoryPojo inventoryPojo = inventoryService.getCheckId(id);
        return convert(inventoryPojo, InventoryData.class);
    }

    @Transactional(readOnly = true)
    public List<InventoryData> getAll() throws ApiException {
        return convert(inventoryService.getAll(), InventoryData.class);
    }
}
