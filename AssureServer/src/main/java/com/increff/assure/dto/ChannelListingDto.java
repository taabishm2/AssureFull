package com.increff.assure.dto;

import com.increff.assure.pojo.ChannelListingPojo;
import com.increff.assure.service.ApiException;
import com.increff.assure.service.ChannelListingService;
import com.increff.assure.service.ChannelService;
import com.increff.assure.service.ProductMasterService;
import com.increff.assure.util.CheckValid;
import com.increff.assure.util.ConvertUtil;
import com.increff.assure.util.NormalizeUtil;
import model.data.ChannelListingData;
import model.form.ChannelListingForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Transactional(readOnly = true)
    public ChannelListingData get(Long id) throws ApiException {
        ChannelListingPojo channelListingPojo = channelListingService.getCheckId(id);
        return convertPojoToData(channelListingPojo);
    }

    private ChannelListingData convertPojoToData(ChannelListingPojo channelListingPojo) throws ApiException {
        ChannelListingData listingData = convert(channelListingPojo, ChannelListingData.class);
        listingData.setClientSkuId(productService.getCheckId(channelListingPojo.getGlobalSkuId()).getClientSkuId());
        listingData.setChannelName(channelService.getCheckId(channelListingPojo.getChannelId()).getName());
        return listingData;
    }

    @Transactional(rollbackFor = ApiException.class)
    public void add(ChannelListingForm channelListingForm) throws ApiException {
        ChannelListingPojo channelListingPojo = convert(channelListingForm, ChannelListingPojo.class);
        validateChannelListing(channelListingPojo);

        channelListingService.add(channelListingPojo);
    }

    @Transactional(readOnly = true)
    public List<ChannelListingData> getAll() throws ApiException {
        List<ChannelListingData> allListingData = new ArrayList<>();
        for (ChannelListingPojo listingPojo : channelListingService.getAll())
            allListingData.add(convertPojoToData(listingPojo));
        return allListingData;
    }

    @Transactional(readOnly = true)
    private void validateChannelListing(ChannelListingPojo listingPojo) throws ApiException {
        channelService.getCheckId(listingPojo.getChannelId());
        productService.getCheckId(listingPojo.getGlobalSkuId());
    }

    public void addList(List<ChannelListingForm> formList, Long channelId) throws ApiException {
        channelService.getCheckId(channelId);

        List<ChannelListingPojo> channelListingPojos = new ArrayList<>();
        for (ChannelListingForm listingForm : formList) {
            normalizeAndValidateForm(listingForm);
            channelListingPojos.add(convertFormToPojo(listingForm, channelId));
        }
        channelListingService.addList(channelListingPojos);
    }

    public void normalizeAndValidateForm(ChannelListingForm listingForm) throws ApiException {
        NormalizeUtil.normalize(listingForm);
        CheckValid.validate((listingForm));
    }

    private ChannelListingPojo convertFormToPojo(ChannelListingForm listingForm, Long channelId) throws ApiException {
        ChannelListingPojo listingPojo = ConvertUtil.convert(listingForm, ChannelListingPojo.class);
        listingPojo.setChannelId(channelId);

        listingPojo.setGlobalSkuId(productService.getByClientAndClientSku(listingForm.getClientId(), listingForm.getClientSkuId()).getId());
        return listingPojo;
    }
}