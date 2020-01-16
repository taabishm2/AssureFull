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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ProductMasterServiceTest extends AbstractUnitTest {

    @Autowired
    ProductMasterService productService;
    @Autowired
    ProductMasterDao productDao;
    @Autowired
    ConsumerDao consumerDao;

    ConsumerPojo client;

    @Before
    public void init() {
        client = TestPojo.getConsumerPojo("Puma", ConsumerType.CLIENT);
        consumerDao.insert(client);
    }

    @Test
    public void testAdd() throws ApiException {
        int productCount = productDao.selectAll().size();

        ProductMasterPojo pojo = TestPojo.getConstructProduct(
                "PUMAX1",
                client.getId(),
                "PUMAX Brand",
                1200D,
                "PUMAX1SKU",
                "PUMAX Description");
        productService.add(pojo);

        assertEquals(1, productDao.selectAll().size() - productCount);

        ProductMasterPojo duplicatePojo = TestPojo.getConstructProduct(
                "PUMAX1Duplicate",
                client.getId(),
                "PUMAX Brand",
                4200D,
                "PUMAX1SKU",
                "PUMAX Duplicate Description");

        try {
            productService.add(duplicatePojo);
            fail("Duplicate Product was inserted");
        } catch (ApiException e) {
            assertEquals(e.getMessage(), "Duplicate ClientSKUs present.");
        }
    }

    @Test
    public void testGetCheckId() throws ApiException {
        ProductMasterPojo pojo = TestPojo.getConstructProduct(
                "PUMAX1Duplicate",
                client.getId(),
                "PUMAX Brand",
                4200D,
                "PUMAX1SKU",
                "PUMAX Duplicate Description");
        productService.add(pojo);

        assertEquals(productService.getCheckId(pojo.getId()), pojo);

        try {
            productService.getCheckId(1235L);
            fail("1235 ID shouldn't exist");
        } catch (ApiException e) {
            assertEquals(e.getMessage(), "Product (ID:1235) does not exist.");
        }
    }

    @Test
    public void testSelectByClientIdAndClientSku() throws ApiException {
        ProductMasterPojo pojo = TestPojo.getConstructProduct(
                "PUMAX1Duplicate",
                client.getId(),
                "PUMAX Brand",
                4200D,
                "PUMAX1SKU",
                "PUMAX Duplicate Description");
        productService.add(pojo);

        assertEquals("PUMAX1Duplicate", productService.getByClientAndClientSku(client.getId(),"PUMAX1SKU").getName());
    }

    @Test
    public void testAddList() throws ApiException {
        List<ProductMasterPojo> productList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ProductMasterPojo pojo = TestPojo.getConstructProduct(
                    "PUMAX1",
                    client.getId(),
                    "PUMAX Brand",
                    4200D,
                    "PUMAX1SKU"+i,
                    "PUMAX Description");
            productList.add(pojo);
        }
        productService.addList(productList);

        //Test if transactional holds
        ProductMasterPojo duplicateProduct = null;
        List<ProductMasterPojo> invalidProductList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ProductMasterPojo pojo = TestPojo.getConstructProduct(
                    "PUMAX1",
                    client.getId(),
                    "PUMAX Brand",
                    4200D,
                    "New-PUMAX1SKU"+i,
                    "PUMAX Description");
            invalidProductList.add(pojo);
            duplicateProduct = pojo;
        }
        invalidProductList.add(duplicateProduct);
        try {
            productService.addList(invalidProductList);
            fail("Product AddList method not Transactional");
        }
        catch(ApiException e){
            assertEquals("Duplicate ClientSKUs present.", e.getMessage());
        }
    }

    @Test
    public void testUpdate() throws ApiException {
        ProductMasterPojo pojo = TestPojo.getConstructProduct(
                "PUMAX1",
                client.getId(),
                "PUMAX Brand",
                4200D,
                "PUMAX1SKU",
                "PUMAX Description");
        productService.add(pojo);

        ProductUpdateForm form = new ProductUpdateForm();
        form.setName("NewTest");
        form.setBrandId("NewBrandId");
        form.setDescription("NewDescription");
        form.setMrp(500D);

        productService.update(client.getId(),"PUMAX1SKU", form);
        ProductMasterPojo updatedPojo = productDao.selectByClientIdAndClientSku(client.getId(), "PUMAX1SKU");

        assertEquals("NewTest", updatedPojo.getName());
        assertEquals("NewBrandId", updatedPojo.getBrandId());
        assertEquals("NewDescription", updatedPojo.getDescription());
    }

    @Test
    public void testGetClientIdOfProduct() throws ApiException {
        ProductMasterPojo pojo = TestPojo.getConstructProduct(
                "PUMAX1",
                client.getId(),
                "PUMAX Brand",
                4200D,
                "PUMAX1SKU",
                "PUMAX Description");
        productService.add(pojo);

        assertEquals(client.getId(), productService.getClientIdOfProduct(pojo.getId()));
    }
}