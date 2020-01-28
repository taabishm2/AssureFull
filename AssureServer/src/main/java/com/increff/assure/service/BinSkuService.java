package com.increff.assure.service;

import com.increff.assure.dao.BinSkuDao;
import com.increff.assure.pojo.BinSkuPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static java.lang.Math.min;

@Service
public class BinSkuService extends AbstractService {
    @Autowired
    private BinSkuDao binSkuDao;

    @Transactional(rollbackFor = ApiException.class)
    public void addOrUpdate(BinSkuPojo inputPojo) {
        BinSkuPojo existingPojo = getByBinIdAndGlobalSku(inputPojo.getBinId(), inputPojo.getGlobalSkuId());
        if (Objects.nonNull(existingPojo))
            existingPojo.setQuantity(inputPojo.getQuantity() + existingPojo.getQuantity());
        else
            binSkuDao.insert(inputPojo);
    }

    public List<BinSkuPojo> getAll() {
        return binSkuDao.selectAll();
    }

    public BinSkuPojo getByBinIdAndGlobalSku(Long binId, Long globalSkuId) {
        return binSkuDao.selectByBinIdAndGlobalSku(binId, globalSkuId);
    }

    public BinSkuPojo getCheckId(Long id) throws ApiException {
        BinSkuPojo binSkuPojo = binSkuDao.select(id);
        checkNotNull(binSkuPojo, "Bin Inventory Item (ID:" + id + ") does not exist.");
        return binSkuPojo;
    }

    @Transactional(rollbackFor = ApiException.class)
    public Long removeFromBin(BinSkuPojo targetBin, Long requiredQuantity) {
        Long deduction;
        deduction = min(targetBin.getQuantity(), requiredQuantity);
        targetBin.setQuantity(targetBin.getQuantity() - deduction);

        return requiredQuantity - deduction;
    }

    public List<BinSkuPojo> selectBinsByGlobalSku(Long globalSku) {
        return binSkuDao.selectByGlobalSku(globalSku);
    }

    @Transactional(rollbackFor = ApiException.class)
    public void addList(List<BinSkuPojo> binSkuMasterPojoList) {
        for (BinSkuPojo binSkuPojo : binSkuMasterPojoList)
            addOrUpdate(binSkuPojo);
    }

    public List<BinSkuPojo> getSearchByBinAndProduct(Long binId, Long globalSkuId) {
        return binSkuDao.getSearchByBinAndProduct(binId, globalSkuId);
    }
}