package com.increff.assure.dto;

import com.increff.assure.dao.ChannelDao;
import com.increff.assure.dao.ChannelListingDao;
import com.increff.assure.dao.ConsumerDao;
import com.increff.assure.dao.ProductMasterDao;
import com.increff.assure.pojo.ChannelPojo;
import com.increff.assure.pojo.ConsumerPojo;
import com.increff.assure.pojo.ProductMasterPojo;
import com.increff.assure.service.ApiException;
import model.ConsumerType;
import model.InvoiceType;
import model.data.ChannelListingData;
import model.data.ProductMasterData;
import model.form.ChannelListingForm;
import model.form.ChannelListingSearchForm;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ChannelListingDtoTest extends AbstractUnitTest {

    @Autowired
    ChannelListingDto listingDto;
    @Autowired
    ChannelListingDao listingDao;
    @Autowired
    ChannelDao channelDao;
    @Autowired
    ConsumerDao consumerDao;
    @Autowired
    ProductMasterDao productDao;

    private ChannelPojo testChannelA;
    private ConsumerPojo testClient;

    @Before
    public void init() {
        testChannelA = TestPojo.getChannelPojo("CHANNEL A", InvoiceType.CHANNEL);
        channelDao.insert(testChannelA);

        testClient = TestPojo.getConsumerPojo("CONSUMER A", ConsumerType.CLIENT);
        consumerDao.insert(testClient);
    }

    @Test
    public void testAddListWithValidList() throws ApiException {
        List<ChannelListingForm> formList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            productDao.insert(TestPojo.getProductPojo("A",testClient.getId(),"B",2D,"CL SKU"+i,"DESC"));
            formList.add(TestForm.getChannelListingForm("CL SKU" + i, "CH SKU" + i));
        }

        listingDto.addList(formList, testChannelA.getId(), testClient.getId());
        assertEquals(5, listingDao.selectAll().size());
    }

    @Test
    public void testAddListWithInvalidClientChannel() {
        List<ChannelListingForm> formList = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            formList.add(TestForm.getChannelListingForm("CL SKU" + i, "CH SKU" + i));

        try{
            listingDto.addList(formList, 123L, testClient.getId());
            listingDto.addList(formList, testChannelA.getId(), 456L);
            fail("Invalid Client/Channel Validated");
        }catch(ApiException e){
            assertTrue(true);
        }
    }

    @Test
    public void testAddListWithInvalidFormFields() {
        List<ChannelListingForm> formList = new ArrayList<>();
        formList.add(TestForm.getChannelListingForm(null, "CH SKU"));
        formList.add(TestForm.getChannelListingForm("CL SKU", null));
        formList.add(TestForm.getChannelListingForm("ABC SKU", "DEF SKU"));
        formList.add(TestForm.getChannelListingForm("ABC SKU", "DEF SKU"));

        try{
            listingDto.addList(formList, testChannelA.getId(), testClient.getId());
            fail("Invalid form fields validated");
        }catch(ApiException e){
            assertTrue(true);
        }
    }

    @Test
    public void testValidateListWithValidList() throws ApiException {
        List<ChannelListingForm> formList = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            formList.add(TestForm.getChannelListingForm("CL SKU" + i, "CH SKU" + i));

        listingDto.validateFormList(formList, testChannelA.getId(), testClient.getId());
    }

    @Test
    public void testValidateListWithInvalidClientChannel(){
        List<ChannelListingForm> formList = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            formList.add(TestForm.getChannelListingForm("CL SKU" + i, "CH SKU" + i));

        try{
            listingDto.validateFormList(formList, 123L, 456L);
            fail("Invalid Client Channel details validated");
        }catch (ApiException e){
            assertTrue(true);
        }
    }

    @Test
    public void testValidateListWithInvalidFormFields(){
        List<ChannelListingForm> formList = new ArrayList<>();
        formList.add(TestForm.getChannelListingForm(null, "CH SKU"));
        formList.add(TestForm.getChannelListingForm("CL SKU", null));
        formList.add(TestForm.getChannelListingForm("ABC SKU", "DEF SKU"));
        formList.add(TestForm.getChannelListingForm("ABC DEF", "DEF SKU"));
        formList.add(TestForm.getChannelListingForm("ABC DEF", "PQR SKU"));

        try{
            listingDto.validateFormList(formList, testChannelA.getId(), testClient.getId());
            fail("Invalid form fields validated");
        } catch(ApiException e){
            assertTrue(true);
        }
    }

    @Test
    public void tesGetSearchWithInvalidClient(){
        ChannelListingSearchForm form = new ChannelListingSearchForm();
        form.setClientId(345L);
        try{
            listingDto.getSearch(form);
            fail("Invalid ID allowed to be searched");
        }catch (ApiException e){
            assertTrue(true);
        }
    }

    @Test
    public void tesGetSearchWithInvalidChannel(){
        ChannelListingSearchForm form = new ChannelListingSearchForm();
        form.setChannelId(345L);
        try{
            listingDto.getSearch(form);
            fail("Invalid ID allowed to be searched");
        }catch (ApiException e){
            assertTrue(true);
        }
    }

    @Test
    public void testGetSearchByClientIdWithValidClient() throws ApiException {
        ConsumerPojo clientA = TestPojo.getConsumerPojo("A", ConsumerType.CLIENT);
        ConsumerPojo clientB = TestPojo.getConsumerPojo("B", ConsumerType.CLIENT);
        ConsumerPojo clientC = TestPojo.getConsumerPojo("C", ConsumerType.CLIENT);
        consumerDao.insert(clientA);
        consumerDao.insert(clientB);
        consumerDao.insert(clientC);

        ProductMasterPojo productA = TestPojo.getProductPojo("A", clientA.getId(), "BIA", 100D, "CLSKU1", "DE1");
        ProductMasterPojo productB = TestPojo.getProductPojo("B", clientB.getId(), "BIB", 100D, "CLSKU2", "DE1");
        ProductMasterPojo productC = TestPojo.getProductPojo("C", clientB.getId(), "BIC", 100D, "CLSKU3", "DE1");
        productDao.insert(productA);
        productDao.insert(productB);
        productDao.insert(productC);

        ChannelPojo channelA = TestPojo.getChannelPojo("A", InvoiceType.SELF);
        ChannelPojo channelB = TestPojo.getChannelPojo("B", InvoiceType.SELF);
        channelDao.insert(channelA);
        channelDao.insert(channelB);

        listingDao.insert(TestPojo.getChannelListingPojo(productA.getId(), channelA.getId(), "CSKU1", clientA.getId()));
        listingDao.insert(TestPojo.getChannelListingPojo(productB.getId(), channelB.getId(), "CSKU2", clientB.getId()));
        listingDao.insert(TestPojo.getChannelListingPojo(productC.getId(), channelB.getId(), "CSKU3", clientB.getId()));

        ChannelListingSearchForm form = new ChannelListingSearchForm();
        form.setClientId(clientA.getId());
        form.setChannelId(null);
        form.setChannelSkuId("");
        form.setClientSkuId("");

        List<ChannelListingData> searchResults = listingDto.getSearch(form);
        assertEquals(1, searchResults.size());
        assertEquals("CSKU1", searchResults.get(0).getChannelSkuId());

        form.setClientId(clientB.getId());
        searchResults = listingDto.getSearch(form);
        assertEquals(2, searchResults.size());

        form.setClientId(clientC.getId());
        searchResults = listingDto.getSearch(form);
        assertEquals(0, searchResults.size());
    }

    @Test
    public void testGetSearchByClientAndChannel() throws ApiException {
        ConsumerPojo clientA = TestPojo.getConsumerPojo("A", ConsumerType.CLIENT);
        ConsumerPojo clientB = TestPojo.getConsumerPojo("B", ConsumerType.CLIENT);
        ConsumerPojo clientC = TestPojo.getConsumerPojo("C", ConsumerType.CLIENT);
        consumerDao.insert(clientA);
        consumerDao.insert(clientB);
        consumerDao.insert(clientC);

        ProductMasterPojo productA = TestPojo.getProductPojo("A", clientA.getId(), "BIA", 100D, "CLSKU1", "DE1");
        ProductMasterPojo productB = TestPojo.getProductPojo("B", clientB.getId(), "BIB", 100D, "CLSKU2", "DE1");
        ProductMasterPojo productC = TestPojo.getProductPojo("C", clientB.getId(), "BIC", 100D, "CLSKU3", "DE1");
        productDao.insert(productA);
        productDao.insert(productB);
        productDao.insert(productC);

        ChannelPojo channelA = TestPojo.getChannelPojo("A", InvoiceType.SELF);
        ChannelPojo channelB = TestPojo.getChannelPojo("B", InvoiceType.SELF);
        channelDao.insert(channelA);
        channelDao.insert(channelB);

        listingDao.insert(TestPojo.getChannelListingPojo(productA.getId(), channelA.getId(), "CSKU1", clientA.getId()));
        listingDao.insert(TestPojo.getChannelListingPojo(productB.getId(), channelB.getId(), "CSKU2", clientB.getId()));
        listingDao.insert(TestPojo.getChannelListingPojo(productC.getId(), channelB.getId(), "CSKU3", clientB.getId()));

        ChannelListingSearchForm form = new ChannelListingSearchForm();
        form.setClientId(clientA.getId());
        form.setChannelId(channelA.getId());
        form.setChannelSkuId("");
        form.setClientSkuId("");

        assertEquals(1, listingDto.getSearch(form).size());
        assertEquals("CSKU1", listingDto.getSearch(form).get(0).getChannelSkuId());

        form.setClientId(clientA.getId());
        form.setChannelId(channelB.getId());
        assertEquals(0, listingDto.getSearch(form).size());
    }

    @Test
    public void testGetSearchByClientIdWithValidChannelId() throws ApiException {
        ConsumerPojo clientA = TestPojo.getConsumerPojo("A", ConsumerType.CLIENT);
        ConsumerPojo clientB = TestPojo.getConsumerPojo("B", ConsumerType.CLIENT);
        ConsumerPojo clientC = TestPojo.getConsumerPojo("C", ConsumerType.CLIENT);
        consumerDao.insert(clientA);
        consumerDao.insert(clientB);
        consumerDao.insert(clientC);

        ProductMasterPojo productA = TestPojo.getProductPojo("A", clientA.getId(), "BIA", 100D, "CLSKU1", "DE1");
        ProductMasterPojo productB = TestPojo.getProductPojo("B", clientB.getId(), "BIB", 100D, "CLSKU2", "DE1");
        ProductMasterPojo productC = TestPojo.getProductPojo("C", clientB.getId(), "BIC", 100D, "CLSKU3", "DE1");
        productDao.insert(productA);
        productDao.insert(productB);
        productDao.insert(productC);

        ChannelPojo channelA = TestPojo.getChannelPojo("A", InvoiceType.SELF);
        ChannelPojo channelB = TestPojo.getChannelPojo("B", InvoiceType.SELF);
        ChannelPojo channelC = TestPojo.getChannelPojo("C", InvoiceType.SELF);
        channelDao.insert(channelA);
        channelDao.insert(channelB);
        channelDao.insert(channelC);

        listingDao.insert(TestPojo.getChannelListingPojo(productA.getId(), channelA.getId(), "CSKU1", clientA.getId()));
        listingDao.insert(TestPojo.getChannelListingPojo(productB.getId(), channelB.getId(), "CSKU2", clientB.getId()));
        listingDao.insert(TestPojo.getChannelListingPojo(productC.getId(), channelB.getId(), "CSKU3", clientB.getId()));

        ChannelListingSearchForm form = new ChannelListingSearchForm();
        form.setChannelId(channelA.getId());
        form.setChannelSkuId("");
        form.setClientSkuId("");

        List<ChannelListingData> searchResults = listingDto.getSearch(form);
        assertEquals(1, searchResults.size());
        assertEquals("CSKU1", searchResults.get(0).getChannelSkuId());

        form.setChannelId(channelB.getId());
        searchResults = listingDto.getSearch(form);
        assertEquals(2, searchResults.size());

        form.setChannelId(channelC.getId());
        searchResults = listingDto.getSearch(form);
        assertEquals(0, searchResults.size());
    }

}