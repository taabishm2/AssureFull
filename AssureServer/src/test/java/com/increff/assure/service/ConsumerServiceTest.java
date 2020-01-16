package com.increff.assure.service;

import com.increff.assure.dao.ConsumerDao;
import com.increff.assure.pojo.ConsumerPojo;
import model.ConsumerType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class ConsumerServiceTest extends AbstractUnitTest {

    @Autowired
    ConsumerService consumerService;
    @Autowired
    ConsumerDao consumerDao;

    private ConsumerPojo validClientPojo;
    private ConsumerPojo validCustomerPojo;

    @Before
    public void init() {
        validClientPojo = TestPojo.getConsumerPojo("TEST NAME", ConsumerType.CLIENT);
        validCustomerPojo = TestPojo.getConsumerPojo("TEST CUSTOMER", ConsumerType.CUSTOMER);
    }

    @Test
    public void testAddValidConsumer() {
        try {
            consumerService.add(validClientPojo);
        } catch (ApiException e) {
            fail("Failed to insert valid Consumer");
        }
        assertEquals(1, consumerDao.selectAll().size());
    }

    @Test
    public void testAddDuplicateConsumer() {
        consumerDao.insert(validClientPojo);

        try {
            consumerService.add(validClientPojo);
            fail("Duplicate Consumer was inserted");
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testGetCheckIdWithValidId() throws ApiException {
        consumerDao.insert(validClientPojo);
        assertEquals(validClientPojo, consumerService.getCheckId(validClientPojo.getId()));
    }

    @Test
    public void testGetCheckIdWithInvalidId() {
        try {
            consumerService.getCheckId(12345L);
            fail("Invalid ID checked");
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testGetCheckClientWithValidTypes
            () throws ApiException {
        consumerDao.insert(validClientPojo);
        consumerDao.insert(validCustomerPojo);

        consumerService.getCheckClient(validClientPojo.getId());
        consumerService.getCheckCustomer(validCustomerPojo.getId());
    }

    @Test
    public void testGetCheckClientWithInvalidClient() {
        consumerDao.insert(validCustomerPojo);

        try {
            consumerService.getCheckClient(validCustomerPojo.getId());
            fail("Invalid Client was checked");
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testGetCheckClientWithInvalidCustomer() {
        consumerDao.insert(validClientPojo);

        try {
            consumerService.getCheckCustomer(validClientPojo.getId());
            fail("Invalid Customer was checked");
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testGetAllOnEmptyConsumerTable() {
        assertEquals(0, consumerService.getAll().size());
    }

    @Test
    public void testGetAllOnNonEmptyConsumerTable() {
        consumerDao.insert(validClientPojo);
        assertEquals(1, consumerService.getAll().size());

        consumerDao.insert(validCustomerPojo);
        assertEquals(2, consumerService.getAll().size());
    }

    @Test
    public void testGetAllByType() {
        consumerDao.insert(validClientPojo);
        consumerDao.insert(validCustomerPojo);

        assertEquals(1, consumerService.getAll(ConsumerType.CLIENT).size());
        assertEquals(1, consumerService.getAll(ConsumerType.CUSTOMER).size());

        consumerDao.insert(TestPojo.getConsumerPojo("TEST NAME 2", ConsumerType.CLIENT));
        assertEquals(2, consumerService.getAll(ConsumerType.CLIENT).size());
        assertEquals(1, consumerService.getAll(ConsumerType.CUSTOMER).size());

        consumerDao.insert(TestPojo.getConsumerPojo("TEST NAME 3", ConsumerType.CUSTOMER));
        assertEquals(2, consumerService.getAll(ConsumerType.CLIENT).size());
        assertEquals(2, consumerService.getAll(ConsumerType.CUSTOMER).size());
    }
}