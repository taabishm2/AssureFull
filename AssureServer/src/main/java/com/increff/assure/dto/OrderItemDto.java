package com.increff.assure.dto;

import com.increff.assure.pojo.ChannelListingPojo;
import com.increff.assure.pojo.OrderItemPojo;
import com.increff.assure.service.*;
import model.data.OrderItemData;
import model.form.OrderItemForm;
import model.form.OrderItemValidationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.increff.assure.util.ConvertUtil.convert;

@Service
public class OrderItemDto extends AbstractDto {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private ProductMasterService productService;
    @Autowired
    private ChannelListingService channelListingService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private ChannelService channelService;

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

    public List<OrderItemData> getByOrderId(Long orderId) throws ApiException {
        List<OrderItemData> orderItemDataList = new ArrayList<>();
        for (OrderItemPojo pojo : orderItemService.getByOrderId(orderId)) {
            OrderItemData orderData = convert(pojo, OrderItemData.class);
            orderData.setClientSkuId(productService.getCheckId(pojo.getGlobalSkuId()).getClientSkuId());
            if(!channelService.getCheckId(orderService.getCheckId(orderId).getChannelId()).getName().equals("INTERNAL"))
                orderData.setChannelSkuId(channelListingService.getByChannelIdAndGlobalSku(orderService.getCheckId(orderId).getChannelId(), pojo.getGlobalSkuId()).getChannelSkuId());
            orderItemDataList.add(orderData);
        }
        return orderItemDataList;
    }

    public void validateChannelOrderItemForm(OrderItemValidationForm validationForm) throws ApiException {
        ChannelListingPojo listing = channelListingService.getUnique(validationForm.getChannelId(), validationForm.getChannelSkuId(), validationForm.getClientId());
        if (Objects.isNull(listing))
            throw new ApiException("Channel listing does not exist");

        Long globalSkuId = listing.getGlobalSkuId();

        if (validationForm.getOrderedQuantity() <= 0)
            throw new ApiException("Quantity must be positive");

         productService.getCheckId(globalSkuId);

        if(Objects.isNull(inventoryService.getByGlobalSku(globalSkuId)))
            throw new ApiException("Product Not in Inventory");

        if (validationForm.getOrderedQuantity() > inventoryService.getByGlobalSku(globalSkuId).getAvailableQuantity())
            throw new ApiException("Insufficient Stock" + inventoryService.getByGlobalSku(globalSkuId).getAvailableQuantity() + " items left");

    }
}
