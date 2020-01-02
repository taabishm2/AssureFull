package com.increff.assure.service;

import com.increff.assure.dao.ConsumerDao;
import com.increff.assure.pojo.ConsumerPojo;
import model.ConsumerType;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SampleTest extends AbstractUnitTest {
    @Autowired
    private static ConsumerService consumerService;
    @Autowired
    private static ConsumerDao consumerDao;
    @Autowired
    private static TestDataInitializer testDataInitializer;

    @BeforeClass
    public static void init() {
        testDataInitializer.init();
        testDataInitializer.populate();
    }

    @Test
    public void testAdd() throws ApiException {
        ConsumerPojo pojo = new ConsumerPojo();
        pojo.setName(" Test Name  ");
        pojo.setType(ConsumerType.CUSTOMER);

        int initialSize = consumerDao.selectAll().size();
        consumerService.add(pojo);
        int finalSize = consumerDao.selectAll().size();

        ConsumerPojo testPojo = consumerDao.selectByNameAndType("TEST NAME", ConsumerType.CUSTOMER);
        assertEquals(1, finalSize - initialSize);

        ConsumerPojo duplicatePojo = new ConsumerPojo();
        pojo.setName("  tEsT NaMe  ");
        pojo.setType(ConsumerType.CUSTOMER);
        try {
            consumerService.add(duplicatePojo);
            fail("Duplicate Consumer POJO was inserted.");
        } catch (ApiException e) {
            assertEquals("TEST NAME already exists.", e.getMessage());
        }
    }
}
