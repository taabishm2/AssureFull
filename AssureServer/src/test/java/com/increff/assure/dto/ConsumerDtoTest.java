package com.increff.assure.dto;

import com.increff.assure.dao.ConsumerDao;
import com.increff.assure.pojo.ConsumerPojo;
import com.increff.assure.service.ApiException;
import model.ConsumerType;
import model.data.ConsumerData;
import model.form.ConsumerForm;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class ConsumerDtoTest extends AbstractUnitTest {

    @Autowired
    ConsumerDto consumerDto;
    @Autowired
    ConsumerDao consumerDao;

    private ConsumerForm consumerForm;
    private ConsumerPojo consumerPojo;

    @Before
    public void init() {
        consumerForm = TestForm.getConsumerForm(" tEsT nAmE  ", ConsumerType.CLIENT);
        consumerPojo = TestPojo.getConsumerPojo("new test name", ConsumerType.CLIENT);
    }

    @Test
    public void testAddValidConsumer() throws ApiException {
        consumerDto.add(consumerForm);

        assertEquals(1, consumerDao.selectAll().size());
        assertNotNull(consumerDao.selectByNameAndType("TEST NAME", ConsumerType.CLIENT));
    }

    @Test
    public void testAddConsumerWithNullFields() {
        try {
            consumerDto.add(TestForm.getConsumerForm(null, ConsumerType.CLIENT));
            fail("Added Consumer with null Name");
        } catch (ApiException e) {
            assertTrue(true);
        }

        try {
            consumerDto.add(TestForm.getConsumerForm("    ", ConsumerType.CLIENT));
            fail("Added Consumer with null Name");
        } catch (ApiException e) {
            assertTrue(true);
        }

        try {
            consumerDto.add(TestForm.getConsumerForm("ABC", null));
            fail("Added Consumer with null Type");
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testGetAValidConsumer() throws ApiException {
        consumerDao.insert(consumerPojo);

        assertEquals("new test name", consumerDto.get(consumerPojo.getId()).getName());
        assertEquals(ConsumerType.CLIENT, consumerDto.get(consumerPojo.getId()).getType());
    }

    @Test
    public void testGetAnInvalidConsumer() {
        try {
            consumerDto.add(TestForm.getConsumerForm("", ConsumerType.CLIENT));
            fail();
        } catch (ApiException e) {
            assertTrue(true);
        }

        try {
            consumerDto.add(TestForm.getConsumerForm("uyasdvyuevauwdvuaydwdfsdfwefwesdfsdf", ConsumerType.CLIENT));
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testGetAll() throws ApiException {
        consumerDao.insert(TestPojo.getConsumerPojo("ABC", ConsumerType.CLIENT));
        consumerDao.insert(TestPojo.getConsumerPojo("DEF", ConsumerType.CLIENT));
        consumerDao.insert(TestPojo.getConsumerPojo("GHI", ConsumerType.CUSTOMER));

        assertEquals(3, consumerDto.getAll().size());
    }

    @Test
    public void testGetAllClients() throws ApiException {
        consumerDao.insert(TestPojo.getConsumerPojo("ABC", ConsumerType.CLIENT));
        consumerDao.insert(TestPojo.getConsumerPojo("DEF", ConsumerType.CLIENT));
        consumerDao.insert(TestPojo.getConsumerPojo("GHI", ConsumerType.CUSTOMER));

        assertEquals(2, consumerDto.getAllClients().size());

        for (ConsumerData consumer : consumerDto.getAllClients())
            assertEquals(ConsumerType.CLIENT, consumer.getType());
    }

    @Test
    public void testGetAllCustomers() throws ApiException {
        consumerDao.insert(TestPojo.getConsumerPojo("ABC", ConsumerType.CLIENT));
        consumerDao.insert(TestPojo.getConsumerPojo("DEF", ConsumerType.CLIENT));
        consumerDao.insert(TestPojo.getConsumerPojo("GHI", ConsumerType.CUSTOMER));

        assertEquals(1, consumerDto.getAllCustomers().size());
        for (ConsumerData consumer : consumerDto.getAllCustomers())
            assertEquals(ConsumerType.CUSTOMER, consumer.getType());
    }
}