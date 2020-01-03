package com.increff.assure.dto;

import com.increff.assure.pojo.OrderItemPojo;
import com.increff.assure.service.ApiException;
import com.increff.assure.service.OrderItemService;
import com.increff.assure.service.OrderService;
import com.increff.assure.service.ProductMasterService;
import model.data.OrderItemData;
import model.form.OrderItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static com.increff.assure.util.ConvertUtil.convert;

@Service
public class OrderItemDto {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private ProductMasterService productService;

    public void add(OrderItemForm form) throws ApiException {
        OrderItemPojo orderItemPojo = convert(form, OrderItemPojo.class);
        validateOrderItem(orderItemPojo);
        orderItemService.add(orderItemPojo);
    }

    public OrderItemData get(Long id) throws ApiException {
        OrderItemPojo orderItemPojo = orderItemService.getCheckId(id);
        return convert(orderItemPojo, OrderItemData.class);
    }

    public List<OrderItemData> getAll() throws ApiException {
        return convert(orderItemService.getAll(), OrderItemData.class);
    }


    private void validateOrderItem(OrderItemPojo orderItemPojo) throws ApiException {
        orderService.getCheckId(orderItemPojo.getOrderId());
        productService.getCheckId(orderItemPojo.getGlobalSkuId());

        if (!orderService.getOrderClient(orderItemPojo.getOrderId()).equals(productService.getClientIdOfProduct(orderItemPojo.getGlobalSkuId())))
            throw new ApiException("Invalid Client for Product(ID: " + orderItemPojo.getGlobalSkuId() + ").");
    }
}
