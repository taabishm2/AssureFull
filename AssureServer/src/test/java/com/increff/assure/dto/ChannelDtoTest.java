package com.increff.assure.dto;

import com.increff.assure.dao.ChannelDao;
import com.increff.assure.pojo.ChannelPojo;
import com.increff.assure.service.ApiException;
import model.InvoiceType;
import model.data.ChannelData;
import model.form.ChannelForm;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ChannelDtoTest extends AbstractUnitTest {

    @Autowired
    ChannelDto channelDto;
    @Autowired
    ChannelDao channelDao;

    @Test
    public void testInit() throws ApiException {
        channelDto.init();
        channelDto.init();
        ChannelPojo internalChannel = channelDao.selectByName("INTERNAL");

        assertEquals(1, channelDao.selectAll().size());
        assertNotNull(internalChannel);
        assertEquals(InvoiceType.SELF, internalChannel.getInvoiceType());
    }

    @Test
    public void testGet() throws ApiException {
        ChannelPojo newChannel = PojoConstructor.getConstructChannel("SNAPDEAL",InvoiceType.CHANNEL);
        channelDao.insert(newChannel);

        assertEquals(newChannel.getName(), channelDto.get(newChannel.getId()).getName());
        assertEquals(newChannel.getInvoiceType(), channelDto.get(newChannel.getId()).getInvoiceType());
    }

    @Test
    public void testAdd() throws ApiException {
        ChannelForm form = FormConstructor.getConstructChannel("  mYnTra ",InvoiceType.CHANNEL);
        channelDto.add(form);

        assertNotNull(channelDao.selectByName("MYNTRA"));
        assertEquals(InvoiceType.CHANNEL, channelDao.selectByName("MYNTRA").getInvoiceType());
    }
}