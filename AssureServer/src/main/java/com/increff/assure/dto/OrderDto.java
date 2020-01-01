package com.increff.assure.dto;

import com.increff.assure.pojo.*;
import com.increff.assure.service.*;
import com.increff.assure.util.PdfGenerateUtil;
import com.increff.assure.util.XmlGenerateUtil;
import model.ConsumerType;
import model.OrderStatus;
import model.data.OrderData;
import model.data.OrderItemReceiptData;
import model.data.OrderReceiptData;
import model.form.OrderForm;
import model.form.OrderItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.increff.assure.util.ConvertUtil.convert;

@Service
public class OrderDto extends AbstractService {
    @Autowired
    private OrderService orderService;
    @Autowired
    private BinSkuService binSkuService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private ConsumerService consumerService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private ProductMasterService productMasterService;
    @Autowired
    private ProductMasterService productService;
    @Autowired
    private ChannelListingService channelListingService;

    @Transactional
    public void add(OrderForm orderForm) throws ApiException {

        OrderPojo orderPojo = convert(orderForm, OrderPojo.class);
        validateOrder(orderPojo);
        orderService.add(orderPojo);

        List<OrderItemPojo> orderItemList = getOrderItemPojoList(orderForm.getOrderItemList(), orderPojo.getId());
        Long clientId = orderPojo.getClientId();
        for (OrderItemPojo orderItem : orderItemList)
            validateOrderItem(orderItem, clientId);

        orderItemService.addList(orderItemList);
    }

    private List<OrderItemPojo> getOrderItemPojoList(List<OrderItemForm> orderItemList, Long orderId) throws ApiException {
        List<OrderItemPojo> orderItemPojoList = new ArrayList<>();

        for(OrderItemForm orderItemForm:orderItemList){
            OrderItemPojo orderItemPojo = convert(orderItemForm, OrderItemPojo.class);
            orderItemPojo.setOrderId(orderId);
            orderItemPojoList.add(orderItemPojo);
        }
        return orderItemPojoList;
    }

    private void validateOrderItem(OrderItemPojo orderItemPojo, Long clientId) throws ApiException {
        productService.getCheckId(orderItemPojo.getGlobalSkuId());

        if (!clientId.equals(productService.getClientIdOfProduct(orderItemPojo.getGlobalSkuId())))
            throw new ApiException("Invalid Client for Product(ID: " + orderItemPojo.getGlobalSkuId() + ").");
    }

    public OrderData get(Long id) throws ApiException {
        OrderPojo orderPojo = orderService.getCheckId(id);
        return convert(orderPojo, OrderData.class);
    }

    public List<OrderData> getAll() throws ApiException {
        return convert(orderService.getAll(), OrderData.class);
    }

    private void validateOrder(OrderPojo orderPojo) throws ApiException {
        if (!consumerService.getCheckId(orderPojo.getClientId()).getType().equals(ConsumerType.CLIENT))
            throw new ApiException("Specified ClientID beLongs to a Customer");

        if (!consumerService.getCheckId(orderPojo.getCustomerId()).getType().equals(ConsumerType.CUSTOMER))
            throw new ApiException("Specified CustomerID beLongs to a Client");

        checkNotNull(channelService.getCheckId(orderPojo.getChannelId()), "Channel does not exist");
    }

    public boolean isOrderAllocated(Long orderId) {
        for (OrderItemPojo orderItem : orderItemService.getByOrderId(orderId)) {
            if (orderItem.getOrderedQuantity().equals(orderItem.getAllocatedQuantity()))
                return false;
        }
        return true;
    }

    @Transactional(rollbackOn = ApiException.class)
    public void runAllocation(Long orderId) throws ApiException {
        Long globalSkuOfOrderItem, orderedQuantity, allocatedOrderItemQuantity;

        for (OrderItemPojo orderItem : orderItemService.getByOrderId(orderId)) {

            globalSkuOfOrderItem = orderItem.getGlobalSkuId();
            orderedQuantity = orderItem.getOrderedQuantity() - orderItem.getAllocatedQuantity();

            allocatedOrderItemQuantity = allocateFromAllBins(globalSkuOfOrderItem, orderedQuantity);

            if (allocatedOrderItemQuantity == 0)
                continue;
            inventoryService.allocateAvailableItems(globalSkuOfOrderItem, allocatedOrderItemQuantity);
            orderItemService.allocateOrderItems(orderItem, allocatedOrderItemQuantity);
        }
        updateOrderStatusToAllocated(orderId);
    }

