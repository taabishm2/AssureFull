package com.increff.assure.service;

import com.increff.assure.dao.ChannelDao;
import com.increff.assure.dao.ChannelListingDao;
import com.increff.assure.dao.ConsumerDao;
import com.increff.assure.dao.OrderDao;
import com.increff.assure.pojo.ChannelPojo;
import com.increff.assure.pojo.ConsumerPojo;
import com.increff.assure.pojo.OrderPojo;
import model.ConsumerType;
import model.InvoiceType;
import model.OrderStatus;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class OrderServiceTest extends AbstractUnitTest {

    @Autowired
    OrderService orderService;
    @Autowired
    OrderDao orderDao;
    @Autowired
    ConsumerDao consumerDao;
    @Autowired
    ChannelDao channelDao;
    @Autowired
    ChannelListingDao channelListingDao;

    private ConsumerPojo clientPojo;
    private ConsumerPojo customerPojo;
    private ChannelPojo channel;
    private OrderPojo orderPojo;

    @Before
    public void init() {
        clientPojo = TestPojo.getConsumerPojo("Puma", ConsumerType.CLIENT);
        customerPojo = TestPojo.getConsumerPojo("User Name", ConsumerType.CUSTOMER);
        channel = TestPojo.getChannelPojo("FLIPKART", InvoiceType.CHANNEL);

        consumerDao.insert(clientPojo);
        consumerDao.insert(customerPojo);
        channelDao.insert(channel);

        orderPojo = TestPojo.getOrderPojo(customerPojo.getId(), clientPojo.getId(), channel.getId(), "FLIPoID", OrderStatus.CREATED);
    }

    @Test
    public void testAdd() throws ApiException {
        int initialCount = orderService.getAll().size();
        orderService.add(orderPojo);
        assertEquals(1, orderService.getAll().size() - initialCount);

        assertEquals(OrderStatus.CREATED, orderPojo.getStatus());

        try {
            orderService.add(orderPojo);
            fail("Duplicate Order Inserted");
        } catch (ApiException e) {
            assertEquals("Order with ChannelID & ChannelOrderID pair already exist.", e.getMessage());
        }
    }

    public void testGetCheckId() throws ApiException {
        orderService.add(orderPojo);
        assertEquals(orderService.getCheckId(orderPojo.getId()),orderPojo);
    }

    public void testUpdateOrderStatus() throws ApiException {
        orderService.add(orderPojo);
        orderService.updateStatus(orderPojo.getId(), OrderStatus.ALLOCATED);
        assertEquals(OrderStatus.ALLOCATED, orderPojo.getStatus());

        orderService.updateStatus(orderPojo.getId(), OrderStatus.FULFILLED);
        assertEquals(OrderStatus.FULFILLED, orderPojo.getStatus());

        orderService.updateStatus(orderPojo.getId(), OrderStatus.CREATED);
        assertEquals(OrderStatus.CREATED, orderPojo.getStatus());
    }

    @Test
    public void testGetOrderClient() throws ApiException {
        orderService.add(orderPojo);
        assertEquals(clientPojo.getId(), orderService.getOrderClient(orderPojo.getId()));
    }

    @Test
    public void testGetByChannel(){
        orderDao.insert(TestPojo.getOrderPojo(123L, 456L, 1L, "CHA1", OrderStatus.CREATED));
        orderDao.insert(TestPojo.getOrderPojo(123L, 456L, 1L, "CHA3", OrderStatus.CREATED));
        orderDao.insert(TestPojo.getOrderPojo(123L, 456L, 3L, "CHA1", OrderStatus.CREATED));
        orderDao.insert(TestPojo.getOrderPojo(123L, 456L, 3L, "CHA3", OrderStatus.CREATED));
        orderDao.insert(TestPojo.getOrderPojo(123L, 456L, 3L, "CHA4", OrderStatus.CREATED));

        assertEquals(2, orderService.getByChannel(1L).size());
        assertEquals(3, orderService.getByChannel(3L).size());
    }

    @Test
    public void testGetByChannelWithInvalidChannel(){
        assertEquals(0, orderService.getByChannel(10L).size());
    }
}