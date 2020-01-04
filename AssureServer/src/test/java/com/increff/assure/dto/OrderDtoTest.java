package com.increff.assure.dto;

import com.increff.assure.dao.ChannelDao;
import com.increff.assure.dao.ConsumerDao;
import com.increff.assure.dao.OrderDao;
import com.increff.assure.dao.ProductMasterDao;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

    @Before
    public void init() {
        clientPuma = PojoConstructor.getConstructConsumer("PUMA", ConsumerType.CLIENT);
        clientNike = PojoConstructor.getConstructConsumer("NIKE", ConsumerType.CLIENT);
        customer = PojoConstructor.getConstructConsumer("Customer Name", ConsumerType.CUSTOMER);
        consumerDao.insert(clientNike);
        consumerDao.insert(clientPuma);
        consumerDao.insert(customer);

        productNike = PojoConstructor.getConstructProduct("PumaProduct", clientNike.getId(), "Puma Brand", 120D, "PUMASKU", "PUMA Description");
        productPuma = PojoConstructor.getConstructProduct("NikeProduct", clientPuma.getId(), "Nike Brand", 320D, "NIKESKU", "NIKE Description");
        productPumaA = PojoConstructor.getConstructProduct("NikeProductA", clientPuma.getId(), "Nike BrandA", 320D, "NIKESKUA", "NIKEA Description");
        productDao.insert(productNike);
        productDao.insert(productPuma);
        productDao.insert(productPumaA);

        channelSnapdeal = PojoConstructor.getConstructChannel("SNAPDEAL", InvoiceType.CHANNEL);
        channelDao.insert(channelSnapdeal);

        List<OrderItemForm> pumaOrderItemFormList = new ArrayList<>();
        pumaOrderItemFormList.add(FormConstructor.getConstructOrderItem(productPuma.getId(), 15L));
        pumaOrderItemFormList.add(FormConstructor.getConstructOrderItem(productPumaA.getId(), 21L));

        pumaOrderForm = FormConstructor.getConstructOrder(customer.getId(), clientPuma.getId(), channelSnapdeal.getId(), "DEFAULT CHANNEL ORDER ID PUMA", pumaOrderItemFormList);

        List<OrderItemForm> nikeOrderItemFormList = new ArrayList<>();
        nikeOrderItemFormList.add(FormConstructor.getConstructOrderItem(productNike.getId(), 80L));

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
            assertEquals("Specified ClientID beLongs to a Customer", e.getMessage());
            pumaOrderForm.setClientId(clientPuma.getId());
        }

        try {
            pumaOrderForm.setCustomerId(clientNike.getId());
            orderDto.add(pumaOrderForm);
            fail("Client Id set as Customer ID");
        } catch (ApiException e) {
            assertEquals("Specified CustomerID beLongs to a Client", e.getMessage());
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

        try{
            List<OrderItemForm> pumaOrderItemFormList = new ArrayList<>();
            pumaOrderItemFormList.add(FormConstructor.getConstructOrderItem(productPuma.getId(), 15L));
            pumaOrderItemFormList.add(FormConstructor.getConstructOrderItem(productNike.getId(), 21L));

            OrderForm pumaOrderForm = FormConstructor.getConstructOrder(customer.getId(), clientPuma.getId(), channelSnapdeal.getId(), "CHANNEL ORDER ID PUMA", pumaOrderItemFormList);
            orderDto.add(pumaOrderForm);
            fail("Invalid Product and Client combination");
        }
        catch (ApiException e){
            assertEquals("Invalid Client for Product(ID: "+productNike.getId()+").", e.getMessage());
        }

        try{
            List<OrderItemForm> pumaOrderItemFormList = new ArrayList<>();
            pumaOrderItemFormList.add(FormConstructor.getConstructOrderItem(productPuma.getId(), 15L));
            pumaOrderItemFormList.add(FormConstructor.getConstructOrderItem(productPuma.getId(), 21L));

            OrderForm pumaOrderForm = FormConstructor.getConstructOrder(customer.getId(), clientPuma.getId(), channelSnapdeal.getId(), "CHANNEL ID PUMA", pumaOrderItemFormList);
            orderDto.add(pumaOrderForm);
            fail("Duplicate OrderItems in Order");
        }
        catch (ApiException e){
            assertEquals("GlobalSKU, OrderID pair already exists.", e.getMessage());
        }
    }

    @Test
    public void testRunAllocation() throws ApiException {
        orderDto.add(pumaOrderForm);
        orderDto.add(nikeOrderForm);

        orderDto.runAllocation(orderDao.selectByChannelAndChannelOrderId(channelSnapdeal.getId(), "DEFAULT CHANNEL ORDER ID PUMA").getId());
        orderDto.runAllocation(orderDao.selectByChannelAndChannelOrderId(channelSnapdeal.getId(), "DEFAULT CHANNEL ORDER ID PUMA").getId());


    }
}