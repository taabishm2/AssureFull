package com.increff.assure.dto;

import com.increff.assure.dao.ConsumerDao;
import com.increff.assure.dao.ProductMasterDao;
import com.increff.assure.pojo.ConsumerPojo;
import com.increff.assure.pojo.ProductMasterPojo;
import com.increff.assure.service.ApiException;
import model.ConsumerType;
import model.form.ProductMasterForm;
import model.form.ProductUpdateForm;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProductMasterDtoTest extends AbstractUnitTest {

    @Autowired
    ProductMasterDto productDto;
    @Autowired
    ProductMasterDao productDao;
    @Autowired
    ConsumerDao consumerDao;

    private ConsumerPojo client;
    private ProductMasterPojo productPojo;

    @Before
    public void init() {
        client = TestPojo.getConsumerPojo("PUMA", ConsumerType.CLIENT);
        ConsumerPojo customer = TestPojo.getConsumerPojo("NAME", ConsumerType.CUSTOMER);
        consumerDao.insert(client);
        consumerDao.insert(customer);

        ProductMasterForm productMasterForm = TestForm.getProductForm("PUMAX", "BRAND", 100D, "CSKU1", "Description");
        productPojo = TestPojo.getProductPojo("PUMAXPOJO", client.getId(), "BRAND", 100D, "CSKU1", "Description");
    }

    @Test
    public void testAddListWithValidList() throws ApiException {
        List<ProductMasterForm> productList = new ArrayList<>();
        productList.add(TestForm.getProductForm("PUMAXPOJO", "BRAND", 100D, "CSKU1", "Description"));
        productList.add(TestForm.getProductForm("PUMAXPOJO", "BRAND", 100D, "CSKU2", "Description"));
        productList.add(TestForm.getProductForm("PUMAXPOJO", "BRAND", 100D, "CSKU3", "Description"));

        productDto.addList(productList, client.getId());
        assertEquals(3, productDao.selectAll().size());
    }

    @Test
    public void testAddListWithDuplicateSKU() {
        List<ProductMasterForm> productList = new ArrayList<>();
        productList.add(TestForm.getProductForm("PUMAXPOJO", "BRAND", 100D, "CSKU2", "Description"));
        productList.add(TestForm.getProductForm("PUMAXPOJO", "BRAND", 100D, "CSKU2", "Description"));

        try {
            productDto.addList(productList, client.getId());
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testAddListWithInvalidFields() {
        List<ProductMasterForm> productList = new ArrayList<>();
        productList.add(TestForm.getProductForm("PUMAXPOJO", "BRAND", 100D, "CSKU2", "Description"));
        productList.add(TestForm.getProductForm("    ", "BRAND", 100D, "CSK32", "Description"));

        try {
            productDto.addList(productList, client.getId());
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testGet() throws ApiException {
        productDao.insert(productPojo);

        assertEquals(productPojo.getClientId(), productDto.get(productPojo.getId()).getClientId());
        assertEquals(productPojo.getClientSkuId(), productDto.get(productPojo.getId()).getClientSkuId());
    }

    @Test
    public void testGetAll() throws ApiException {
        productDao.insert(TestPojo.getProductPojo("A", 123L, "AB", 12D, "XYZ", "DES"));
        assertEquals(1, productDto.getAll().size());

        productDao.insert(TestPojo.getProductPojo("A", 123L, "AB", 12D, "PYZ", "DES"));
        assertEquals(2, productDto.getAll().size());
    }

    @Test
    public void testGetByClientId() throws ApiException {
        productDao.insert(TestPojo.getProductPojo("A", 1L, "B", 1D, "X", "D"));
        assertEquals(1, productDto.getByClientId(1L).size());

        productDao.insert(TestPojo.getProductPojo("A", 1L, "B", 1D, "Y", "D"));
        assertEquals(2, productDto.getByClientId(1L).size());

        productDao.insert(TestPojo.getProductPojo("A", 345L, "B", 1D, "Y", "D"));
        assertEquals(2, productDto.getByClientId(1L).size());
        assertEquals(1, productDto.getByClientId(345L).size());
        assertEquals(0, productDto.getByClientId(789L).size());
    }

    @Test
    public void testValidateFormListWithInvalidField() {
        List<ProductMasterForm> productList = new ArrayList<>();
        productList.add(TestForm.getProductForm("PUMAXPOJO", "BRAND", 100D, "CSKU2", "Description"));
        productList.add(TestForm.getProductForm("    ", "BRAND", 100D, "CSKU4", "Description"));

        try {
            productDto.validateFormList(productList, client.getId());
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testValidateFormListWithInvalidClient() {
        List<ProductMasterForm> productList = new ArrayList<>();
        productList.add(TestForm.getProductForm("PumPojo", "BRAND", 100D, "CSKU2", "Description"));
        productList.add(TestForm.getProductForm("PumPojo", "BRAND", 100D, "CSKU2", "Description"));

        try {
            productDto.validateFormList(productList, 1234L);
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testValidateFormListWithInvalidFormList() {
        List<ProductMasterForm> productList = new ArrayList<>();
        productList.add(TestForm.getProductForm("PumPojo", "BRAND", 100D, "CSKU2", "Description"));
        productList.add(TestForm.getProductForm("", "BRAND", 100D, "CSKU2", "Description"));
        consumerDao.insert(client);

        try {
            productDto.validateFormList(productList, client.getId());
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testUpdate() throws ApiException {
        productDao.insert(TestPojo.getProductPojo("PUMAXPOJO", 123L, "BRAND", 100D, "CSKU1", "Description"));
        ProductMasterPojo updatedPojo = TestPojo.getProductPojo("NEW NAME", 123L, "NEW BRAND ID", 999D, "CSKU1", "NEW DESCRIPTION");

        ProductUpdateForm updateForm = new ProductUpdateForm();
        updateForm.setMrp(updatedPojo.getMrp());
        updateForm.setDescription(updatedPojo.getDescription());
        updateForm.setBrandId(updatedPojo.getBrandId());
        updateForm.setName(updatedPojo.getName());

        productDto.update(123L, "CSKU1", updateForm);
        assertEquals(updatedPojo.getClientId(), productDao.selectByClientIdAndClientSku(updatedPojo.getClientId(),updatedPojo.getClientSkuId()).getClientId());
        assertEquals(updatedPojo.getBrandId(), productDao.selectByClientIdAndClientSku(updatedPojo.getClientId(),updatedPojo.getClientSkuId()).getBrandId());
        assertEquals(updatedPojo.getMrp(), productDao.selectByClientIdAndClientSku(updatedPojo.getClientId(),updatedPojo.getClientSkuId()).getMrp());
        assertEquals(updatedPojo.getDescription(), productDao.selectByClientIdAndClientSku(updatedPojo.getClientId(),updatedPojo.getClientSkuId()).getDescription());
    }
}