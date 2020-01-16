package com.increff.assure.dto;

import com.increff.assure.dao.BinDao;
import com.increff.assure.dao.BinSkuDao;
import com.increff.assure.dao.InventoryDao;
import com.increff.assure.dao.ProductMasterDao;
import com.increff.assure.pojo.BinPojo;
import com.increff.assure.pojo.InventoryPojo;
import com.increff.assure.pojo.ProductMasterPojo;
import com.increff.assure.service.ApiException;
import model.form.BinSkuForm;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class BinSkuDtoTest extends AbstractUnitTest {

    @Autowired
    BinSkuDto binSkuDto;
    @Autowired
    BinSkuDao binSkuDao;
    @Autowired
    ProductMasterDao productDao;
    @Autowired
    BinDao binDao;
    @Autowired
    InventoryDao inventoryDao;

    private ProductMasterPojo product;
    private BinPojo bin;
    private BinSkuForm binSkuForm;
    private InventoryPojo inventoryPojo;

    @Before
    public void init() {
        product = TestPojo.getProductPojo("Name", 123L, "Brand", 1230D, "CSKU", "Description");
        bin = TestPojo.getConstructBin();

        productDao.insert(product);
        binDao.insert(bin);

        binSkuForm = TestForm.getConstructBinSku(product.getId(), bin.getId(), 123L);
    }

    @Test
    public void testAddSingleEntry() throws ApiException {
        binSkuDto.add(binSkuForm);
        assertEquals(1, binSkuDao.selectAll().size());
        assertEquals(123L, (long) binSkuDao.selectByBinIdAndGlobalSku(bin.getId(), product.getId()).getQuantity());

        InventoryPojo inventory = inventoryDao.selectByGlobalSku(product.getId());
        assertEquals(123L, (long) inventory.getAvailableQuantity());
        assertEquals(0L, (long) inventory.getAllocatedQuantity());
        assertEquals(0L, (long) inventory.getFulfilledQuantity());
    }

    @Test
    public void testAddSameProductsToSameBins() throws ApiException {
        binSkuDto.add(binSkuForm);
        binSkuDto.add(binSkuForm);

        InventoryPojo inventory = inventoryDao.selectByGlobalSku(product.getId());
        assertEquals(246L, (long) binSkuDao.selectByBinIdAndGlobalSku(bin.getId(), product.getId()).getQuantity());
        assertEquals(246L, (long) inventory.getAvailableQuantity());
        assertEquals(0L, (long) inventory.getAllocatedQuantity());
        assertEquals(0L, (long) inventory.getFulfilledQuantity());
    }

    @Test
    public void testAddWithInvalidProduct() {
        try {
            binSkuDto.add(TestForm.getConstructBinSku(9999L, bin.getId(), 123L));
            fail("Invalid Product in BinSKU allowed");
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testAddWithInvalidBin() {
        try {
            binSkuDto.add(TestForm.getConstructBinSku(product.getId(), 99L, 123L));
            fail("Invalid BinID in BinSKU allowed");
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testAddWithInvalidQuantity(){
        try {
            binSkuDto.add(TestForm.getConstructBinSku(product.getId(), bin.getId(), -123L));
            fail("Invalid Quantity allowed");
        } catch (ApiException e) {
            assertTrue(true);
        }
    }
}