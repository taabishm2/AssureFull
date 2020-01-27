package com.increff.assure.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.increff.assure.dao.*;
import com.increff.assure.pojo.*;
import com.increff.assure.service.ApiException;
import com.increff.assure.service.ClientWrapper;
import model.ConsumerType;
import model.InvoiceType;
import model.OrderStatus;
import model.data.OrderReceiptData;
import model.form.ChannelOrderItemForm;
import model.form.OrderForm;
import model.form.OrderItemForm;
import model.form.OrderValidationForm;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;

public class OrderDtoTest extends AbstractUnitTest {

    @Autowired
    OrderDto orderDto;
    @Autowired
    OrderDao orderDao;
    @Autowired
    ConsumerDao consumerDao;
    @Autowired
    ProductMasterDao productDao;
    @Autowired
    ChannelDao channelDao;
    @Autowired
    BinDao binDao;
    @Autowired
    BinSkuDao binSkuDao;
    @Autowired
    InventoryDao inventoryDao;
    @Autowired
    OrderItemDao orderItemDao;
    @Autowired
    ChannelListingDao listingDao;
    @Mock
    ClientWrapper mockClientWrapper;

    private ConsumerPojo clientPuma;
    private ConsumerPojo clientNike;
    private ConsumerPojo customer;
    private ChannelPojo channelSnapdeal;
    private ChannelPojo channelFlipkart;
    private ProductMasterPojo productPuma;
    private ProductMasterPojo productPumaA;
    private ProductMasterPojo productNike;
    private OrderForm pumaOrderForm;
    private OrderForm nikeOrderForm;
    private List<OrderItemForm> orderItemFormList;
    private BinPojo bin1, bin2, bin3, bin4;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        clientPuma = TestPojo.getConsumerPojo("PUMA", ConsumerType.CLIENT);
        clientNike = TestPojo.getConsumerPojo("NIKE", ConsumerType.CLIENT);
        customer = TestPojo.getConsumerPojo("Customer NameZ", ConsumerType.CUSTOMER);
        consumerDao.insert(clientPuma);
        consumerDao.insert(clientNike);
        consumerDao.insert(customer);

        productPuma = TestPojo.getProductPojo("PumaProduct", clientPuma.getId(), "Puma Brand", 320D, "Puma", "Puma Description");
        productPumaA = TestPojo.getProductPojo("PumaProductA", clientPuma.getId(), "Puma BrandA", 320D, "PumaA", "PumaA Description");
        productNike = TestPojo.getProductPojo("NikeProduct", clientNike.getId(), "Nike Brand", 120D, "NikeProd", "Nike Description");

        productDao.insert(productNike);
        productDao.insert(productPuma);
        productDao.insert(productPumaA);

        binDao.insert(TestPojo.getBinPojo());

        bin1 = TestPojo.getBinPojo();
        bin2 = TestPojo.getBinPojo();
        bin3 = TestPojo.getBinPojo();
        bin4 = TestPojo.getBinPojo();
        binDao.insert(bin1);
        binDao.insert(bin2);
        binDao.insert(bin3);
        binDao.insert(bin4);

        binSkuDao.insert(TestPojo.getBinSkuPojo(productPuma.getId(), bin1.getId(), 8L));
        binSkuDao.insert(TestPojo.getBinSkuPojo(productPumaA.getId(), bin1.getId(), 0L));
        binSkuDao.insert(TestPojo.getBinSkuPojo(productNike.getId(), bin1.getId(), 10L));

        binSkuDao.insert(TestPojo.getBinSkuPojo(productPuma.getId(), bin2.getId(), 6L));
        binSkuDao.insert(TestPojo.getBinSkuPojo(productPumaA.getId(), bin2.getId(), 9L));
        binSkuDao.insert(TestPojo.getBinSkuPojo(productNike.getId(), bin2.getId(), 0L));

        binSkuDao.insert(TestPojo.getBinSkuPojo(productPuma.getId(), bin3.getId(), 10L));
        binSkuDao.insert(TestPojo.getBinSkuPojo(productPumaA.getId(), bin3.getId(), 6L));
        binSkuDao.insert(TestPojo.getBinSkuPojo(productNike.getId(), bin3.getId(), 16L));

