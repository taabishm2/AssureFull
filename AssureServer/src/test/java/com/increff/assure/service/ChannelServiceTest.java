package com.increff.assure.service;

import com.increff.assure.dao.ChannelDao;
import com.increff.assure.pojo.ChannelPojo;
import model.InvoiceType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class ChannelServiceTest extends AbstractUnitTest {

    @Autowired
    ChannelService channelService;
    @Autowired
    ChannelDao channelDao;

    private ChannelPojo channel;

    @Before
    public void init() {
        channel = TestPojo.getChannelPojo("FLIPKART", InvoiceType.CHANNEL);
    }

    @Test
    public void testAdd() throws ApiException {
        channelService.add(channel);
        assertEquals(1, channelDao.selectAll().size());
    }

    @Test
    public void testAddDuplicateChannel(){
        channelDao.insert(channel);
        try {
            channelService.add(channel);
            fail("Duplicate Channel Inserted");
        } catch (ApiException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testGetAll(){
        channelDao.insert(channel);
        assertEquals(1, channelService.getAll().size());

        channelDao.insert(TestPojo.getChannelPojo("Test Name", InvoiceType.CHANNEL));
        assertEquals(2, channelService.getAll().size());
    }

    @Test
    public void testGetCheckIdWithValidId() throws ApiException {
        channelDao.insert(channel);
        assertEquals(channel, channelService.getCheckId(channel.getId()));
    }

    @Test
    public void testGetCheckIdWithInvalidId(){
        try{
            channelService.getCheckId(124L);
            fail("Invalid ID selected");
        } catch (ApiException e){
            assertTrue(true);
        }
    }
}