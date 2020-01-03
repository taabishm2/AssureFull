package com.increff.assure.service;

import com.increff.assure.dao.ChannelListingDao;
import com.increff.assure.pojo.ChannelListingPojo;
import com.increff.assure.pojo.ChannelPojo;
import com.increff.assure.pojo.ConsumerPojo;
import com.increff.assure.pojo.ProductMasterPojo;
import model.ConsumerType;
import model.InvoiceType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ChannelListingServiceTest extends AbstractUnitTest {

    @Autowired
    ChannelListingService channelListingService;
    @Autowired
    ChannelListingDao channelListingDao;

    private ConsumerPojo consumerPojo;
    private ProductMasterPojo productMasterPojo;
    private ChannelPojo channelPojo;
    private ChannelListingPojo channelListingPojo;

    @Before
    public void init() {
        consumerPojo = PojoConstructor.getConstructConsumer("PUMA", ConsumerType.CLIENT);
        productMasterPojo = PojoConstructor.getConstructProduct("PUMAX", consumerPojo.getId(), "BrandID", 200D, "SKUID", "Descriiption");
        channelPojo = PojoConstructor.getConstructChannel("FLIPKART", InvoiceType.CHANNEL);

        channelListingPojo = PojoConstructor.getConstructChannelListing(productMasterPojo.getId(), channelPojo.getId(), "ChannelSKU");
    }

    @Test
    public void testAdd() throws ApiException {
        int initialCount = channelListingDao.selectAll().size();
        channelListingService.add(channelListingPojo);
        assertEquals(1, channelListingDao.selectAll().size() - initialCount);

        try {
            channelListingService.add(PojoConstructor.getConstructChannelListing(123L, channelPojo.getId(), "ChannelSKU"));
            fail("Duplicate Channel & Channel ID inserted");
        } catch (ApiException e) {
            assertEquals("Channel has already registered the Channel-Order-ID: ChannelSKU", e.getMessage());
        }

        try {
            channelListingService.add(PojoConstructor.getConstructChannelListing(productMasterPojo.getId(), channelPojo.getId(), "ChannelSKU"));
            fail("Duplicate Channel & Channel ID inserted");
        } catch (ApiException e) {
            assertEquals("ChannelID " + channelPojo.getId() + "and GSKU " + productMasterPojo.getId() + " pair already exists", e.getMessage());
        }
    }
}