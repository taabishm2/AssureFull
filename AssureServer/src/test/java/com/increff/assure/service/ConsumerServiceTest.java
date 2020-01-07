package com.increff.assure.service;

import com.increff.assure.dao.ConsumerDao;
import com.increff.assure.pojo.ConsumerPojo;
import model.ConsumerType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ConsumerServiceTest extends AbstractUnitTest {

    @Autowired
    ConsumerService consumerService;
    @Autowired
    ConsumerDao consumerDao;

    @Before
    public void init() {
    }

    @Test
    public void testAdd() throws ApiException {

        //TODO: TestPojo.getConsumerPojo
        ConsumerPojo pojo = PojoConstructor.getConstructConsumer("TEST NAME", ConsumerType.CUSTOMER);
        consumerService.add(pojo);

        assertEquals(1, consumerDao.selectAll().size());

        ConsumerPojo duplicatePojo = PojoConstructor.getConstructConsumer("TEST NAME", ConsumerType.CUSTOMER);
        try {
            consumerService.add(duplicatePojo);
            fail("Duplicate Consumer was inserted");
        } catch (ApiException e) {
            assertEquals(e.getMessage(), "TEST NAME already exists.");
        }
    }

    public void testGetCheckId() throws ApiException {
        ConsumerPojo pojo = PojoConstructor.getConstructConsumer("TEST NAME", ConsumerType.CUSTOMER);
        consumerService.add(pojo);

        assertEquals(pojo, consumerService.getCheckId(pojo.getId()));
    }

    public void testGetAll() throws ApiException {
        assertEquals(0, consumerService.getAll().size());
        ConsumerPojo pojo = PojoConstructor.getConstructConsumer("TEST NAME", ConsumerType.CUSTOMER);
        consumerService.add(pojo);
        assertEquals(1, consumerService.getAll().size());
    }
}