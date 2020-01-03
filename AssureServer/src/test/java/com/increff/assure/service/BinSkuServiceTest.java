package com.increff.assure.service;

import com.increff.assure.dao.BinDao;
import com.increff.assure.dao.BinSkuDao;
import com.increff.assure.dao.ConsumerDao;
import com.increff.assure.dao.ProductMasterDao;
import com.increff.assure.pojo.BinPojo;
import com.increff.assure.pojo.BinSkuPojo;
import com.increff.assure.pojo.ConsumerPojo;
import com.increff.assure.pojo.ProductMasterPojo;
import model.ConsumerType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BinSkuServiceTest extends AbstractUnitTest {

    @Autowired
    BinSkuService binSkuService;
    @Autowired
    BinSkuDao binSkuDao;
    @Autowired
    ConsumerDao consumerDao;
    @Autowired
    ProductMasterDao productMasterDao;
    @Autowired
    BinDao binDao;

    private ConsumerPojo consumerPojo;
    private ProductMasterPojo productPojo;
    private BinPojo binPojo;
    private BinSkuPojo binSkuPojo;

    @Before
    public void init() {
        consumerPojo = PojoConstructor.getConstructConsumer("PUMA", ConsumerType.CLIENT);
        consumerDao.insert(consumerPojo);

        productPojo = PojoConstructor.getConstructProduct(
                "PUMAXNAME",
                consumerPojo.getId(),
                "PUMA BrandID",
                500D,
                "PUMASKUID",
                "Puma Description");
        productMasterDao.insert(productPojo);

        binPojo = PojoConstructor.getConstructBin();
        binDao.insert(binPojo);

        binSkuPojo = PojoConstructor.getConstructBinSku(productPojo.getClientId(), binPojo.getId(), 10L);
    }

    @Test
    public void testAddOrUpdate() {
        binSkuService.addOrUpdate(binSkuPojo);
        BinSkuPojo addedBinSku = binSkuDao.select(binSkuPojo.getId());
        assertEquals(10L, (long) addedBinSku.getAvailableQuantity());

        binSkuService.addOrUpdate(binSkuPojo);
        BinSkuPojo updatedBinSku = binSkuDao.select(binSkuPojo.getId());
        assertEquals(20L, (long) updatedBinSku.getAvailableQuantity());
    }

    @Test
    public void testGetCheckId() throws ApiException {
        binSkuDao.insert(binSkuPojo);
        assertEquals(binSkuService.getCheckId(binSkuPojo.getId()), binSkuPojo);

        try {
            binSkuService.getCheckId(1234L);
            fail("Invalid BinSKU ID validated");
        } catch (ApiException e) {
            assertEquals("Bin Inventory Item (ID:1234) does not exist.", e.getMessage());
        }
    }

    @Test
    public void testRemoveFromBin() {
        Long deficit = binSkuService.removeFromBin(binSkuPojo, 5L);
        assertEquals(0L, (long) deficit);

        deficit = binSkuService.removeFromBin(binSkuPojo, 20L);
        assertEquals(15L, (long) deficit);

        deficit = binSkuService.removeFromBin(binSkuPojo, 20L);
        assertEquals(20L, (long) deficit);
    }
}