        binSkuDao.insert(TestPojo.getBinSkuPojo(productPuma.getId(), bin4.getId(), 7L));
        binSkuDao.insert(TestPojo.getBinSkuPojo(productPumaA.getId(), bin4.getId(), 11L));
        binSkuDao.insert(TestPojo.getBinSkuPojo(productNike.getId(), bin4.getId(), 20L));

        inventoryDao.insert(TestPojo.getInventoryPojo(productPuma.getId(), 31L, 0L, 0L));
        inventoryDao.insert(TestPojo.getInventoryPojo(productPumaA.getId(), 26L, 0L, 0L));
        inventoryDao.insert(TestPojo.getInventoryPojo(productNike.getId(), 46L, 0L, 0L));

        channelSnapdeal = TestPojo.getChannelPojo("SNAPDEAL", InvoiceType.SELF);
        channelFlipkart = TestPojo.getChannelPojo("FLIPKART", InvoiceType.CHANNEL);
        channelDao.insert(channelSnapdeal);
        channelDao.insert(channelFlipkart);

        listingDao.insert(TestPojo.getChannelListingPojo(productPuma.getId(), channelSnapdeal.getId(), "CSKU-PUMA", clientPuma.getId()));
        listingDao.insert(TestPojo.getChannelListingPojo(productPumaA.getId(), channelSnapdeal.getId(), "CSKU-PUMA-A", clientPuma.getId()));

        listingDao.insert(TestPojo.getChannelListingPojo(productPuma.getId(), channelFlipkart.getId(), "CSKUPUMA", clientPuma.getId()));
        listingDao.insert(TestPojo.getChannelListingPojo(productNike.getId(), channelFlipkart.getId(), "CSKUNIKE", clientNike.getId()));

        List<OrderItemForm> pumaOrderItemFormList = new ArrayList<>();
        pumaOrderItemFormList.add(TestForm.getConstructOrderItem(productPuma.getClientSkuId(), 15L));
        pumaOrderItemFormList.add(TestForm.getConstructOrderItem(productPumaA.getClientSkuId(), 21L));

        pumaOrderForm = TestForm.getConstructOrder(customer.getId(), clientPuma.getId(), channelSnapdeal.getId(), "DEFAULT CHANNEL ORDER ID PUMA", pumaOrderItemFormList);

        List<OrderItemForm> nikeOrderItemFormList = new ArrayList<>();
        nikeOrderItemFormList.add(TestForm.getConstructOrderItem(productNike.getClientSkuId(), 80L));

