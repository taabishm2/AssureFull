package com.increff.assure.service;

import com.increff.assure.dao.BinDao;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

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
        assertEquals(5, binService.addBins(5).size());
    }

    @Test
    public void testGetCheckId() throws ApiException {
        for (Long id : binService.addBins(5))
            binService.getCheckId(id);
    }

    @Test
    public void testGetCheckIdWithInvalidId() {
        try {
            binService.getCheckId(1234L);
            fail("Invalid BinId validated");
        } catch (ApiException e) {
            assertTrue(true);
        }
    }
}