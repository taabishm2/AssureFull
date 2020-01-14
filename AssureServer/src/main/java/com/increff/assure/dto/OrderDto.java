package com.increff.assure.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.increff.assure.pojo.BinSkuPojo;
import com.increff.assure.pojo.OrderItemPojo;
import com.increff.assure.pojo.OrderPojo;
import com.increff.assure.pojo.ProductMasterPojo;
import com.increff.assure.service.*;
import com.increff.assure.util.CheckValid;
import com.increff.assure.util.FileWriteUtil;
import com.increff.assure.util.PdfGenerateUtil;
import com.increff.assure.util.XmlGenerateUtil;
import model.ConsumerType;
import model.InvoiceType;
import model.OrderStatus;
import model.data.MessageData;
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

    @Transactional(rollbackFor = ApiException.class)
    public void add(ChannelOrderForm orderForm) throws ApiException {
        CheckValid.validate(orderForm);
        OrderPojo orderPojo = convert(orderForm, OrderPojo.class);
        validateOrder(orderPojo);

        orderService.add(orderPojo);

        addChanelOrderItemsForOrder(orderForm.getOrderItemList(), orderForm.getChannelId(), orderPojo.getId(), orderPojo.getClientId());
    }

    private void addChanelOrderItemsForOrder(List<ChannelOrderItemForm> orderItemFormList, Long channelId, Long orderId, Long clientId) throws ApiException {
        List<OrderItemPojo> orderItemList = convertFormToPojo(orderItemFormList, channelId, orderId, clientId);
        for (OrderItemPojo orderItem : orderItemList)
            validateOrderItem(orderItem, clientId);

        orderItemService.addList(orderItemList);
    }

    private List<OrderItemPojo> convertFormToPojo(List<ChannelOrderItemForm> orderItemFormList, Long channelId, Long orderId, Long clientId) {
        List<OrderItemPojo> orderItemList = new ArrayList<>();
        for (ChannelOrderItemForm form : orderItemFormList) {
            OrderItemPojo pojo = new OrderItemPojo();
            pojo.setOrderId(orderId);
            pojo.setGlobalSkuId(channelListingService.getUnique(channelId, form.getChannelSkuId(), clientId).getGlobalSkuId());
            pojo.setOrderedQuantity(form.getOrderedQuantity());
            orderItemList.add(pojo);
        }
        return orderItemList;
    }

    private void validateOrder(OrderPojo orderPojo) throws ApiException {
        if (!consumerService.getCheckId(orderPojo.getClientId()).getType().equals(ConsumerType.CLIENT))
            throw new ApiException("Invalid ClientID");

        if (channelService.getCheckId(orderPojo.getChannelId()).getInvoiceType().equals(InvoiceType.SELF))
            if (!consumerService.getCheckId(orderPojo.getCustomerId()).getType().equals(ConsumerType.CUSTOMER))
                throw new ApiException("Invalid CustomerID");

        orderService.checkDuplicateOrders(orderPojo);
        channelService.getCheckId(orderPojo.getChannelId());
    }

    @Transactional(rollbackFor = ApiException.class)
    private void addOrderItemsForOrder(List<OrderItemForm> orderItemFormList, Long orderId, Long clientId) throws ApiException {
        List<OrderItemPojo> orderItemList = convertFormToPojo(orderItemFormList, orderId, clientId);
        for (OrderItemPojo orderItem : orderItemList)
            validateOrderItem(orderItem, clientId);

        orderItemService.addList(orderItemList);
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
        orderData.setDateCreated(order.getCreatedAt().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")));
        orderData.setClientName(consumerService.getCheckId(order.getClientId()).getName());
        orderData.setCustomerName(consumerService.getCheckId(order.getCustomerId()).getName());
        orderData.setChannelName(channelService.getCheckId(order.getChannelId()).getName());
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
            if (!orderItem.getOrderedQuantity().equals(orderItem.getAllocatedQuantity()))
                return false;
        }
        return true;
    }

    @Transactional(rollbackFor = ApiException.class)
    public void runAllocation(Long orderId) throws ApiException {
        if (orderService.getCheckId(orderId).getStatus().equals(OrderStatus.ALLOCATED))
            throw new ApiException("Order is already ALLOCATED");

        if (orderService.getCheckId(orderId).getStatus().equals(OrderStatus.FULFILLED))
            throw new ApiException("Cannot ALLOCATE a FULFILLED Order");

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
        if (order.getStatus().equals(OrderStatus.CREATED))
            throw new ApiException("Order is not Allocated");

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
        orderInvoice.setOrderCreationTime(order.getCreatedAt().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")));

        List<OrderItemReceiptData> orderItems = new ArrayList<>();
        for (OrderItemPojo orderItem : orderItemService.getByOrderId(order.getId())) {
            OrderItemReceiptData orderItemReceipt = new OrderItemReceiptData();

            orderItemReceipt.setClientSkuId(productService.getCheckId(orderItem.getGlobalSkuId()).getClientSkuId());
            orderItemReceipt.setOrderItemId(orderItem.getId());
            orderItemReceipt.setQuantity(orderItem.getAllocatedQuantity());

            ProductMasterPojo product = productMasterService.getCheckId(orderItem.getGlobalSkuId());
            orderItemReceipt.setMrp(product.getMrp());
            orderItemReceipt.setTotal((long) (orderItem.getAllocatedQuantity() * product.getMrp()));

            orderItemReceipt.setChannelSkuId(channelListingService.getByChannelIdAndGlobalSku(order.getChannelId(),product.getId()).getChannelSkuId());

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
        return convertOrderPojoToOrderData(orderService.getByChannel(channelId));
    }

    public void validateList(List<OrderItemForm> formList, Long clientId, Long channelId) throws ApiException {
        consumerService.getCheckClient(clientId);
        channelService.getCheckId(channelId);

        List<MessageData> errorMessages = new ArrayList<>();
        HashSet<String> clientSkus = new HashSet<>();
        for (int i = 0; i < formList.size(); i++) {
            try {
                OrderItemForm form = formList.get(i);
                CheckValid.validate((form));

                if (clientSkus.contains(form.getClientSkuId()))
                    throw new ApiException("Duplicate Client SKU");
                else
                    clientSkus.add(form.getClientSkuId());

                Long globalSkuId = productService.getByClientAndClientSku(clientId, form.getClientSkuId()).getId();

                if (form.getOrderedQuantity() <= 0)
                    throw new ApiException("Quantity must be positive");

                if (!channelService.getCheckId(channelId).getName().equals("INTERNAL"))
                    if (Objects.isNull(channelListingService.getByChannelIdAndGlobalSku(channelId, globalSkuId)))
                        throw new ApiException("Channel does not provide the mentioned Product");

                checkNotNull(inventoryService.getByGlobalSku(globalSkuId), "Product not in Inventory");
                if (form.getOrderedQuantity() > inventoryService.getByGlobalSku(globalSkuId).getAvailableQuantity())
                    throw new ApiException("Insufficient Stock. " + inventoryService.getByGlobalSku(globalSkuId).getAvailableQuantity() + " items left");
            } catch (ApiException e) {
                MessageData errorMessage = new MessageData();
                errorMessage.setMessage("Error in Line: " + i + ": " + e.getMessage() + "\n");
                errorMessages.add(errorMessage);
            }
        }

        if (errorMessages.size() != 0)
            throw new ApiException(FileWriteUtil.writeErrorsToFile("orderError" + formList.hashCode(), errorMessages));
    }

    public List<OrderData> getSearch(Long clientId, Long customerId, Long channelId, String fromDate, String toDate) throws ApiException {
        if (Objects.nonNull(clientId))
            consumerService.getCheckClient(clientId);

        if (Objects.nonNull(customerId))
            consumerService.getCheckCustomer(customerId);

        if (Objects.nonNull(channelId))
            channelService.getCheckId(channelId);

/*        ZonedDateTime fromDateObject = ZonedDateTime.parse(fromDate);
        ZonedDateTime toDateObject = ZonedDateTime.parse(toDate);

        checkDateFilters(fromDateObject, toDateObject);

        if (Objects.isNull(fromDateObject) || Objects.isNull(toDateObject)) {
            if (Objects.nonNull(fromDateObject))
                toDateObject = ZonedDateTime.now();
            else
                fromDateObject = toDateObject.minusMonths(1L);

                fromDateObject.format(DateTimeFormatter.ofPattern("YYYY-mm-dd HH:mm:ss"))
        }*/

        List<OrderPojo> searchResults = orderService.getSearch(clientId, customerId, channelId,fromDate,toDate);

        return convertOrderPojoToOrderData(searchResults);
    }

    private void checkDateFilters(ZonedDateTime fromDate, ZonedDateTime toDate) throws ApiException {
        if (Objects.nonNull(fromDate))
            if (fromDate.isAfter(ZonedDateTime.now()))
                throw new ApiException("Invalid \"From\" Date");

        if (Objects.nonNull(toDate))
            if (fromDate.isAfter(ZonedDateTime.now()))
                throw new ApiException("Invalid \"To\" Date");

        if (Objects.nonNull(fromDate) && Objects.nonNull(toDate))
            if (fromDate.isAfter(toDate))
                throw new ApiException("\"From\" date cannot be after \"To\" date");
    }
}
