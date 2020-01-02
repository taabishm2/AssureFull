package com.increff.assure.service;

import com.increff.assure.dao.ConsumerDao;
import com.increff.assure.pojo.ConsumerPojo;
import model.ConsumerType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class ConsumerServiceTest extends AbstractUnitTest {
    private static ConsumerPojo pojo;

    static {
        pojo = new ConsumerPojo();
        pojo.setName(" Tabish Mir ");
        pojo.setType(ConsumerType.CUSTOMER);
    }

    @Autowired
    private ConsumerService consumerServiceTest;
    @Autowired
    private ConsumerDao consumerDaoTest;

    @Test
    public void testAdd() throws ApiException {
        int initialSize = consumerDaoTest.selectAll().size();
        consumerServiceTest.add(pojo);
        int finalSize = consumerDaoTest.selectAll().size();

        ConsumerPojo pojo = consumerDaoTest.selectByNameAndType("TABISH MIR", ConsumerType.CUSTOMER);
        assertEquals(1, finalSize - initialSize);
    }
}
