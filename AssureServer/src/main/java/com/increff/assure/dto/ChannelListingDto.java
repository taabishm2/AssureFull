package com.increff.assure.dto;

import com.increff.assure.pojo.ChannelListingPojo;
import com.increff.assure.service.ApiException;
import com.increff.assure.service.ChannelListingService;
import com.increff.assure.service.ChannelService;
import com.increff.assure.service.ProductMasterService;
import model.data.ChannelListingData;
import model.form.ChannelListingForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.increff.assure.util.ConvertUtil.convert;

@Service
public class ChannelListingDto {
    @Autowired
    private ChannelService channelService;
    @Autowired
    private ProductMasterService productService;
    @Autowired
    private ChannelListingService channelListingService;

    public ChannelListingData get(Long id) throws ApiException {
        ChannelListingPojo channelListingPojo = channelListingService.getCheckId(id);
        return convert(channelListingPojo, ChannelListingData.class);
    }

    public void add(ChannelListingForm channelListingForm) throws ApiException {
        ChannelListingPojo channelListingPojo = convert(channelListingForm, ChannelListingPojo.class);
        validateChannelListing(channelListingPojo);

        channelListingService.add(channelListingPojo);
    }

    public List<ChannelListingData> getAll() throws ApiException {
        return convert(channelListingService.getAll(), ChannelListingData.class);
    }

    private void validateChannelListing(ChannelListingPojo listingPojo) throws ApiException {
        validateChannel(listingPojo.getChannelId());
        validateProduct(listingPojo.getGlobalSkuId());
    }

    private void validateProduct(Long globalSkuId) throws ApiException {
        productService.getCheckId(globalSkuId);
    }

    private void validateChannel(Long channelId) throws ApiException {
        channelService.getCheckId(channelId);
    }
}