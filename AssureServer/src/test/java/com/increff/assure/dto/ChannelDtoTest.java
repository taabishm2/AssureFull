package com.increff.assure.dto;

import com.increff.assure.dao.ChannelDao;
import com.increff.assure.pojo.ChannelPojo;
import com.increff.assure.service.ApiException;
import model.InvoiceType;
import model.form.ChannelForm;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class ChannelDtoTest extends AbstractUnitTest {

    @Autowired
    ChannelDto channelDto;
    @Autowired
    ChannelDao channelDao;

    @Test
    public void testGet() throws ApiException {
        ChannelPojo channelPojo = TestPojo.getConstructChannel("SNAPDEAL",InvoiceType.CHANNEL);
        channelDao.insert(channelPojo);

        assertEquals(channelPojo.getName(), channelDto.get(channelPojo.getId()).getName());
        assertEquals(channelPojo.getInvoiceType(), channelDto.get(channelPojo.getId()).getInvoiceType());
    }

    @Test
    public void testGetInvalidChannel() throws ApiException {
        try{
            channelDto.get(123L);
            fail("Invalid channel selected");
        } catch (ApiException e){
            assertTrue(true);
        }
    }

    @Test
    public void testAdd() throws ApiException {
        ChannelForm form = TestForm.getChannelForm("  mYnTra ",InvoiceType.CHANNEL);
        channelDto.add(form);

        assertNotNull(channelDao.selectByNameAndType("myntra",InvoiceType.CHANNEL));
    }

    @Test
    public void testAddWithInvalidInput() throws ApiException {
        try{
            channelDto.add(TestForm.getChannelForm("",InvoiceType.CHANNEL));
            channelDto.add(TestForm.getChannelForm("ABC",null));
            fail("Channel with null fields Inserted");
        }catch (ApiException e){
            assertTrue(true);
        }
    }
}