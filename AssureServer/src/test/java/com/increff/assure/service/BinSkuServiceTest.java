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

import java.util.List;

import static org.junit.Assert.*;

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
        consumerPojo = TestPojo.getConsumerPojo("PUMA", ConsumerType.CLIENT);
        consumerDao.insert(consumerPojo);

        productPojo = TestPojo.getConstructProduct(
                "PUMAXNAME",
                consumerPojo.getId(),
                "PUMA BrandID",
                500D,
                "PUMASKUID",
                "Puma Description");
        productMasterDao.insert(productPojo);

        binPojo = TestPojo.getConstructBin();
        binDao.insert(binPojo);

        binSkuPojo = TestPojo.getConstructBinSku(productPojo.getClientId(), binPojo.getId(), 10L);
    }

    @Test
    public void testAddOrUpdate() {
        binSkuService.addOrUpdate(binSkuPojo);
        BinSkuPojo addedBinSku = binSkuDao.select(binSkuPojo.getId());
        assertEquals(10L, (long) addedBinSku.getQuantity());

        binSkuService.addOrUpdate(binSkuPojo);
        BinSkuPojo updatedBinSku = binSkuDao.select(binSkuPojo.getId());
        assertEquals(20L, (long) updatedBinSku.getQuantity());
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

    @Test
    public void testGetSearchByBinAndProduct(){
        binSkuDao.insert(TestPojo.getConstructBinSku(1L, 1L, 3L));
        binSkuDao.insert(TestPojo.getConstructBinSku(1L, 2L, 3L));
        binSkuDao.insert(TestPojo.getConstructBinSku(1L, 3L, 3L));
        binSkuDao.insert(TestPojo.getConstructBinSku(2L, 1L, 3L));
        binSkuDao.insert(TestPojo.getConstructBinSku(2L, 2L, 3L));
        binSkuDao.insert(TestPojo.getConstructBinSku(3L, 4L, 3L));

        List<BinSkuPojo> resultBinId1 = binSkuService.getSearchByBinAndProduct(1L, null);
        assertEquals(2, resultBinId1.size());

        List<BinSkuPojo> resultBinId2 = binSkuService.getSearchByBinAndProduct(2L, null);
        assertEquals(2, resultBinId2.size());

        List<BinSkuPojo> resultBinId4 = binSkuService.getSearchByBinAndProduct(4L, null);
        assertEquals(1, resultBinId4.size());
        assertEquals(3L, (long) resultBinId4.get(0).getGlobalSkuId());

        List<BinSkuPojo> resultGsku1 = binSkuService.getSearchByBinAndProduct(null, 1L);
        assertEquals(3L, resultGsku1.size());

        List<BinSkuPojo> resultGsku3 = binSkuService.getSearchByBinAndProduct(null, 3L);
        assertEquals(1L, resultGsku3.size());
        assertEquals(4L, (long) resultBinId4.get(0).getBinId());

        List<BinSkuPojo> resultGskuInv = binSkuService.getSearchByBinAndProduct(null, 13L);
        assertEquals(0L, resultGskuInv.size());

        List<BinSkuPojo> resultBinIdInv = binSkuService.getSearchByBinAndProduct(34L, null);
        assertEquals(0L, resultBinIdInv.size());
    }
}