        nikeOrderForm = TestForm.getConstructOrder(customer.getId(), clientNike.getId(), channelSnapdeal.getId(), "DEFAULT CHANNEL ORDER ID NIKE", nikeOrderItemFormList);

    }

    @Test
    public void testAddWithInvalidClient() throws ApiException {
        try {
            pumaOrderForm.setClientId(customer.getId());
            orderDto.add(pumaOrderForm);
            fail("Customer Id set as Client ID");
        } catch (ApiException e) {
            pumaOrderForm.setClientId(clientPuma.getId());
            assertTrue(true);
        }
    }

    @Test
    public void testAddWithInvalidCustomer(){
        try {
            pumaOrderForm.setCustomerId(clientNike.getId());
            orderDto.add(pumaOrderForm);
            fail("Client Id set as Customer ID");
        } catch (ApiException e) {
            pumaOrderForm.setCustomerId(customer.getId());
            assertTrue(true);
        }
    }

    @Test
    public void testAddWithInvalidCustomerId() {
        try {
            pumaOrderForm.setCustomerId(87283L);
            orderDto.add(pumaOrderForm);
            fail("Invalid Consumer ID");
        } catch (ApiException e) {
            pumaOrderForm.setCustomerId(customer.getId());
            assertTrue(true);
        }
    }

    @Test
    public void  testAddWithInvalidChannelId() {
        try {
            pumaOrderForm.setChannelId(123L);
            orderDto.add(pumaOrderForm);
            fail("Invalid Channel ID used");
        } catch (ApiException e) {
            pumaOrderForm.setChannelId(channelSnapdeal.getId());
            assertTrue(true);
        }
    }

    @Test
    public void testAddWithDuplicateOrder() {
        try {
            orderDto.add(pumaOrderForm);
            orderDto.add(pumaOrderForm);
            fail("Duplicate order inserted");
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testAddWithInvalidClientProductPair() {
        try {
            List<OrderItemForm> pumaOrderItemFormList = new ArrayList<>();
            pumaOrderItemFormList.add(TestForm.getConstructOrderItem(productPuma.getClientSkuId(), 15L));
            pumaOrderItemFormList.add(TestForm.getConstructOrderItem(productNike.getClientSkuId(), 21L));

            OrderForm pumaOrderForm = TestForm.getConstructOrder(customer.getId(), clientPuma.getId(), channelSnapdeal.getId(), "CHANNEL ORDER ID PUMA", pumaOrderItemFormList);
            orderDto.add(pumaOrderForm);
            fail("Invalid Product and Client combination");
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testAddWithDuplicateOrderIdAndProduct() {
        try {
            List<OrderItemForm> pumaOrderItemFormList = new ArrayList<>();
            pumaOrderItemFormList.add(TestForm.getConstructOrderItem(productPuma.getClientSkuId(), 15L));
            pumaOrderItemFormList.add(TestForm.getConstructOrderItem(productPuma.getClientSkuId(), 21L));

            OrderForm pumaOrderForm = TestForm.getConstructOrder(customer.getId(), clientPuma.getId(), channelSnapdeal.getId(), "CHANNEL ID PUMA", pumaOrderItemFormList);
            orderDto.add(pumaOrderForm);
            fail("Duplicate OrderItems in Order");
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testAddForExternalChannelWIthValidChannelListing() throws ApiException {
        nikeOrderForm.setChannelId(channelFlipkart.getId());
        orderDto.add(nikeOrderForm);

        assertEquals(1, orderDao.selectAll().size());
        pumaOrderForm.setChannelId(channelSnapdeal.getId());
   }

    @Test
    public void testAddForExternalChannelWithInvalidChannelListing() throws ApiException {
        pumaOrderForm.setChannelId(channelFlipkart.getId());
        try{
            orderDto.add(pumaOrderForm);
            fail("Items in order are not registered for Channel");
        }catch (ApiException e){
            assertTrue(true);
            pumaOrderForm.setChannelId(channelSnapdeal.getId());
        }
    }

    @Test
    public void testGet() throws ApiException {
        OrderPojo order = TestPojo.getOrderPojo(customer.getId(), clientPuma.getId(), channelSnapdeal.getId(), "ABCL", OrderStatus.CREATED);
        orderDao.insert(order);

        assertEquals(order.getChannelOrderId(), orderDto.get(order.getId()).getChannelOrderId());
    }

    @Test
    public void testGetAllWithEmptyTable() throws ApiException {
        assertEquals(0, orderDto.getAll().size());
    }

    @Test
    public void testGetAll() throws ApiException {
        orderDao.insert( TestPojo.getOrderPojo(customer.getId(), clientPuma.getId(), channelSnapdeal.getId(), "ABCL", OrderStatus.CREATED));
        assertEquals(1, orderDto.getAll().size());

        orderDao.insert( TestPojo.getOrderPojo(customer.getId(), clientPuma.getId(), channelSnapdeal.getId(), "ABCD", OrderStatus.CREATED));
        assertEquals(2, orderDto.getAll().size());
    }

    @Test
    public void testRunAllocation() throws ApiException {
        listingDao.insert(TestPojo.getChannelListingPojo(productNike.getId(), channelSnapdeal.getId(), "CSKU-NIKE", clientNike.getId()));
        orderDto.add(pumaOrderForm);
        orderDto.add(nikeOrderForm);

        assertEquals(31L,(long) inventoryDao.selectByGlobalSku(productPuma.getId()).getAvailableQuantity());
        assertEquals(26L,(long) inventoryDao.selectByGlobalSku(productPumaA.getId()).getAvailableQuantity());
        assertEquals(46L,(long) inventoryDao.selectByGlobalSku(productNike.getId()).getAvailableQuantity());

        orderDto.runAllocation(orderDao.selectByChannelAndChannelOrderId(channelSnapdeal.getId(), "DEFAULT CHANNEL ORDER ID PUMA").getId());
        orderDto.runAllocation(orderDao.selectByChannelAndChannelOrderId(channelSnapdeal.getId(), "DEFAULT CHANNEL ORDER ID NIKE").getId());

        assertEquals(16L,(long) inventoryDao.selectByGlobalSku(productPuma.getId()).getAvailableQuantity());
        assertEquals(5L,(long) inventoryDao.selectByGlobalSku(productPumaA.getId()).getAvailableQuantity());
        assertEquals(0L,(long) inventoryDao.selectByGlobalSku(productNike.getId()).getAvailableQuantity());

        assertEquals(15L,(long) inventoryDao.selectByGlobalSku(productPuma.getId()).getAllocatedQuantity());
        assertEquals(21L,(long) inventoryDao.selectByGlobalSku(productPumaA.getId()).getAllocatedQuantity());
        assertEquals(46L,(long) inventoryDao.selectByGlobalSku(productNike.getId()).getAllocatedQuantity());

        assertEquals(0L,(long) inventoryDao.selectByGlobalSku(productPuma.getId()).getFulfilledQuantity());
        assertEquals(0L,(long) inventoryDao.selectByGlobalSku(productPumaA.getId()).getFulfilledQuantity());
        assertEquals(0L,(long) inventoryDao.selectByGlobalSku(productNike.getId()).getFulfilledQuantity());

        assertEquals(3L, (long) binSkuDao.selectByBinIdAndGlobalSku(bin1.getId(), productPuma.getId()).getQuantity());
        assertEquals(6L, (long) binSkuDao.selectByBinIdAndGlobalSku(bin2.getId(), productPuma.getId()).getQuantity());
        assertEquals(0L, (long) binSkuDao.selectByBinIdAndGlobalSku(bin3.getId(), productPuma.getId()).getQuantity());
        assertEquals(7L, (long) binSkuDao.selectByBinIdAndGlobalSku(bin4.getId(), productPuma.getId()).getQuantity());

        assertEquals(0L, (long) binSkuDao.selectByBinIdAndGlobalSku(bin1.getId(), productPumaA.getId()).getQuantity());
        assertEquals(0L, (long) binSkuDao.selectByBinIdAndGlobalSku(bin2.getId(), productPumaA.getId()).getQuantity());
        assertEquals(5L, (long) binSkuDao.selectByBinIdAndGlobalSku(bin3.getId(), productPumaA.getId()).getQuantity());
        assertEquals(0L, (long) binSkuDao.selectByBinIdAndGlobalSku(bin4.getId(), productPumaA.getId()).getQuantity());

        assertEquals(0L, (long) binSkuDao.selectByBinIdAndGlobalSku(bin1.getId(), productNike.getId()).getQuantity());
        assertEquals(0L, (long) binSkuDao.selectByBinIdAndGlobalSku(bin2.getId(), productNike.getId()).getQuantity());
        assertEquals(0L, (long) binSkuDao.selectByBinIdAndGlobalSku(bin3.getId(), productNike.getId()).getQuantity());
        assertEquals(0L, (long) binSkuDao.selectByBinIdAndGlobalSku(bin4.getId(), productNike.getId()).getQuantity());

        assertEquals(OrderStatus.ALLOCATED, orderDao.selectByChannelAndChannelOrderId(pumaOrderForm.getChannelId(), pumaOrderForm.getChannelOrderId()).getStatus());
        assertEquals(OrderStatus.CREATED, orderDao.selectByChannelAndChannelOrderId(nikeOrderForm.getChannelId(), nikeOrderForm.getChannelOrderId()).getStatus());

        try{
            orderDto.runAllocation(orderDao.selectByChannelAndChannelOrderId(channelSnapdeal.getId(), "DEFAULT CHANNEL ORDER ID PUMA").getId());
            fail("Pre allocated order allocated again");
        }catch (ApiException e){
            assertTrue(true);
        }
    }

    @Test
    public void testFulfillOrderWithCreatedOrder() throws JsonProcessingException, ApiException {
        OrderPojo orderPojo = TestPojo.getOrderPojo(123L, 345L, 3L, "ABCL", OrderStatus.CREATED);
        orderDao.insert(orderPojo);

        try {
            orderDto.fulfillOrder(orderPojo.getId());
            fail("Unallocated order was fulfilled");
        }catch (ApiException e){
            assertTrue(true);
        }
    }

    @Test
    public void testFulfillOrderWithAllocatedOrder() throws JsonProcessingException, ApiException {
        OrderPojo orderPojo = TestPojo.getOrderPojo(customer.getId(), clientPuma.getId(), channelSnapdeal.getId(), "ABCL", OrderStatus.ALLOCATED);
        orderDao.insert(orderPojo);

        orderDto.fulfillOrder(orderPojo.getId());
        assertEquals(OrderStatus.FULFILLED, orderPojo.getStatus());
    }

    @Test
    public void testFulfillOrderForChannelInvoiceType() throws JsonProcessingException, ApiException {
        orderDto.setClientWrapper(mockClientWrapper);

        ChannelPojo channelPojo = TestPojo.getChannelPojo("INTERNAL", InvoiceType.CHANNEL);
        channelDao.insert(channelPojo);
        consumerDao.insert(clientPuma);
        consumerDao.insert(customer);

        ProductMasterPojo productPojo = TestPojo.getProductPojo("A",clientPuma.getId(),"SF",3D,"CSKUiA","Des");
        productDao.insert(productPojo);

        OrderPojo order = TestPojo.getOrderPojo(customer.getId(), clientPuma.getId(), channelPojo.getId(), "CA", OrderStatus.ALLOCATED);
        orderDao.insert(order);

        OrderItemPojo orderItem = TestPojo.getOrderItemPojo(productPojo.getId(), order.getId(), 15L, 15L, 0L);
        orderItemDao.insert(orderItem);
        InventoryPojo inventoryPojo = TestPojo.getInventoryPojo(productPojo.getId(),100L, 30L, 10L);
        inventoryDao.insert(inventoryPojo);

        Mockito.doReturn(null).when(mockClientWrapper).fetchInvoiceFromChannel(Mockito.any(OrderReceiptData.class));

        orderDto.fulfillOrder(order.getId());

        assertEquals(OrderStatus.FULFILLED, order.getStatus());
        assertEquals(15, (long) orderItem.getOrderedQuantity());
        assertEquals(0, (long) orderItem.getAllocatedQuantity());
        assertEquals(15, (long) orderItem.getFulfilledQuantity());

        assertEquals(100, (long) inventoryPojo.getAvailableQuantity());
        assertEquals(15, (long) inventoryPojo.getAllocatedQuantity());
        assertEquals(25, (long) inventoryPojo.getFulfilledQuantity());
    }

    @Test
    public void testValidateOrderFormForValidOrder() throws ApiException {
        orderDto.validateOrderForm(TestForm.getOrderValidationForm(channelSnapdeal.getId(), clientPuma.getId(), customer.getId(), "COID1"));
    }

    @Test
    public void testValidateOrderFormForInvalidClientCustomerAndChannel() throws ApiException {
        try {
            orderDto.validateOrderForm(TestForm.getOrderValidationForm(channelSnapdeal.getId(), 131L, customer.getId(), "COID1"));
            orderDto.validateOrderForm(TestForm.getOrderValidationForm(channelSnapdeal.getId(), clientPuma.getId(), 543L, "COID1"));
            orderDto.validateOrderForm(TestForm.getOrderValidationForm(323L, clientPuma.getId(), customer.getId(), "COID1"));
            fail("Failed to validate fields correctly");
        }catch (ApiException e){
            assertTrue(true);
        }
    }

    @Test
    public void testValidateOrderFormForDuplicateOrder() throws ApiException {
        orderDao.insert(TestPojo.getOrderPojo(customer.getId(), clientPuma.getId(), channelSnapdeal.getId(), "COID1", OrderStatus.CREATED));

        try {
            orderDto.validateOrderForm(TestForm.getOrderValidationForm(channelSnapdeal.getId(), clientPuma.getId(), customer.getId(), "COID1"));
            fail("Duplicate Order Validated");
        }catch (ApiException e){
            assertTrue(true);
        }
    }

    @Test
    public void testValidateListWithValidList() throws ApiException {
        List<OrderItemForm> formList = new ArrayList<>();
        formList.add(TestForm.getOrderItemForm(productPuma.getClientSkuId(), 2L));
        formList.add(TestForm.getOrderItemForm(productPumaA.getClientSkuId(), 3L));

        orderDto.validateList(formList, clientPuma.getId(), channelSnapdeal.getId());
    }

    @Test
    public void testValidateListWithInvalidClient(){
        List<OrderItemForm> formList = new ArrayList<>();
        formList.add(TestForm.getOrderItemForm(productPuma.getClientSkuId(), 2L));

        try {
            orderDto.validateList(formList, 45L, channelSnapdeal.getId());
            fail("Invalid Client validated");
        }catch (ApiException e){
            assertTrue(true);
        }
    }

    @Test
    public void testValidateListWithInvalidChannel(){
        List<OrderItemForm> formList = new ArrayList<>();
        formList.add(TestForm.getOrderItemForm(productPuma.getClientSkuId(), 2L));

        try {
            orderDto.validateList(formList, clientPuma.getId(), 23L);
            fail("Invalid Channel validated");
        }catch (ApiException e){
            assertTrue(true);
        }
    }

    @Test
    public void testValidateListWithDuplicateClientSku(){
        List<OrderItemForm> formList = new ArrayList<>();
        formList.add(TestForm.getOrderItemForm(productPuma.getClientSkuId(), 2L));
        formList.add(TestForm.getOrderItemForm(productPuma.getClientSkuId(), 5L));

        try {
            orderDto.validateList(formList, clientPuma.getId(), 23L);
            fail("Duplicate Products in form list");
        }catch (ApiException e){
            assertTrue(true);
        }
    }

    @Test
    public void testValidateListWithInvalidClientClientSkuPair(){
        List<OrderItemForm> formList = new ArrayList<>();
        formList.add(TestForm.getOrderItemForm("CSKU-TEST", 2L));

        try {
            orderDto.validateList(formList, clientPuma.getId(), channelSnapdeal.getId());
            fail("Client, ClientSKU pair is invalid");
        }catch (ApiException e){
            assertTrue(true);
        }
    }

    @Test
    public void testValidateListWithInvalidQuantity(){
        List<OrderItemForm> formList = new ArrayList<>();
        formList.add(TestForm.getOrderItemForm(productPuma.getClientSkuId(), -2L));

        try {
            orderDto.validateList(formList, clientPuma.getId(), channelSnapdeal.getId());
            fail("Quantity is invalid");
        }catch (ApiException e){
            assertTrue(true);
        }
    }

    @Test
    public void testValidateListWithInvalidChannelAndProductPair(){
        List<OrderItemForm> formList = new ArrayList<>();
        formList.add(TestForm.getOrderItemForm(productPumaA.getClientSkuId(), 2L));

        try {
            orderDto.validateList(formList, clientPuma.getId(), channelFlipkart.getId());
            fail("Listing does not exist");
        }catch (ApiException e){
            assertTrue(true);
        }
    }

    @Test
    public void testValidateListWithInsufficientProductCountPresent(){
        List<OrderItemForm> formList = new ArrayList<>();
        formList.add(TestForm.getOrderItemForm(productPuma.getClientSkuId(), 2000L));

        try {
            orderDto.validateList(formList, clientPuma.getId(), channelSnapdeal.getId());
            fail("Insufficient quantity available");
        }catch (ApiException e){
            assertTrue(true);
        }
    }
}