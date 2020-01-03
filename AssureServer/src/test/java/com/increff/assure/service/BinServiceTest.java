package com.increff.assure.service;

import com.increff.assure.dao.BinDao;
import com.increff.assure.dao.ConsumerDao;
import com.increff.assure.pojo.ConsumerPojo;
import model.ConsumerType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BinServiceTest extends AbstractUnitTest {

    @Autowired
    BinService binService;
    @Autowired
    BinDao binDao;

    @Before
    public void init() {
    }

    @Test
    public void testAddBins() {
        int initialBinCount = binService.getAll().size();
        binService.addBins(5);

        assertEquals(5,binService.getAll().size()-initialBinCount);
    }

    @Test
    public void testGetCheckId() throws ApiException {
        ArrayList<Long> binIdList = binService.addBins(5);
        for(Long id:binIdList)
            binService.getCheckId(id);

        try{
            binService.getCheckId(1234L);
            fail("Invalid BinId validated");
        }
        catch(ApiException e){
            assertEquals("Bin (ID:1234) does not exist.",e.getMessage());
        }
    }
}