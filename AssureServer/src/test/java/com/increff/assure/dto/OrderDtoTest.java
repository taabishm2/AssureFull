package com.increff.assure.dto;

import com.increff.assure.dao.*;
import com.increff.assure.pojo.BinPojo;
import com.increff.assure.pojo.ChannelPojo;
import com.increff.assure.pojo.ConsumerPojo;
import com.increff.assure.pojo.ProductMasterPojo;
import com.increff.assure.service.ApiException;
import model.ConsumerType;
import model.InvoiceType;
import model.form.OrderForm;
import model.form.OrderItemForm;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

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

    private ConsumerPojo clientPuma;
    private ConsumerPojo clientNike;
    private ConsumerPojo customer;
    private ChannelPojo channelSnapdeal;
    private ProductMasterPojo productPuma;
    private ProductMasterPojo productPumaA;
    private ProductMasterPojo productNike;
    private OrderForm pumaOrderForm;
    private OrderForm nikeOrderForm;
    private List<OrderItemForm> orderItemFormList;
    private BinPojo bin1, bin2, bin3, bin4;

    @Before
    public void init() {
        clientPuma = TestPojo.getConsumerPojo("PUMA", ConsumerType.CLIENT);
        clientNike = TestPojo.getConsumerPojo("NIKE", ConsumerType.CLIENT);
        customer = TestPojo.getConsumerPojo("Customer NameZ", ConsumerType.CUSTOMER);
        consumerDao.insert(clientPuma);
        consumerDao.insert(clientNike);
        consumerDao.insert(customer);

        productPuma = TestPojo.getConstructProduct("PumaProduct", clientPuma.getId(), "Puma Brand", 320D, "Puma", "Puma Description");
        productPumaA = TestPojo.getConstructProduct("PumaProductA", clientPuma.getId(), "Puma BrandA", 320D, "PumaA", "PumaA Description");
        productNike = TestPojo.getConstructProduct("NikeProduct", clientNike.getId(), "Nike Brand", 120D, "NikeProd", "Nike Description");

        productDao.insert(productNike);
        productDao.insert(productPuma);
        productDao.insert(productPumaA);

        binDao.insert(TestPojo.getConstructBin());

        bin1 = TestPojo.getConstructBin();
        bin2 = TestPojo.getConstructBin();
        bin3 = TestPojo.getConstructBin();
        bin4 = TestPojo.getConstructBin();
        binDao.insert(bin1);
        binDao.insert(bin2);
        binDao.insert(bin3);
        binDao.insert(bin4);

        binSkuDao.insert(TestPojo.getConstructBinSku(productPuma.getId(), bin1.getId(), 8L));
        binSkuDao.insert(TestPojo.getConstructBinSku(productPumaA.getId(), bin1.getId(), 0L));
        binSkuDao.insert(TestPojo.getConstructBinSku(productNike.getId(), bin1.getId(), 10L));

        binSkuDao.insert(TestPojo.getConstructBinSku(productPuma.getId(), bin2.getId(), 6L));
        binSkuDao.insert(TestPojo.getConstructBinSku(productPumaA.getId(), bin2.getId(), 9L));
        binSkuDao.insert(TestPojo.getConstructBinSku(productNike.getId(), bin2.getId(), 0L));

        binSkuDao.insert(TestPojo.getConstructBinSku(productPuma.getId(), bin3.getId(), 10L));
        binSkuDao.insert(TestPojo.getConstructBinSku(productPumaA.getId(), bin3.getId(), 6L));
        binSkuDao.insert(TestPojo.getConstructBinSku(productNike.getId(), bin3.getId(), 16L));

        binSkuDao.insert(TestPojo.getConstructBinSku(productPuma.getId(), bin4.getId(), 7L));
        binSkuDao.insert(TestPojo.getConstructBinSku(productPumaA.getId(), bin4.getId(), 11L));
        binSkuDao.insert(TestPojo.getConstructBinSku(productNike.getId(), bin4.getId(), 20L));

        inventoryDao.insert(TestPojo.getConstructInventory(productPuma.getId(), 31L, 0L, 0L));
        inventoryDao.insert(TestPojo.getConstructInventory(productPumaA.getId(), 26L, 0L, 0L));
        inventoryDao.insert(TestPojo.getConstructInventory(productNike.getId(), 46L, 0L, 0L));

        channelSnapdeal = TestPojo.getConstructChannel("SNAPDEAL", InvoiceType.SELF);
        channelDao.insert(channelSnapdeal);

        List<OrderItemForm> pumaOrderItemFormList = new ArrayList<>();
        pumaOrderItemFormList.add(FormConstructor.getConstructOrderItem(productPuma.getClientSkuId(), 15L));
        pumaOrderItemFormList.add(FormConstructor.getConstructOrderItem(productPumaA.getClientSkuId(), 21L));

        pumaOrderForm = FormConstructor.getConstructOrder(customer.getId(), clientPuma.getId(), channelSnapdeal.getId(), "DEFAULT CHANNEL ORDER ID PUMA", pumaOrderItemFormList);

        List<OrderItemForm> nikeOrderItemFormList = new ArrayList<>();
        nikeOrderItemFormList.add(FormConstructor.getConstructOrderItem(productNike.getClientSkuId(), 80L));

        nikeOrderForm = FormConstructor.getConstructOrder(customer.getId(), clientNike.getId(), channelSnapdeal.getId(), "DEFAULT CHANNEL ORDER ID NIKE", nikeOrderItemFormList);

    }

    @Test
    public void testAdd() throws ApiException {
        orderDto.add(pumaOrderForm);
        orderDto.add(nikeOrderForm);

        try {
            pumaOrderForm.setClientId(customer.getId());
            orderDto.add(pumaOrderForm);
            fail("Customer Id set as Client ID");
        } catch (ApiException e) {
            pumaOrderForm.setClientId(clientPuma.getId());
        }

        try {
            pumaOrderForm.setCustomerId(clientNike.getId());
            orderDto.add(pumaOrderForm);
            fail("Client Id set as Customer ID");
        } catch (ApiException e) {
            pumaOrderForm.setCustomerId(customer.getId());
        }

        try {
            pumaOrderForm.setCustomerId(87283L);
            orderDto.add(pumaOrderForm);
            fail("Invalid Consumer ID");
        } catch (ApiException e) {
            assertEquals("Consumer (ID:87283) does not exist.", e.getMessage());
            pumaOrderForm.setCustomerId(customer.getId());
        }

        try {
            pumaOrderForm.setChannelId(123L);
            orderDto.add(pumaOrderForm);
            fail("Invalid Channel ID used");
        } catch (ApiException e) {
            assertEquals("Channel (ID:" + 123 + ") does not exist", e.getMessage());
            pumaOrderForm.setChannelId(channelSnapdeal.getId());
        }

        try {
            orderDto.add(pumaOrderForm);
            fail("Duplicate order inserted");
        } catch (ApiException e) {
            assertEquals("Order with ChannelID & ChannelOrderID pair already exist.", e.getMessage());
        }

        try {
            List<OrderItemForm> pumaOrderItemFormList = new ArrayList<>();
            pumaOrderItemFormList.add(FormConstructor.getConstructOrderItem(productPuma.getClientSkuId(), 15L));
            pumaOrderItemFormList.add(FormConstructor.getConstructOrderItem(productNike.getClientSkuId(), 21L));

            OrderForm pumaOrderForm = FormConstructor.getConstructOrder(customer.getId(), clientPuma.getId(), channelSnapdeal.getId(), "CHANNEL ORDER ID PUMA", pumaOrderItemFormList);
            orderDto.add(pumaOrderForm);
            fail("Invalid Product and Client combination");
        } catch (ApiException e) {
            assertTrue(true);
        }

        try {
            List<OrderItemForm> pumaOrderItemFormList = new ArrayList<>();
            pumaOrderItemFormList.add(FormConstructor.getConstructOrderItem(productPuma.getClientSkuId(), 15L));
            pumaOrderItemFormList.add(FormConstructor.getConstructOrderItem(productPuma.getClientSkuId(), 21L));

            OrderForm pumaOrderForm = FormConstructor.getConstructOrder(customer.getId(), clientPuma.getId(), channelSnapdeal.getId(), "CHANNEL ID PUMA", pumaOrderItemFormList);
            orderDto.add(pumaOrderForm);
            fail("Duplicate OrderItems in Order");
        } catch (ApiException e) {
            assertEquals("GlobalSKU, OrderID pair already exists.", e.getMessage());
        }
    }

    @Test
    public void testRunAllocation() throws ApiException {
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

    }
}