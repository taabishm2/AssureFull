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
public class BinDto extends AbstractDto {
    @Autowired
    private BinService binService;

    @Transactional(rollbackFor = ApiException.class)
    public ArrayList<Long> add(Integer binCount) throws ApiException {
        if(binCount <= 0)
            throw new ApiException("Bin Count must be positive");

        return binService.addBins(binCount);
    }

    @Transactional(readOnly = true)
    public List<Long> getAll() throws ApiException {
        return binService.getAll().stream().map(BinPojo::getId).collect(Collectors.toList());
    }
}