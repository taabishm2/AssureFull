package com.increff.assure.service;

import com.increff.assure.dao.ChannelDao;
import com.increff.assure.pojo.ChannelPojo;
import model.InvoiceType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ChannelServiceTest extends AbstractUnitTest {

    @Autowired
    ChannelService channelService;
    @Autowired
    ChannelDao channelDao;

    private ChannelPojo channel;

    @Before
    public void init() {
        channel = TestPojo.getConstructChannel("FLIPKART", InvoiceType.CHANNEL);
    }

    @Test
    public void testAdd() throws ApiException {
        int initialCount = channelDao.selectAll().size();
        channelService.add(channel);
        assertEquals(1, channelDao.selectAll().size() - initialCount);

        try {
            channelService.add(channel);
            fail("Duplicate Channel Inserted");
        } catch (ApiException e) {
            assertEquals("Channel (NAME:FLIPKART) already exists.", e.getMessage());
        }
    }
}