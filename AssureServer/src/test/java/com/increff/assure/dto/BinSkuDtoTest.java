package com.increff.assure.dto;

import com.increff.assure.dao.BinDao;
import com.increff.assure.dao.BinSkuDao;
import com.increff.assure.dao.InventoryDao;
import com.increff.assure.dao.ProductMasterDao;
import com.increff.assure.pojo.BinPojo;
import com.increff.assure.pojo.BinSkuPojo;
import com.increff.assure.pojo.InventoryPojo;
import com.increff.assure.pojo.ProductMasterPojo;
import com.increff.assure.service.ApiException;
import model.form.BinSkuForm;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class BinSkuDtoTest extends AbstractUnitTest {

    @Autowired
    BinDto binDto;
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
        bin = TestPojo.getBinPojo();

        productDao.insert(product);
        binDao.insert(bin);

        binSkuForm = TestForm.getBinSkuForm(product.getId(), bin.getId(), 123L);
    }

    @Test
    public void testAddSingleEntry() throws ApiException {
        binDto.add(binSkuForm);
        assertEquals(1, binSkuDao.selectAll().size());
        assertEquals(123L, (long) binSkuDao.selectByBinIdAndGlobalSku(bin.getId(), product.getId()).getQuantity());

        InventoryPojo inventory = inventoryDao.selectByGlobalSku(product.getId());
        assertEquals(123L, (long) inventory.getAvailableQuantity());
        assertEquals(0L, (long) inventory.getAllocatedQuantity());
        assertEquals(0L, (long) inventory.getFulfilledQuantity());
    }

    @Test
    public void testAddSameProductsToSameBins() throws ApiException {
        binDto.add(binSkuForm);
        binDto.add(binSkuForm);

        InventoryPojo inventory = inventoryDao.selectByGlobalSku(product.getId());
        assertEquals(246L, (long) binSkuDao.selectByBinIdAndGlobalSku(bin.getId(), product.getId()).getQuantity());
        assertEquals(246L, (long) inventory.getAvailableQuantity());
        assertEquals(0L, (long) inventory.getAllocatedQuantity());
        assertEquals(0L, (long) inventory.getFulfilledQuantity());
    }

    @Test
    public void testAddWithInvalidProduct() {
        try {
            binDto.add(TestForm.getBinSkuForm(9999L, bin.getId(), 123L));
            fail("Invalid Product in BinSKU allowed");
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testAddWithInvalidBin() {
        try {
            binDto.add(TestForm.getBinSkuForm(product.getId(), 99L, 123L));
            fail("Invalid BinID in BinSKU allowed");
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testAddWithInvalidQuantity() {
        try {
            binDto.add(TestForm.getBinSkuForm(product.getId(), bin.getId(), -123L));
            fail("Invalid Quantity allowed");
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testGetValidBinSku() throws ApiException {
        BinSkuPojo binSkuPojo = TestPojo.getBinSkuPojo(123L, 567L, 100L);
        binSkuDao.insert(binSkuPojo);

        assertEquals(binSkuPojo.getGlobalSkuId(), binDto.get(binSkuPojo.getId()).getGlobalSkuId());
    }

    @Test
    public void testGetInvalidBinSku() throws ApiException {
        try {
            binDto.get(8493L);
            fail("Invalid BinID Selected");
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testAddListWithValidList() throws ApiException {
        ProductMasterPojo product1 = TestPojo.getProductPojo("P1", 11L, "BID", 12D, "CK1", "De1");
        ProductMasterPojo product2 = TestPojo.getProductPojo("P2", 11L, "BID", 12D, "CK2", "De2");
        productDao.insert(product1);
        productDao.insert(product2);

        BinPojo bin1 = TestPojo.getBinPojo();
        BinPojo bin2 = TestPojo.getBinPojo();
        binDao.insert(bin1);
        binDao.insert(bin2);

        List<BinSkuForm> formList = new ArrayList<>();
        formList.add(TestForm.getBinSkuForm(product1.getId(), bin1.getId(), 12L));
        formList.add(TestForm.getBinSkuForm(product2.getId(), bin1.getId(), 34L));
        formList.add(TestForm.getBinSkuForm(product1.getId(), bin2.getId(), 43L));
        formList.add(TestForm.getBinSkuForm(product2.getId(), bin2.getId(), 25L));

        binDto.addList(formList);
        assertEquals(4, binSkuDao.selectAll().size());
        assertEquals(2, inventoryDao.selectAll().size());

        assertEquals(55L, (long) inventoryDao.selectByGlobalSku(product1.getId()).getAvailableQuantity());
        assertEquals(59L, (long) inventoryDao.selectByGlobalSku(product2.getId()).getAvailableQuantity());
    }

    @Test
    public void testAddListWithInvalidList() {
        ProductMasterPojo product1 = TestPojo.getProductPojo("P1", 11L, "BID", 12D, "CK1", "De1");
        ProductMasterPojo product2 = TestPojo.getProductPojo("P2", 11L, "BID", 12D, "CK2", "De2");
        productDao.insert(product1);
        productDao.insert(product2);

        BinPojo bin1 = TestPojo.getBinPojo();
        BinPojo bin2 = TestPojo.getBinPojo();
        binDao.insert(bin1);
        binDao.insert(bin2);

        List<BinSkuForm> formList = new ArrayList<>();
        formList.add(TestForm.getBinSkuForm(product1.getId(), bin1.getId(), 0L));
        formList.add(TestForm.getBinSkuForm(product2.getId(), bin1.getId(), -12L));
        formList.add(TestForm.getBinSkuForm(product1.getId(), bin2.getId(), null));
        formList.add(TestForm.getBinSkuForm(product2.getId(), null, 12L));
        formList.add(TestForm.getBinSkuForm(null, bin1.getId(), 10L));

        try {
            binDto.addList(formList);
            fail("Field Validation failed on invalid fields");
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testGetAllWithEmptyTable() throws ApiException {
        assertEquals(0, binDto.getAllBinSku().size());
    }

    @Test
    public void testGetAll() throws ApiException {
        binSkuDao.insert(TestPojo.getBinSkuPojo(123L, 456L, 10L));
        assertEquals(1, binDto.getAllBinSku().size());

        binSkuDao.insert(TestPojo.getBinSkuPojo(456L, 456L, 10L));
        assertEquals(2, binDto.getAllBinSku().size());
    }

    @Test
    public void testGetSearchByBinAndProduct() throws ApiException {
        ProductMasterPojo product1 = TestPojo.getProductPojo("p1", 123L, "BID1", 120D, "CSKU1", "DES1");
        ProductMasterPojo product2 = TestPojo.getProductPojo("p2", 321L, "BID2", 320D, "CSKU2", "DES2");
        productDao.insert(product1);
        productDao.insert(product2);

        BinPojo bin1 = TestPojo.getBinPojo();
        BinPojo bin2 = TestPojo.getBinPojo();
        binDao.insert(bin1);
        binDao.insert(bin2);

        binSkuDao.insert(TestPojo.getBinSkuPojo(product1.getId(), bin1.getId(), 2L));
        binSkuDao.insert(TestPojo.getBinSkuPojo(product2.getId(), bin2.getId(), 3L));
        binSkuDao.insert(TestPojo.getBinSkuPojo(product1.getId(), bin2.getId(), 4L));
        binSkuDao.insert(TestPojo.getBinSkuPojo(product2.getId(), bin1.getId(), 5L));

        assertEquals(4, binDto.getSearchByBinAndProduct(null, null).size());

        assertEquals(2, binDto.getSearchByBinAndProduct(null, product1.getId()).size());
        assertEquals(2, binDto.getSearchByBinAndProduct(bin1.getId(), null).size());

        assertEquals(1, binDto.getSearchByBinAndProduct(bin1.getId(), product2.getId()).size());

        try{
            assertEquals(0, binDto.getSearchByBinAndProduct(999L, 777L).size());
            fail("Bin and Product does not exist");
        } catch (ApiException e){
            assertTrue(true);
        }
    }

    @Test
    public void testValidateFormList() throws ApiException {
        ProductMasterPojo product1 = TestPojo.getProductPojo("P1", 11L, "BID", 12D, "CK1", "De1");
        ProductMasterPojo product2 = TestPojo.getProductPojo("P2", 11L, "BID", 12D, "CK2", "De2");
        productDao.insert(product1);
        productDao.insert(product2);

        BinPojo bin1 = TestPojo.getBinPojo();
        BinPojo bin2 = TestPojo.getBinPojo();
        binDao.insert(bin1);
        binDao.insert(bin2);

        List<BinSkuForm> formList = new ArrayList<>();
        formList.add(TestForm.getBinSkuForm(product1.getId(), bin1.getId(), 12L));
        formList.add(TestForm.getBinSkuForm(product2.getId(), bin1.getId(), 34L));
        formList.add(TestForm.getBinSkuForm(product1.getId(), bin2.getId(), 43L));
        formList.add(TestForm.getBinSkuForm(product2.getId(), bin2.getId(), 25L));

        binDto.validateFormList(formList);
    }

    @Test
    public void testValidateFormListWithInvalidList() throws ApiException {
        ProductMasterPojo product1 = TestPojo.getProductPojo("P1", 11L, "BID", 12D, "CK1", "De1");
        productDao.insert(product1);

        BinPojo bin1 = TestPojo.getBinPojo();
        binDao.insert(bin1);

        List<BinSkuForm> formList = new ArrayList<>();
        formList.add(TestForm.getBinSkuForm(product1.getId(), bin1.getId(), 12L));
        formList.add(TestForm.getBinSkuForm(product1.getId(), bin1.getId(), 42L));

        try {
            binDto.validateFormList(formList);
            fail("Duplicate BinSku entries");
        } catch (ApiException e) {
            assertTrue(true);
        }
    }
}