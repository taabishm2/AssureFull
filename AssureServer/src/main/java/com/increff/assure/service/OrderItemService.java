package com.increff.assure.service;

import com.increff.assure.dao.OrderItemDao;
import com.increff.assure.pojo.OrderItemPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class OrderItemService extends AbstractService {
    @Autowired
    private OrderItemDao orderItemDao;

    @Transactional(rollbackFor = ApiException.class)
    public void add(OrderItemPojo orderItemPojo) throws ApiException {
        checkDuplicate(orderItemPojo);
        orderItemPojo.setAllocatedQuantity(0L);
        orderItemPojo.setFulfilledQuantity(0L);
        orderItemDao.insert(orderItemPojo);
    }

    @Transactional(rollbackFor = ApiException.class)
    public void addList(List<OrderItemPojo> orderItemList) throws ApiException {
        for (OrderItemPojo orderItem : orderItemList)
            add(orderItem);
    }

    private void checkDuplicate(OrderItemPojo orderItem) throws ApiException {
        OrderItemPojo matchedOrderItem = orderItemDao.selectByOrderIdAndGlobalSku(orderItem.getOrderId(), orderItem.getGlobalSkuId());
        checkNull(matchedOrderItem, "GlobalSKU, OrderID pair already exists.");
    }

    public List<OrderItemPojo> getAll() {
        return orderItemDao.selectAll();
    }

    public OrderItemPojo getCheckId(Long id) throws ApiException {
        OrderItemPojo orderItemPojo = orderItemDao.select(id);
        checkNotNull(orderItemPojo, "OrderItem (ID:" + id + ") does not exist.");
        return orderItemPojo;
    }

    public OrderItemPojo getCheckOrderAndGlobalSku(Long orderId, Long globalSkuId) throws ApiException {
        OrderItemPojo matchedPojo = orderItemDao.selectByOrderIdAndGlobalSku(orderId, globalSkuId);
        checkNotNull(matchedPojo, "Order Item with OrderID:" + orderId + " GSKU:" + globalSkuId + " does not exist.");
        return matchedPojo;
    }

    public List<OrderItemPojo> getByOrderId(Long orderId) {
        return orderItemDao.selectByOrderId(orderId);
    }

    @Transactional(rollbackFor = ApiException.class)
    public void allocateOrderItems(OrderItemPojo orderItem, Long quantityToBeAllocated) throws ApiException {
        orderItem.setAllocatedQuantity(orderItem.getAllocatedQuantity() + quantityToBeAllocated);
    }
}

