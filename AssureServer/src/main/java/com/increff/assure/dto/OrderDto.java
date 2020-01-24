package com.increff.assure.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.increff.assure.pojo.BinSkuPojo;
import com.increff.assure.pojo.OrderItemPojo;
import com.increff.assure.pojo.OrderPojo;
import com.increff.assure.pojo.ProductMasterPojo;
import com.increff.assure.service.*;
import com.increff.assure.util.DateUtil;
import com.increff.assure.util.PdfGenerateUtil;
import com.increff.assure.util.XmlGenerateUtil;
import model.ConsumerType;
import model.InvoiceType;
import model.OrderStatus;
import model.data.OrderData;
import model.data.OrderItemReceiptData;
import model.data.OrderReceiptData;
import model.form.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static com.increff.assure.util.ConvertUtil.convert;

@Service
public class OrderDto extends AbstractDto {
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
        checkValid(orderForm);

        OrderPojo orderPojo = convert(orderForm, OrderPojo.class);
        validateOrder(orderPojo);
        orderService.add(orderPojo);

        addOrderItemsForOrder(orderForm.getOrderItemList(), orderPojo.getId(), orderPojo.getClientId());
    }

    @Transactional(rollbackFor = ApiException.class)
    public void add(ChannelOrderForm orderForm) throws ApiException {
        checkValid(orderForm);

        OrderPojo orderPojo = convert(orderForm, OrderPojo.class);
        validateOrder(orderPojo);
        orderService.add(orderPojo);

        addChannelOrderItemsForOrder(orderForm.getOrderItemList(), orderForm.getChannelId(), orderPojo.getId(), orderPojo.getClientId());
    }

    private void validateOrder(OrderPojo orderPojo) throws ApiException {
        checkTrue(consumerService.getCheckId(orderPojo.getClientId()).getType().equals(ConsumerType.CLIENT), "Invalid ClientID");
        checkTrue(consumerService.getCheckId(orderPojo.getCustomerId()).getType().equals(ConsumerType.CUSTOMER), "Invalid CustomerID");
        channelService.getCheckId(orderPojo.getChannelId());
        orderService.checkDuplicateOrders(orderPojo);
    }

    private void addChannelOrderItemsForOrder(List<ChannelOrderItemForm> orderItemFormList, Long channelId, Long orderId, Long clientId) throws ApiException {
        List<OrderItemPojo> orderItemList = convertFormToPojo(orderItemFormList, channelId, orderId, clientId);
        for (OrderItemPojo orderItem : orderItemList)
            validateOrderItem(orderItem);

        orderItemService.addList(orderItemList);
    }

    private void addOrderItemsForOrder(List<OrderItemForm> orderItemFormList, Long orderId, Long clientId) throws ApiException {
        List<OrderItemPojo> orderItemList = convertFormToPojo(orderItemFormList, orderId, clientId);
        for (OrderItemPojo orderItem : orderItemList)
            validateOrderItem(orderItem);

        orderItemService.addList(orderItemList);
    }

    @Transactional(readOnly = true)
    public void validateOrderItem(OrderItemPojo orderItemPojo) throws ApiException {
        productService.getCheckId(orderItemPojo.getGlobalSkuId());

        Long clientId = orderService.getCheckId(orderItemPojo.getOrderId()).getClientId();
        checkTrue(clientId.equals(productService.getClientIdOfProduct(orderItemPojo.getGlobalSkuId())),
                "Invalid Client for Product(ID: " + orderItemPojo.getGlobalSkuId() + ").");

        Long channelId = orderService.getCheckId(orderItemPojo.getOrderId()).getChannelId();
        if (!channelService.getCheckId(channelId).getName().equals("INTERNAL"))
            checkNotNull(channelListingService.getByChannelIdAndGlobalSku(channelId, orderItemPojo.getGlobalSkuId()),
                    "Channel does not provide the mentioned Product");
    }

    @Transactional(readOnly = true)
    public OrderData get(Long id) throws ApiException {
        return convertPojoToData(orderService.getCheckId(id));
    }

    @Transactional(readOnly = true)
    public List<OrderData> getAll() throws ApiException {
        return convertPojoToData(orderService.getAll());
    }

    private List<OrderData> convertPojoToData(List<OrderPojo> orderPojoList) throws ApiException {
        List<OrderData> orderDataList = new ArrayList<>();
        for (OrderPojo pojo : orderPojoList)
            orderDataList.add(convertPojoToData(pojo));
        return orderDataList;
    }

    public boolean isOrderAllocated(Long orderId) {
        for (OrderItemPojo orderItem : orderItemService.getByOrderId(orderId)) {
            if (!orderItem.getOrderedQuantity().equals(orderItem.getAllocatedQuantity()))
                return false;
        }
        return true;
    }

    @Transactional(rollbackFor = ApiException.class)
    public void runAllocation(Long orderId) throws ApiException {
        checkFalse(orderService.getCheckId(orderId).getStatus().equals(OrderStatus.ALLOCATED), "Order is already ALLOCATED");
        checkFalse(orderService.getCheckId(orderId).getStatus().equals(OrderStatus.FULFILLED), "Cannot Allocate FULFILLED Order");

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
    public Long allocateFromAllBins(Long globalSku, Long quantityToAllocate) {
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
        checkFalse(order.getStatus().equals(OrderStatus.CREATED), "Order is not Allocated");

        if (order.getStatus().equals(OrderStatus.FULFILLED))
            return;

        if (channelService.getCheckId(order.getChannelId()).getInvoiceType().equals(InvoiceType.SELF)) {
            generateInvoicePdf(order);
        } else {
            clientWrapper.fetchInvoiceFromChannel(createOrderInvoice(order));
        }

        fulfillOrderItems(orderId);
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

        orderInvoice.setOrderCreationTime(order.getCreatedAt().format(DateTimeFormatter.ofPattern(DateUtil.getDateFormat())));

        List<OrderItemReceiptData> orderItems = new ArrayList<>();
        for (OrderItemPojo orderItem : orderItemService.getByOrderId(order.getId())) {
            OrderItemReceiptData orderItemReceipt = new OrderItemReceiptData();

            orderItemReceipt.setClientSkuId(productService.getCheckId(orderItem.getGlobalSkuId()).getClientSkuId());
            orderItemReceipt.setOrderItemId(orderItem.getId());
            orderItemReceipt.setQuantity(orderItem.getAllocatedQuantity());

            ProductMasterPojo product = productMasterService.getCheckId(orderItem.getGlobalSkuId());
            orderItemReceipt.setMrp(product.getMrp());
            orderItemReceipt.setTotal((long) (orderItem.getAllocatedQuantity() * product.getMrp()));

            if (!channelService.getCheckId(order.getChannelId()).getName().equals("INTERNAL"))
                orderItemReceipt.setChannelSkuId(channelListingService.getByChannelIdAndGlobalSku(order.getChannelId(), product.getId()).getChannelSkuId());

            orderItems.add(orderItemReceipt);
        }

        orderInvoice.setOrderItems(orderItems);
        return orderInvoice;
    }

    public void validateOrderForm(OrderValidationForm validationForm) throws ApiException {
        validateOrder(convert(validationForm, OrderPojo.class));
    }

    public List<OrderData> getByChannel(Long channelId) throws ApiException {
        channelService.getCheckId(channelId);
        return convertPojoToData(orderService.getByChannel(channelId));
    }

    //TODO try to consolidate lines based on functionality
    public void validateList(List<OrderItemForm> formList, Long clientId, Long channelId) throws ApiException {
        consumerService.getCheckClient(clientId);
        channelService.getCheckId(channelId);

        StringBuilder errorDetailString = new StringBuilder();
        HashSet<String> clientSkus = new HashSet<>();
        for (int index = 0; index < formList.size(); index++) {
            try {
                OrderItemForm form = formList.get(index);
                Long globalSkuId = productService.getByClientAndClientSku(clientId, form.getClientSkuId()).getId();

                checkValid((form));
                checkTrue(form.getOrderedQuantity() > 0, "Quantity must be positive");
                checkFalse(clientSkus.contains(form.getClientSkuId()), "Duplicate Client SKU");
                checkNotNull(inventoryService.getByGlobalSku(globalSkuId), "Product not in Inventory");
                checkTrue(form.getOrderedQuantity() <= inventoryService.getByGlobalSku(globalSkuId).getAvailableQuantity(),
                        "Insufficient Stock. " + inventoryService.getByGlobalSku(globalSkuId).getAvailableQuantity() + " items left");
                if (!channelService.getCheckId(channelId).getName().equals("INTERNAL"))
                    checkNotNull(channelListingService.getByChannelIdAndGlobalSku(channelId, globalSkuId),
                            "Channel does not provide the mentioned Product");

                clientSkus.add(form.getClientSkuId());
            } catch (ApiException e) {
                errorDetailString.append("Error in Line: ").append(index + 1).append(": ").append(e.getMessage()).append("<br \\>");
            }
        }

        if (errorDetailString.length() > 0)
            throw new ApiException(errorDetailString.toString());
    }

    public List<OrderData> getSearch(OrderSearchForm form) throws ApiException {
        if (Objects.nonNull(form.getClientId()))
            consumerService.getCheckClient(form.getClientId());

        if (Objects.nonNull(form.getCustomerId()))
            consumerService.getCheckCustomer(form.getCustomerId());

        if (Objects.nonNull(form.getChannelId()))
            channelService.getCheckId(form.getChannelId());

        DateUtil.checkDateFilters(form.getFromDate(), form.getToDate());
        ZonedDateTime[] dateList = DateUtil.setStartEndDates(form.getFromDate(), form.getToDate());

        return convertPojoToData(orderService.getSearch(form.getClientId(),
                form.getCustomerId(), form.getChannelId(), dateList[0], dateList[1]));
    }

    private List<OrderItemPojo> convertFormToPojo(List<ChannelOrderItemForm> orderItemFormList, Long channelId, Long orderId, Long clientId) {
        List<OrderItemPojo> orderItemList = new ArrayList<>();
        for (ChannelOrderItemForm form : orderItemFormList) {
            OrderItemPojo pojo = new OrderItemPojo();
            pojo.setOrderId(orderId);
            pojo.setGlobalSkuId(channelListingService.getByChannelChannelSkuAndClient(channelId, form.getChannelSkuId(), clientId).getGlobalSkuId());
            pojo.setOrderedQuantity(form.getOrderedQuantity());
            orderItemList.add(pojo);
        }
        return orderItemList;
    }

    private List<OrderItemPojo> convertFormToPojo(List<OrderItemForm> orderItemFormList, Long orderId, Long clientId) throws ApiException {
        List<OrderItemPojo> orderItemList = new ArrayList<>();
        for (OrderItemForm form : orderItemFormList) {
            OrderItemPojo pojo = new OrderItemPojo();
            pojo.setOrderId(orderId);
            pojo.setGlobalSkuId(productMasterService.getByClientAndClientSku(clientId, form.getClientSkuId()).getId());
            pojo.setOrderedQuantity(form.getOrderedQuantity());
            orderItemList.add(pojo);
        }
        return orderItemList;
    }

    private OrderData convertPojoToData(OrderPojo order) throws ApiException {
        OrderData orderData = convert(order, OrderData.class);
        orderData.setOrderItemList(convert(orderItemService.getByOrderId(order.getId()), OrderItemForm.class));
        orderData.setDateCreated(order.getCreatedAt());
        orderData.setClientName(consumerService.getCheckId(order.getClientId()).getName());
        orderData.setCustomerName(consumerService.getCheckId(order.getCustomerId()).getName());
        orderData.setChannelName(channelService.getCheckId(order.getChannelId()).getName());
        return orderData;
    }

    public void setClientWrapper(ClientWrapper clientWrapper) {
        this.clientWrapper = clientWrapper;
    }
}