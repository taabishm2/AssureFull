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
        consumerForm = FormConstructor.getConstructConsumer(" tEsT nAmE  ", ConsumerType.CLIENT);
        consumerPojo = TestPojo.getConsumerPojo("NEW TEST NAME", ConsumerType.CLIENT);
    }

    @Test
    public void testAdd() throws ApiException {
        int initialCount = consumerDao.selectAll().size();
        consumerDto.add(consumerForm);

        assertEquals(1, consumerDao.selectAll().size() - initialCount);
        assertNotNull(consumerDao.selectByNameAndType("TEST NAME", ConsumerType.CLIENT));
    }

    @Test
    public void testGet() throws ApiException {
        consumerDao.insert(consumerPojo);

        assertEquals("NEW TEST NAME", consumerDto.get(consumerPojo.getId()).getName());
        assertEquals(ConsumerType.CLIENT, consumerDto.get(consumerPojo.getId()).getType());

        try {
            consumerDto.add(FormConstructor.getConstructConsumer("", ConsumerType.CLIENT));
            fail();
        } catch (ApiException e) {
        }

        try {
            consumerDto.add(FormConstructor.getConstructConsumer("uyasdvyuevauwdvuaydwdfsdfwefwesdfsdf", ConsumerType.CLIENT));
        } catch (ApiException e) {
        }
    }

    @Test
    public void testGetAll() throws ApiException {
        assertEquals(0, consumerDto.getAll().size());

        consumerDao.insert(TestPojo.getConsumerPojo("ABC", ConsumerType.CLIENT));
        consumerDao.insert(TestPojo.getConsumerPojo("DEF", ConsumerType.CLIENT));
        consumerDao.insert(TestPojo.getConsumerPojo("GHI", ConsumerType.CUSTOMER));

        assertEquals(3, consumerDto.getAll().size());
    }

    @Test
    public void testGetAllClients() throws ApiException {
        assertEquals(0, consumerDto.getAll().size());

        consumerDao.insert(TestPojo.getConsumerPojo("ABC", ConsumerType.CLIENT));
        consumerDao.insert(TestPojo.getConsumerPojo("DEF", ConsumerType.CLIENT));
        consumerDao.insert(TestPojo.getConsumerPojo("GHI", ConsumerType.CUSTOMER));

        assertEquals(2, consumerDto.getAllClients().size());
        for(ConsumerData consumer:consumerDto.getAllClients())
            assertEquals(ConsumerType.CLIENT, consumer.getType());
    }

    @Test
    public void testGetAllCustomers() throws ApiException {
        assertEquals(0, consumerDto.getAll().size());

        consumerDao.insert(TestPojo.getConsumerPojo("ABC", ConsumerType.CLIENT));
        consumerDao.insert(TestPojo.getConsumerPojo("DEF", ConsumerType.CLIENT));
        consumerDao.insert(TestPojo.getConsumerPojo("GHI", ConsumerType.CUSTOMER));

        assertEquals(1, consumerDto.getAllCustomers().size());
        for(ConsumerData consumer:consumerDto.getAllCustomers())
            assertEquals(ConsumerType.CUSTOMER, consumer.getType());
    }
}