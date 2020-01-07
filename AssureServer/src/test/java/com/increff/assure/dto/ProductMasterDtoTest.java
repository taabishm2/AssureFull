package com.increff.assure.dto;

import com.increff.assure.dao.ConsumerDao;
import com.increff.assure.dao.ProductMasterDao;
import com.increff.assure.pojo.ConsumerPojo;
import com.increff.assure.pojo.ProductMasterPojo;
import com.increff.assure.service.ApiException;
import model.ConsumerType;
import model.form.ProductMasterForm;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ProductMasterDtoTest extends AbstractUnitTest {

    @Autowired
    ProductMasterDto productDto;
    @Autowired
    ProductMasterDao productDao;
    @Autowired
    ConsumerDao consumerDao;

    private ConsumerPojo client;
    private ConsumerPojo customer;
    private ProductMasterForm productMasterForm;
    private ProductMasterPojo productPojo;

    @Before
    public void init() {
        //TODO: Create general class. Autowire all DAOs and write methods prepare_for_test
        client = PojoConstructor.getConstructConsumer("PUMA", ConsumerType.CLIENT);
        customer = PojoConstructor.getConstructConsumer("NAME", ConsumerType.CUSTOMER);

        consumerDao.insert(client);
        consumerDao.insert(customer);

        productMasterForm = FormConstructor.getConstructProduct("PUMAX", client.getId(), "BRAND", 100D, "CSKU1", "Description");
        productPojo = PojoConstructor.getConstructProduct("PUMAXPOJO", client.getId(), "BRAND", 100D, "CSKU1", "Description");
    }

    @Test
    public void testAdd() throws ApiException {
        int initialCount = productDao.selectAll().size();
        productDto.add(FormConstructor.getConstructProduct("PUMAX", client.getId(), "BRAND", 100D, "CSKU1", "Description"));

        assertEquals(1, productDao.selectAll().size() - initialCount);
        assertNotNull(productDao.selectByClientIdAndClientSku(client.getId(), "CSKU1"));

        try {
            productDto.add(FormConstructor.getConstructProduct("PUMAX", customer.getId(), "BRAND", 100D, "CSKU1", "Description"));
        } catch (ApiException e) {
            assertEquals("Client (ClientID:" + customer.getId() + ") not registered.", e.getMessage());
        }

        try {
            productDto.add(FormConstructor.getConstructProduct("PUMAX", customer.getId(), "BRAND", -300D, "CSKU1", "Description"));
        } catch (ApiException e) {
            assertEquals("MRP must be non-zero and positive.", e.getMessage());
        }

        try {
            productDto.add(productMasterForm);
            productDto.add(productMasterForm);
        } catch (ApiException e) {

        }
    }

    @Test
    public void testGet() throws ApiException {
        productDao.insert(productPojo);

        assertEquals("PUMAXPOJO", productDto.get(productPojo.getId()).getName());
    }

    @Test
    public void testAddList() throws ApiException {
        assertEquals(0, productDao.selectAll().size());
        List<ProductMasterForm> productList = new ArrayList<>();
        productList.add(FormConstructor.getConstructProduct("PUMAXPOJO", client.getId(), "BRAND", 100D, "CSKU1", "Description"));
        productList.add(FormConstructor.getConstructProduct("PUMAXPOJO", client.getId(), "BRAND", 100D, "CSKU2", "Description"));
        productList.add(FormConstructor.getConstructProduct("PUMAXPOJO", client.getId(), "BRAND", 100D, "CSKU3", "Description"));

        productDto.addList(productList);
        assertEquals(3, productDao.selectAll().size());
    }

//    @Test(expected = ApiException.class)
//    public void testAddListTransactionality() throws ApiException {
//        assertEquals(0, productDao.selectAll().size());
//        List<ProductMasterForm> productList = new ArrayList<>();
//        productList.add(FormConstructor.getConstructProduct("PUMAXPOJO", client.getId(), "BRAND", 100D, "CSKU1", "Description"));
//        productList.add(FormConstructor.getConstructProduct("PUMAXPOJO", client.getId(), "BRAND", 100D, "CSKU2", "Description"));
//        productList.add(FormConstructor.getConstructProduct("PUMAXPOJO", client.getId(), "BRAND", 100D, "CSKU3", "Description"));
//
//        productDto.addList(productList);
//
//        List<ProductMasterForm> invalidProductList = new ArrayList<>();
//        invalidProductList.add(FormConstructor.getConstructProduct("PUMAXPOJO", client.getId(), "BRAND", 100D, "CSKU4", "Description"));
//        invalidProductList.add(FormConstructor.getConstructProduct("PUMAXPOJO", client.getId(), "BRAND", 100D, "CSKU4", "Description"));
//
//        try {
//            productDto.addList(invalidProductList);
//            fail("failed");
//        } catch (ApiException e) {
//        }
//
//        for(ProductMasterPojo product:productDao.selectAll())
//            System.out.println(product.getClientSkuId());
//
//        assertEquals(3, productDao.selectAll().size());
//    }
}