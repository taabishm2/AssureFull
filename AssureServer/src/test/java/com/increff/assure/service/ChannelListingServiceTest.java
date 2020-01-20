package com.increff.assure.service;

import com.increff.assure.dao.ChannelDao;
import com.increff.assure.dao.ChannelListingDao;
import com.increff.assure.dao.ConsumerDao;
import com.increff.assure.dao.ProductMasterDao;
import com.increff.assure.pojo.ChannelListingPojo;
import com.increff.assure.pojo.ChannelPojo;
import com.increff.assure.pojo.ConsumerPojo;
import com.increff.assure.pojo.ProductMasterPojo;
import model.ConsumerType;
import model.InvoiceType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ChannelListingServiceTest extends AbstractUnitTest {

    @Autowired
    ChannelListingService channelListingService;
    @Autowired
    ChannelListingDao channelListingDao;

    @Test
    public void testAddValidEntry() throws ApiException {
        channelListingService.add(TestPojo.getChannelListingPojo(123L, 987L, "ChannelSKU", 567L));
        assertEquals(1, channelListingDao.selectAll().size());

        channelListingService.add(TestPojo.getChannelListingPojo(1253L, 987L, "ChannelSKU1", 567L));
        assertEquals(2, channelListingDao.selectAll().size());
    }

    @Test
    public void testAddDuplicateChannelClientAndChannelSku(){
        channelListingDao.insert(TestPojo.getChannelListingPojo(123L, 987L, "ChannelSKU", 567L));
        try {
            channelListingService.add(TestPojo.getChannelListingPojo(456L, 987L, "ChannelSKU", 567L));
            fail("Duplicate Channel & Channel SKU inserted for same Client");
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testAddDuplicateChannelAndGsku(){
        channelListingDao.insert(TestPojo.getChannelListingPojo(123L, 987L, "CSKU1", 567L));
        try {
            channelListingService.add(TestPojo.getChannelListingPojo(123L, 987L, "CSKU2", 567L));
            fail("Duplicate Product for same Channel Present");
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testGetAllWithEmptyTable(){
        assertEquals(0, channelListingService.getAll().size());
    }

    @Test
    public void testGetAll(){
        channelListingDao.insert(TestPojo.getChannelListingPojo(123L, 987L, "CSKU1", 567L));
        assertEquals(1, channelListingService.getAll().size());

        channelListingDao.insert(TestPojo.getChannelListingPojo(125L, 985L, "CSKU2", 567L));
        assertEquals(2, channelListingService.getAll().size());
    }

    @Test
    public void testGetCheckIdWithValidId() throws ApiException {
        ChannelListingPojo listingPojo = TestPojo.getChannelListingPojo(123L, 987L, "CSKU1", 567L);
        channelListingDao.insert(listingPojo);

        assertEquals(channelListingService.getCheckId(listingPojo.getId()), listingPojo);
    }

    @Test
    public void testGetCheckIdWithInvalidId(){
        try {
            channelListingService.getCheckId(1234L);
            fail("Invalid Channel ID validated");
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testAddList() throws ApiException {
        List<ChannelListingPojo> listingPojos = new ArrayList<>();
        for(long i = 0L; i<5L; i++)
            listingPojos.add(TestPojo.getChannelListingPojo(i,i*10,"CSKU"+i,i+123));

        channelListingService.addList(listingPojos);
        assertEquals(5, channelListingDao.selectAll().size());
    }
}