    public void updateOrderStatusToAllocated(Long orderId) throws ApiException {
        if (isOrderAllocated(orderId))
            orderService.updateStatus(orderId, OrderStatus.ALLOCATED);
    }

    public Long allocateFromAllBins(Long globalSku, Long quantityToAllocate) throws ApiException {
        Long remainingQuantityToAllocate = quantityToAllocate;
        List<BinSkuPojo> allBinSkus = binSkuService.selectBinsByGlobalSku(globalSku);
        Collections.sort(allBinSkus);

        for (BinSkuPojo binSkuPojo : allBinSkus) {
            remainingQuantityToAllocate = binSkuService.removeFromBin(binSkuPojo, remainingQuantityToAllocate);
            if (remainingQuantityToAllocate == 0) return 0L;
        }
        return quantityToAllocate - remainingQuantityToAllocate;
    }

    public void fulfillOrder(Long orderId) throws ApiException {
        OrderPojo order = orderService.getCheckId(orderId);
        if (!order.getStatus().equals(OrderStatus.ALLOCATED))
            throw new ApiException("Order is not Allocated");

        generateInvoicePdf(order);
        fulfillOrderItems(orderId);

        order.setStatus(OrderStatus.FULFILLED);
    }

    private void fulfillOrderItems(Long orderId) {
        Long allocatedOrderItemQuantity = 0L;
        for (OrderItemPojo orderItem : orderItemService.getByOrderId(orderId)) {
            allocatedOrderItemQuantity = orderItem.getAllocatedQuantity();
            orderItem.setFulfilledQuantity(orderItem.getFulfilledQuantity() + allocatedOrderItemQuantity);
            orderItem.setAllocatedQuantity(0L);

            fulfillInInventory(orderItem.getGlobalSkuId(), allocatedOrderItemQuantity);
        }
    }

    private void fulfillInInventory(Long globalSkuId, Long allocatedOrderItemQuantity) {
        InventoryPojo inventoryPojo = inventoryService.getByGlobalSku(globalSkuId);
        inventoryPojo.setAllocatedQuantity(inventoryPojo.getAvailableQuantity() - allocatedOrderItemQuantity);
        inventoryPojo.setFulfilledQuantity(inventoryPojo.getFulfilledQuantity() + allocatedOrderItemQuantity);
    }

    private void generateInvoicePdf(OrderPojo order) throws ApiException {
        OrderReceiptData orderReceipt = createOrderInvoice(order);
        XmlGenerateUtil.generate(orderReceipt);
        PdfGenerateUtil.generate(order.getId());
    }

    private OrderReceiptData createOrderInvoice(OrderPojo order) throws ApiException {
        OrderReceiptData orderInvoice = new OrderReceiptData();
        orderInvoice.setOrderId(order.getId());
        orderInvoice.setChannelName(channelService.getCheckId(order.getChannelId()).getName());
        orderInvoice.setChannelOrderId(order.getChannelOrderId());
        orderInvoice.setClientDetails(consumerService.getCheckId(order.getClientId()).getName());
        orderInvoice.setCustomerDetails(consumerService.getCheckId(order.getCustomerId()).getName());
        orderInvoice.setOrderCreationTime(order.getCreatedAt());

        List<OrderItemReceiptData> orderItems = new ArrayList<>();
        for (OrderItemPojo orderItem : orderItemService.getByOrderId(order.getId())) {
            OrderItemReceiptData orderItemReceipt = new OrderItemReceiptData();

            orderItemReceipt.setClientSkuId(channelListingService.getByChannelIdAndGlobalSku(order.getChannelId(), orderItem.getGlobalSkuId()).getChannelSkuId());
            orderItemReceipt.setOrderItemId(orderItem.getId());
            orderItemReceipt.setQuantity(orderItem.getAllocatedQuantity());

            //TODO: Get all data as a list before loop
            ProductMasterPojo product = productMasterService.getCheckId(orderItem.getGlobalSkuId());
            orderItemReceipt.setMrp(product.getMrp());
            orderItemReceipt.setTotal((long) (orderItem.getAllocatedQuantity() * product.getMrp()));

            orderItems.add(orderItemReceipt);
        }

        orderInvoice.setOrderItems(orderItems);
        return orderInvoice;
    }
}
