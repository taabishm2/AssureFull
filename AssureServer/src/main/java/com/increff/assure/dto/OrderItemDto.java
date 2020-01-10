package com.increff.assure.dto;

import com.increff.assure.pojo.ChannelListingPojo;
import com.increff.assure.pojo.OrderItemPojo;
import com.increff.assure.pojo.ProductMasterPojo;
import com.increff.assure.service.*;
import model.data.OrderItemData;
import model.form.OrderItemForm;
import model.form.OrderItemValidationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Objects;

import static com.increff.assure.util.ConvertUtil.convert;

@Service
public class OrderItemDto {
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

    public void validateOrderItemForm(OrderItemValidationForm validationForm) throws ApiException {
        ProductMasterPojo product = productService.getCheckId(validationForm.getGlobalSkuId());
        if(!product.getClientId().equals(validationForm.getClientId()))
            throw new ApiException("Client does not provide the mentioned Product");

        ChannelListingPojo channelListing = channelListingService.getByChannelIdAndGlobalSku(validationForm.getChannelId(), validationForm.getGlobalSkuId());
        if(Objects.isNull(channelListing))
            throw new ApiException("Channel does not provide the mentioned Product");

        if(!channelListing.getChannelId().equals(validationForm.getChannelId()))
            throw new ApiException("Channel does not provide the mentioned Product");

        System.out.println("");
        if(validationForm.getOrderedQuantity() > inventoryService.getByGlobalSku(product.getId()).getAvailableQuantity())
            throw new ApiException("Insufficient Stock.");
    }
}
