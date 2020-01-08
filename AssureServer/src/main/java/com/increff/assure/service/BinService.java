package com.increff.assure.service;

import com.increff.assure.dao.BinDao;
import com.increff.assure.pojo.BinPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class BinService extends AbstractService {
    @Autowired
    private BinDao binDao;

    //Returns List of IDs of newly created Bins
    @Transactional(rollbackFor = ApiException.class)
    public ArrayList<Long> addBins(int numberOfBins) {
        ArrayList<Long> newBinIds = new ArrayList<>();
        for (Integer i = 0; i < numberOfBins; i++) {
            BinPojo newBinPojo = new BinPojo();
            binDao.insert(newBinPojo);
            newBinIds.add(newBinPojo.getId());
        }
        return newBinIds;
    }

    public List<BinPojo> getAll() {
        return binDao.selectAll();
    }

    public BinPojo getCheckId(Long binId) throws ApiException {
        BinPojo binPojo = binDao.select(binId);
        checkNotNull(binPojo, "Bin (ID:" + binId + ") does not exist.");
        return binPojo;
    }
}
