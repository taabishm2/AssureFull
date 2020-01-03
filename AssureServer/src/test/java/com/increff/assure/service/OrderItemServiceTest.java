package com.increff.assure.service;

import com.increff.assure.dao.OrderItemDao;
import com.increff.assure.pojo.OrderItemPojo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class OrderItemServiceTest extends AbstractUnitTest {

    @Autowired
    OrderItemService orderItemService;
    @Autowired
    OrderItemDao orderItemDao;

    private OrderItemPojo orderItemPojo;

    @Before
    public void init() {
        orderItemPojo = PojoConstructor.getConstructOrderItem(123L, 100L, 10L, 0L, 0L);
    }

    @Test
    public void testAdd() throws ApiException {
        int initialCount = orderItemDao.selectAll().size();
        orderItemService.add(orderItemPojo);
        assertEquals(1, orderItemService.getAll().size() - initialCount);

        try {
            orderItemService.add(orderItemPojo);
            fail("Duplicate Order Item Inserted");
        } catch (ApiException e) {
            assertEquals("GlobalSKU, OrderID pair already exists.", e.getMessage());
        }

        assertEquals(0, (long) orderItemPojo.getAllocatedQuantity());
        assertEquals(0, (long) orderItemPojo.getFulfilledQuantity());
    }

    @Test
    public void testGetCheckOrderAndGlobalSku() throws ApiException {
        orderItemDao.insert(orderItemPojo);

        assertEquals(orderItemService.getCheckOrderAndGlobalSku(orderItemPojo.getOrderId(), orderItemPojo.getGlobalSkuId()), orderItemPojo);
        try {
            orderItemService.getCheckOrderAndGlobalSku(999L, 666L);
            fail("Invalid OrderID, GlobalSKU matched");
        } catch (ApiException e) {
            assertEquals("Order Item with OrderID:999 GSKU:666 does not exist.", e.getMessage());
        }
    }

    @Test
    public void testGetByOrderId() {
        orderItemDao.insert(PojoConstructor.getConstructOrderItem(123L, 100L, 10L, 0L, 0L));
        orderItemDao.insert(PojoConstructor.getConstructOrderItem(456L, 100L, 10L, 0L, 0L));
        orderItemDao.insert(PojoConstructor.getConstructOrderItem(789L, 200L, 10L, 0L, 0L));

        int countClientId100 = 0;
        for (OrderItemPojo orderItem : orderItemService.getByOrderId(100L)) {
            assertEquals(100L, (long) orderItem.getOrderId());
            countClientId100++;
        }
        assertEquals(2, countClientId100);

        int countClientId200 = 0;
        for (OrderItemPojo orderItem : orderItemService.getByOrderId(200L)) {
            assertEquals(200L, (long) orderItem.getOrderId());
            countClientId200++;
        }
        assertEquals(1, countClientId200);
    }

    @Test
    public void testAllocateOrderItems() throws ApiException {
        orderItemDao.insert(orderItemPojo);
        orderItemService.allocateOrderItems(orderItemPojo, 8L);
        assertEquals(10L, (long) orderItemPojo.getOrderedQuantity());
        assertEquals(8L, (long) orderItemPojo.getAllocatedQuantity());
        assertEquals(0L, (long) orderItemPojo.getFulfilledQuantity());

        orderItemService.allocateOrderItems(orderItemPojo, 2L);
        assertEquals(10L, (long) orderItemPojo.getAllocatedQuantity());
        assertEquals(10L, (long) orderItemPojo.getAllocatedQuantity());
        assertEquals(0L, (long) orderItemPojo.getFulfilledQuantity());
    }
}