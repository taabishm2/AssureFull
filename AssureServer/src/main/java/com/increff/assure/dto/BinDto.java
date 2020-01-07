package com.increff.assure.dto;

import com.increff.assure.pojo.BinPojo;
import com.increff.assure.service.ApiException;
import com.increff.assure.service.BinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BinDto {
    @Autowired
    private BinService binService;

    @Transactional(rollbackFor = ApiException.class)
    public ArrayList<Long> add(Integer numberOfBins) throws ApiException {
        return binService.addBins(numberOfBins);
    }

    @Transactional(readOnly = true)
    public List<Long> getAll() throws ApiException {
        List<BinPojo> binPojoList = binService.getAll();
        return binPojoList.stream().map(BinPojo::getId).collect(Collectors.toList());
    }
}