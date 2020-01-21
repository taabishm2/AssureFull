package com.increff.assure.dto;

import com.increff.assure.dao.ChannelDao;
import com.increff.assure.dao.ChannelListingDao;
import com.increff.assure.dao.ConsumerDao;
import com.increff.assure.dao.ProductMasterDao;
import com.increff.assure.pojo.ChannelPojo;
import com.increff.assure.pojo.ConsumerPojo;
import com.increff.assure.service.ApiException;
import model.ConsumerType;
import model.InvoiceType;
import model.data.ProductMasterData;
import model.form.ChannelListingForm;
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

}