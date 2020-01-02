package com.increff.assure.service;

import com.increff.assure.dao.ConsumerDao;
import com.increff.assure.dao.ProductMasterDao;
import com.increff.assure.pojo.ConsumerPojo;
import com.increff.assure.pojo.ProductMasterPojo;
import model.ConsumerType;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ProductMasterServiceTest extends AbstractUnitTest {
    @Autowired
    private static ProductMasterService productMasterService;
    @Autowired
    private static ProductMasterDao productMasterDao;
    @Autowired
    private static TestDataInitializer testDataInitializer;

    @BeforeClass
    public static void init(){
        testDataInitializer.populate();
    }

    @Test
    public void testAdd() throws ApiException {
        ProductMasterPojo pojo = new ProductMasterPojo();
        pojo = new ProductMasterPojo();
        pojo.setName("PumaXTest");
        pojo.setClientId(testDataInitializer.getClientPuma().getId());
        pojo.setClientSkuId("PUMASKUTEST01");
        pojo.setMrp(3500D);
        pojo.setBrandId("PumaXTest Brand ID");
        pojo.setDescription("PumaXTest Description");
        productMasterService.add(pojo);

        assertEquals("PumaXTest", productMasterDao.select(pojo.getId()).getName());

        ProductMasterPojo duplicatePojo = new ProductMasterPojo();
        duplicatePojo = new ProductMasterPojo();
        duplicatePojo.setName("Duplicate");
        duplicatePojo.setClientId(testDataInitializer.getClientPuma().getId());
        duplicatePojo.setClientSkuId("PUMASKUTEST01");
        duplicatePojo.setMrp(5500D);
        duplicatePojo.setBrandId("PumaXTestDuplicate Brand ID");
        duplicatePojo.setDescription("PumaXTestDuplicate Description");
        try{
            productMasterService.add(duplicatePojo);
            fail("Duplicate Product POJO was inserted");
        }
        catch (ApiException e){
            assertEquals("ClientId:" + testDataInitializer.getClientPuma().getId() + ", ClientSKU:" + duplicatePojo.getClientSkuId() + " already exists", e.getMessage());
        }
    }
}
