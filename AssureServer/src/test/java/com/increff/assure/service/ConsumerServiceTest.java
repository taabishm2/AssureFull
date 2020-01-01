package com.increff.assure.service;

import com.increff.assure.dao.ConsumerDao;
import com.increff.assure.pojo.ConsumerPojo;
import model.ConsumerType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class ConsumerServiceTest extends AbstractUnitTest {

    @Autowired
    private ConsumerService consumerServiceTest;
    @Autowired
    private ConsumerDao consumerDaoTest;

    @Test
    public void testAdd() throws ApiException {

        int initialSize = consumerDaoTest.selectAll().size();

        ConsumerPojo p = new ConsumerPojo();
        p.setName(" Tabish Mir ");
        p.setType(ConsumerType.CUSTOMER);

        consumerServiceTest.add(p);
    }

}
