package com.increff.assure.service;

import com.increff.assure.dao.ConsumerDao;
import com.increff.assure.dao.ProductMasterDao;
import com.increff.assure.pojo.ConsumerPojo;
import com.increff.assure.pojo.ProductMasterPojo;
import model.ConsumerType;
import model.form.ProductUpdateForm;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ProductMasterServiceTest extends AbstractUnitTest {

    @Autowired
    ProductMasterService productService;
    @Autowired
    ProductMasterDao productDao;
    @Autowired
    ConsumerDao consumerDao;

    ConsumerPojo client;
    ProductMasterPojo validProduct;

    @Before
    public void init() {
        client = TestPojo.getConsumerPojo("PUMA", ConsumerType.CLIENT);
        consumerDao.insert(client);

        validProduct = TestPojo.getProductPojo(
                "PUMAX1",
                client.getId(),
                "PUMAX Brand",
                1200D,
                "PUMAX1SKU",
                "PUMAX Description");
    }

    @Test
    public void testAddWithValidProduct() throws ApiException {
        try{
            productService.add(validProduct);
        } catch(ApiException e){
            fail("Failed to insert valid Product");
        }
        assertEquals(1, productDao.selectAll().size());
    }

    @Test
    public void testAddDuplicateProduct(){
        productDao.insert(validProduct);

        try {
            productService.add(validProduct);
            fail("Duplicate Product was inserted");
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testGetCheckIdWithValidId() throws ApiException {
        productDao.insert(validProduct);

        assertEquals(productService.getCheckId(validProduct.getId()), validProduct);
    }

    @Test
    public void testGetCheckIdWithInvalidId(){
        try {
            productService.getCheckId(1235L);
            fail("ID shouldn't exist");
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testAddListWithValidList() throws ApiException {
        List<ProductMasterPojo> productList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            validProduct.setClientSkuId("CSKU" + i*10);
            productList.add(TestPojo.getProductPojo("A", 1L, "B", 1D, String.valueOf(i), "D"));
        }

        productService.addList(productList);
    }

    @Test
    public void testAddListWithInvalidList() throws ApiException {
        List<ProductMasterPojo> invalidProductList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            validProduct.setClientSkuId("ID");
            invalidProductList.add(validProduct);
        }

        try {
            productService.addList(invalidProductList);
            fail("Product AddList method not Transactional");
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testGetAll() throws ApiException {
        productDao.insert(TestPojo.getProductPojo("A", 1L, "B", 1D, "X", "D"));
        assertEquals(1,productService.getAll().size());

        productDao.insert(TestPojo.getProductPojo("A", 1L, "B", 1D, "Y", "D"));
        assertEquals(2,productService.getAll().size());
    }

    @Test
    public void testUpdate() throws ApiException {
        validProduct.setClientSkuId("UPDATETESTSKU");
        productDao.insert(validProduct);

        ProductUpdateForm form = new ProductUpdateForm();
        form.setName("NewTest");
        form.setBrandId("NewBrandId");
        form.setDescription("NewDescription");
        form.setMrp(500D);

        productService.update(client.getId(), "UPDATETESTSKU", form);
        ProductMasterPojo updatedPojo = productDao.selectByClientIdAndClientSku(client.getId(), "UPDATETESTSKU");

        assertEquals("NewTest", updatedPojo.getName());
        assertEquals("NewBrandId", updatedPojo.getBrandId());
        assertEquals("NewDescription", updatedPojo.getDescription());
    }

    @Test
    public void testSelectByClientIdAndClientSkuWithValidInputs() throws ApiException {
        productDao.insert(validProduct);

        assertEquals(validProduct, productService.getByClientAndClientSku(client.getId(), validProduct.getClientSkuId()) );
    }

    @Test
    public void testGetClientIdOfProduct() throws ApiException {
        productDao.insert(validProduct);

        assertEquals(client.getId(), productService.getClientIdOfProduct(validProduct.getId()));
    }

    @Test
    public void testSelectByClientIdAndClientSkuWithInvalidInputs() throws ApiException {
        productDao.insert(validProduct);

        try{
            productService.getByClientAndClientSku(client.getId(), validProduct.getClientSkuId()+"abc");
        } catch (ApiException e){
            assertTrue(true);
        }

        try{
            productService.getByClientAndClientSku(client.getId()+123L, validProduct.getClientSkuId());
        } catch (ApiException e){
            assertTrue(true);
        }
    }

    @Test
    public void testGetByClientId(){
        productDao.insert(TestPojo.getProductPojo("A", 1L, "B", 1D, "X", "D"));
        assertEquals(1, productService.getByClientId(1L).size());

        productDao.insert(TestPojo.getProductPojo("A", 1L, "B", 1D, "Y", "D"));
        assertEquals(2, productService.getByClientId(1L).size());

        productDao.insert(TestPojo.getProductPojo("A", 345L, "B", 1D, "Y", "D"));
        assertEquals(2, productService.getByClientId(1L).size());
        assertEquals(1, productService.getByClientId(345L).size());
        assertEquals(0, productService.getByClientId(789L).size());
    }

    @Test
    public void testGetByClientSku(){
        assertEquals(0, productService.getByClientSku("XYZ").size());

        productDao.insert(TestPojo.getProductPojo("A", 345L, "B", 1D, "XYZ", "D"));
        assertEquals(1, productService.getByClientSku("XYZ").size());

        productDao.insert(TestPojo.getProductPojo("A", 567L, "B", 1D, "XYZ", "D"));
        assertEquals(2, productService.getByClientSku("XYZ").size());
    }
}