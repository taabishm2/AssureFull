package com.increff.assure.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.increff.assure.pojo.BinSkuPojo;
import com.increff.assure.pojo.OrderItemPojo;
import com.increff.assure.pojo.OrderPojo;
import com.increff.assure.pojo.ProductMasterPojo;
import com.increff.assure.service.*;
import com.increff.assure.util.CheckValid;
import com.increff.assure.util.PdfGenerateUtil;
import com.increff.assure.util.XmlGenerateUtil;
import model.ConsumerType;
import model.InvoiceType;
import model.OrderStatus;
import model.data.OrderData;
import model.data.OrderItemReceiptData;
import model.data.OrderReceiptData;
import model.form.OrderForm;
import model.form.OrderItemForm;
import model.form.OrderItemValidationForm;
import model.form.OrderValidationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private ClientWrapper clientWrapper;

    @Transactional(rollbackFor = ApiException.class)
    public void add(OrderForm orderForm) throws ApiException {
        CheckValid.validate(orderForm);
        OrderPojo orderPojo = convert(orderForm, OrderPojo.class);
        validateOrder(orderPojo);

        orderService.add(orderPojo);

        addOrderItemsForOrder(orderForm.getOrderItemList(), orderPojo.getId(), orderPojo.getClientId());
    }

    private void validateOrder(OrderPojo orderPojo) throws ApiException {
        if (!consumerService.getCheckId(orderPojo.getClientId()).getType().equals(ConsumerType.CLIENT))
            throw new ApiException("Invalid ClientID");

        if (!consumerService.getCheckId(orderPojo.getCustomerId()).getType().equals(ConsumerType.CUSTOMER))
            throw new ApiException("Invalid CustomerID");

        channelService.getCheckId(orderPojo.getChannelId());
    }

    @Transactional(rollbackFor = ApiException.class)
    private void addOrderItemsForOrder(List<OrderItemForm> orderItemFormList, Long orderId, Long clientId) throws ApiException {
        List<OrderItemPojo> orderItemList = convert(orderItemFormList, OrderItemPojo.class);
        orderItemList.forEach(orderItem->orderItem.setOrderId(orderId));
        for (OrderItemPojo orderItem : orderItemList)
            validateOrderItem(orderItem, clientId);

        orderItemService.addList(orderItemList);
    }

    @Transactional(readOnly = true)
    private void validateOrderItem(OrderItemPojo orderItemPojo, Long clientId) throws ApiException {
        productService.getCheckId(orderItemPojo.getGlobalSkuId());

        if (!clientId.equals(productService.getClientIdOfProduct(orderItemPojo.getGlobalSkuId())))
            throw new ApiException("Invalid Client for Product(ID: " + orderItemPojo.getGlobalSkuId() + ").");
    }

    @Transactional(readOnly = true)
    public OrderData get(Long id) throws ApiException {
        return convertOrderPojoToOrderData(orderService.getCheckId(id));
    }

    private OrderData convertOrderPojoToOrderData(OrderPojo order) throws ApiException {
        OrderData orderData = convert(order, OrderData.class);
        orderData.setOrderItemList(convert(orderItemService.getByOrderId(order.getId()), OrderItemForm.class));
        return orderData;
    }

    @Transactional(readOnly = true)
    public List<OrderData> getAll() throws ApiException {
        return convertOrderPojoToOrderData(orderService.getAll());
    }

    private List<OrderData> convertOrderPojoToOrderData(List<OrderPojo> orderPojoList) throws ApiException {
        List<OrderData> orderDataList = new ArrayList<>();
        for (OrderPojo pojo : orderPojoList)
            orderDataList.add(convertOrderPojoToOrderData(pojo));
        return orderDataList;
    }

    public boolean isOrderAllocated(Long orderId) {
        for (OrderItemPojo orderItem : orderItemService.getByOrderId(orderId)) {
            if (orderItem.getOrderedQuantity().equals(orderItem.getAllocatedQuantity()))
                return false;
        }
        return true;
    }

    @Transactional(rollbackFor = ApiException.class)
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

    @Transactional(rollbackFor = ApiException.class)
    public void updateOrderStatusToAllocated(Long orderId) throws ApiException {
        if (isOrderAllocated(orderId))
            orderService.updateStatus(orderId, OrderStatus.ALLOCATED);
    }

    @Transactional(rollbackFor = ApiException.class)
    public Long allocateFromAllBins(Long globalSku, Long quantityToAllocate) throws ApiException {
        Long remainingQuantityToAllocate = quantityToAllocate;

        List<BinSkuPojo> allBinSkus = binSkuService.selectBinsByGlobalSku(globalSku);
        sortByQuantity(allBinSkus);

        for (BinSkuPojo binSkuPojo : allBinSkus) {
            remainingQuantityToAllocate = binSkuService.removeFromBin(binSkuPojo, remainingQuantityToAllocate);
            if (remainingQuantityToAllocate == 0) return quantityToAllocate;
        }
        return quantityToAllocate - remainingQuantityToAllocate;
    }

    private void sortByQuantity(List<BinSkuPojo> allBinSkus) {
        allBinSkus.sort((bin1, bin2) -> (bin2.getQuantity()).compareTo(bin1.getQuantity()));
    }

    @Transactional(rollbackFor = ApiException.class)
    public void fulfillOrder(Long orderId) throws ApiException, JsonProcessingException {
        OrderPojo order = orderService.getCheckId(orderId);
        if (!order.getStatus().equals(OrderStatus.ALLOCATED))
            throw new ApiException("Order is not Allocated");

        if (channelService.getCheckId(order.getChannelId()).getInvoiceType().equals(InvoiceType.SELF)) {
            generateInvoicePdf(order);
            fulfillOrderItems(orderId);
        } else {
            clientWrapper.fetchInvoiceFromChannel(createOrderInvoice(order));
        }

        order.setStatus(OrderStatus.FULFILLED);
    }

    private void fulfillOrderItems(Long orderId) {
        Long allocatedOrderItemQuantity = 0L;
        for (OrderItemPojo orderItem : orderItemService.getByOrderId(orderId)) {
            allocatedOrderItemQuantity = orderItem.getAllocatedQuantity();
            orderItem.setFulfilledQuantity(orderItem.getFulfilledQuantity() + allocatedOrderItemQuantity);
            orderItem.setAllocatedQuantity(0L);

            inventoryService.fulfillInInventory(orderItem.getGlobalSkuId(), allocatedOrderItemQuantity);
        }
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
        orderInvoice.setOrderCreationTime(order.getCreatedAt().toString());

        List<OrderItemReceiptData> orderItems = new ArrayList<>();
        for (OrderItemPojo orderItem : orderItemService.getByOrderId(order.getId())) {
            OrderItemReceiptData orderItemReceipt = new OrderItemReceiptData();

            orderItemReceipt.setClientSkuId(channelListingService.getByChannelIdAndGlobalSku(order.getChannelId(), orderItem.getGlobalSkuId()).getChannelSkuId());
            orderItemReceipt.setOrderItemId(orderItem.getId());
            orderItemReceipt.setQuantity(orderItem.getAllocatedQuantity());

            ProductMasterPojo product = productMasterService.getCheckId(orderItem.getGlobalSkuId());
            orderItemReceipt.setMrp(product.getMrp());
            orderItemReceipt.setTotal((long) (orderItem.getAllocatedQuantity() * product.getMrp()));

            orderItems.add(orderItemReceipt);
        }

        orderInvoice.setOrderItems(orderItems);
        return orderInvoice;
    }

    public void validateOrderForm(OrderValidationForm validationForm) throws ApiException {
        validateOrder(convert(validationForm, OrderPojo.class));
    }
}
