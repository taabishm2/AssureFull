package com.increff.assure.dto;

import com.increff.assure.dao.*;
import com.increff.assure.pojo.*;
import com.increff.assure.service.ApiException;
import model.ConsumerType;
import model.InvoiceType;
import model.OrderStatus;
import model.form.OrderItemValidationForm;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OrderItemDtoTest extends AbstractUnitTest {

    @Autowired
    OrderItemDto orderItemDto;
    @Autowired
    OrderItemDao orderItemDao;
    @Autowired
    OrderDao orderDao;
    @Autowired
    ProductMasterDao productDao;
    @Autowired
    ChannelDao channelDao;
    @Autowired
    ConsumerDao consumerDao;
    @Autowired
    ChannelListingDao channelListingDao;
    @Autowired
    InventoryDao inventoryDao;

    private OrderPojo orderPojo;
    private OrderItemPojo orderItemPojo;
    private ProductMasterPojo productA;
    private ProductMasterPojo productB;
    private ChannelPojo testChannel;
    private ConsumerPojo testClient;
    private ConsumerPojo testCustomer;
    private ChannelListingPojo channelListingPojo;
    private InventoryPojo inventoryPojo;
    @Before
    public void init() {
        orderPojo = TestPojo.getOrderPojo(1L, 2L, 3L, "C@#", OrderStatus.CREATED);
        orderDao.insert(orderPojo);

        testClient = TestPojo.getConsumerPojo("ABC", ConsumerType.CLIENT);
        consumerDao.insert(testClient);
        testCustomer = TestPojo.getConsumerPojo("ABC", ConsumerType.CUSTOMER);
        consumerDao.insert(testCustomer);

        testChannel = TestPojo.getChannelPojo("TEST", InvoiceType.SELF);
        channelDao.insert(testChannel);

        productA = TestPojo.getProductPojo("A",32L,"zz",34D, "SF", "FE");
        productB = TestPojo.getProductPojo("B",32L,"zz",34D, "FF", "FE");
        productDao.insert(productA);
        productDao.insert(productB);

        channelListingPojo = TestPojo.getChannelListingPojo(productA.getId(),testChannel.getId(),"CHSKUA", testClient.getId());
        channelListingDao.insert(channelListingPojo);

        inventoryPojo = TestPojo.getInventoryPojo(productA.getId(), 50L, 0L, 0L);
        inventoryDao.insert(inventoryPojo);

        orderItemPojo = TestPojo.getOrderItemPojo(productA.getId(),orderPojo.getId(),10L,0L,0L);
    }

    @Test
    public void testValidateChannelOrderItemFormWithValidForm() throws ApiException {
        OrderItemValidationForm form = new OrderItemValidationForm();
        form.setChannelId(testChannel.getId());
        form.setChannelOrderId("COID");
        form.setChannelSkuId("CHSKUA");
        form.setClientId(testClient.getId());
        form.setCustomerId(testCustomer.getId());
        form.setOrderedQuantity(10L);

        orderItemDto.validateChannelOrderItemForm(form);
    }

    @Test
    public void testValidateChannelOrderItemFormWithInvalidFormFields(){
        OrderItemValidationForm form = new OrderItemValidationForm();
        form.setChannelId(testChannel.getId());
        form.setChannelOrderId("COID");
        form.setChannelSkuId("CHSKUB");
        form.setClientId(testClient.getId());
        form.setCustomerId(testCustomer.getId());
        form.setOrderedQuantity(10L);

        try{
            orderItemDto.validateChannelOrderItemForm(form);
            fail("Invalid ChannelSKU ID");
        }catch (ApiException e){
            form.setChannelSkuId("CHSKUA");
            assertTrue(true);
        }

        form.setClientId(testCustomer.getId());
        try{
            orderItemDto.validateChannelOrderItemForm(form);
            fail("Invalid Client/Customer");
        }catch (ApiException e){
            form.setClientId(testClient.getId());
            assertTrue(true);
        }

        form.setOrderedQuantity(500L);
        try{
            orderItemDto.validateChannelOrderItemForm(form);
            fail("Quantity is higher than available");
        }catch (ApiException e){
            assertTrue(true);
        }
    }
}