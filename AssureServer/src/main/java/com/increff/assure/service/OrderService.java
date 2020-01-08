package com.increff.assure.service;

import com.increff.assure.dao.OrderDao;
import com.increff.assure.pojo.OrderPojo;
import model.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService extends AbstractService {
    @Autowired
    private OrderDao orderDao;

    @Transactional(rollbackFor = ApiException.class)
    public void add(OrderPojo orderPojo) throws ApiException {
        checkDuplicateOrders(orderPojo);
        orderPojo.setStatus(OrderStatus.CREATED);
        orderDao.insert(orderPojo);
    }

    private void checkDuplicateOrders(OrderPojo order) throws ApiException {
        OrderPojo matchedPojo = orderDao.selectByChannelAndChannelOrderId(order.getChannelId(), order.getChannelOrderId());
        checkNull(matchedPojo, "Order with ChannelID & ChannelOrderID pair already exist.");
    }

    public List<OrderPojo> getAll() {
        return orderDao.selectAll();
    }

    public OrderPojo getCheckId(Long id) throws ApiException {
        OrderPojo orderPojo = orderDao.select(id);
        checkNotNull(orderPojo, "Order (ID:" + id + ") does not exist.");
        return orderPojo;
    }

    @Transactional(rollbackFor = ApiException.class)
    public void updateStatus(Long orderId, OrderStatus status) throws ApiException {
        OrderPojo matchedPojo = getCheckId(orderId);
        matchedPojo.setStatus(status);
    }

    public Long getOrderClient(Long orderId) throws ApiException {
        return getCheckId(orderId).getClientId();
    }
}