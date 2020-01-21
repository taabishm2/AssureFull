package com.increff.assure.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.increff.assure.dao.*;
import com.increff.assure.pojo.*;
import com.increff.assure.service.ApiException;
import com.increff.assure.util.ConvertUtil;
import model.ConsumerType;
import model.InvoiceType;
import model.OrderStatus;
import model.form.OrderForm;
import model.form.OrderItemForm;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class OrderItemDtoTest extends AbstractUnitTest {

    @Autowired
    OrderItemDto orderItemDto;
    @Autowired
    OrderItemDao orderItemDao;
    @Autowired
    OrderDao orderDao;
    @Autowired
    ProductMasterDao productDao;

    private OrderPojo orderPojo;
    private OrderItemPojo orderItemPojo;
    private ProductMasterPojo productA;
    private ProductMasterPojo productB;
    @Before
    public void init() {
        orderPojo = TestPojo.getOrderPojo(1L, 2L, 3L, "C@#", OrderStatus.CREATED);
        orderDao.insert(orderPojo);

        productA = TestPojo.getProductPojo("A",32L,"zz",34D, "SF", "FE");
        productB = TestPojo.getProductPojo("B",32L,"zz",34D, "FF", "FE");

        orderItemPojo = TestPojo.getConstructOrderItem(productA.getId(),orderPojo.getId(),10L,0L,0L);
    }

    @Test
    public void testAddValidOrderItem() throws ApiException {
    }

}