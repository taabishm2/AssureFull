package com.increff.assure.dto;

import com.increff.assure.service.ApiException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BinDtoTest extends AbstractUnitTest {

    @Autowired
    BinDto binDto;

    @Test
    public void testAdd() throws ApiException {
        ArrayList<Long> binIdList = binDto.add(5);
        assertEquals(5, binIdList.size());

        HashSet<Long> hashSet = new HashSet<>(binIdList);
        assertEquals(5, hashSet.size());
    }

    @Test
    public void testAddInvalidBinQty(){
        try{
            binDto.add(-10);
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testGetAll() {
        List<Long> allBinIds = binDto.getAllBins();
        HashSet<Long> hashSet = new HashSet<>(allBinIds);
        assertEquals(allBinIds.size(), hashSet.size());
    }
}