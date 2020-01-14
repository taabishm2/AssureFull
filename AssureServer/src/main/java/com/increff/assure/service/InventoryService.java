package com.increff.assure.service;

import com.increff.assure.dao.InventoryDao;
import com.increff.assure.pojo.InventoryPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;

@Service
public class InventoryService extends AbstractService {
    @Autowired
    private InventoryDao inventoryDao;

    @Transactional(rollbackFor = ApiException.class)
    public void addOrUpdate(InventoryPojo inventoryPojo) {
        InventoryPojo exists = getByGlobalSku(inventoryPojo.getGlobalSkuId());
        if (Objects.nonNull(exists)) {
            exists.setAvailableQuantity(inventoryPojo.getAvailableQuantity() + exists.getAvailableQuantity());
            exists.setAllocatedQuantity(inventoryPojo.getAllocatedQuantity() + exists.getAllocatedQuantity());
            exists.setFulfilledQuantity(inventoryPojo.getFulfilledQuantity() + exists.getFulfilledQuantity());
        } else {
            inventoryDao.insert(inventoryPojo);
        }
    }

    public InventoryPojo getByGlobalSku(Long globalSkuId) {
        return inventoryDao.selectByGlobalSku(globalSkuId);
    }

    public List<InventoryPojo> getAll() {
        return inventoryDao.selectAll();
    }

    public InventoryPojo getCheckId(Long id) throws ApiException {
        InventoryPojo inventoryPojo = inventoryDao.select(id);
        checkNotNull(inventoryPojo, "Inventory Entry (ID:" + id + ") does not exist");
        return inventoryPojo;
    }

    @Transactional(rollbackFor = ApiException.class)
    public void allocateAvailableItems(Long globalSkuId, Long allocatedQuantity) throws ApiException {
        InventoryPojo inventoryItem = getByGlobalSku(globalSkuId);
        checkNotNull(inventoryItem, "Couldn't find Product in Inventory, GlobalSkuID:" + globalSkuId);

        inventoryItem.setAvailableQuantity(inventoryItem.getAvailableQuantity() - allocatedQuantity);
        inventoryItem.setAllocatedQuantity(inventoryItem.getAllocatedQuantity() + allocatedQuantity);
    }

    public void fulfillInInventory(Long globalSkuId, Long allocatedOrderItemQuantity) {
        InventoryPojo inventoryPojo = getByGlobalSku(globalSkuId);
        inventoryPojo.setAllocatedQuantity(inventoryPojo.getAllocatedQuantity() - allocatedOrderItemQuantity);
        inventoryPojo.setFulfilledQuantity(inventoryPojo.getFulfilledQuantity() + allocatedOrderItemQuantity);
    }
}
