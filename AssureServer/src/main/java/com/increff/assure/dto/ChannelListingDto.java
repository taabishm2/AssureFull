package com.increff.assure.dto;

import com.increff.assure.pojo.ChannelListingPojo;
import com.increff.assure.service.*;
import com.increff.assure.util.CheckValid;
import com.increff.assure.util.ConvertUtil;
import com.increff.assure.util.FileWriteUtil;
import com.increff.assure.util.NormalizeUtil;
import model.ConsumerType;
import model.data.ChannelListingData;
import model.data.MessageData;
import model.form.ChannelListingForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static com.increff.assure.util.ConvertUtil.convert;

@Service
public class ChannelListingDto {
    @Autowired
    private ChannelService channelService;
    @Autowired
    private ProductMasterService productService;
    @Autowired
    private ChannelListingService channelListingService;
    @Autowired
    private ConsumerService consumerService;

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

    public void validateList(List<ChannelListingForm> formList, Long channelId) throws ApiException {
        channelService.getCheckId(channelId);
        List<MessageData> errorMessages = new ArrayList<>();
        HashSet<String> channelSkus = new HashSet<>();
        HashSet<String> clientAndClientSkus = new HashSet<>();

        for (int i = 0; i < formList.size(); i++) {
            try {
                ChannelListingForm form = formList.get(i);
                CheckValid.validate(form);
                if (!consumerService.getCheckId(form.getClientId()).getType().equals(ConsumerType.CLIENT))
                    throw new ApiException("Client ID Invalid");

                if (channelSkus.contains(form.getChannelSkuId()))
                    throw new ApiException("Duplicate Channel SKU");
                else
                    channelSkus.add(form.getChannelSkuId());

                if (Objects.isNull(productService.getByClientAndClientSku(form.getClientId(), form.getClientSkuId())))
                    throw new ApiException("Invalid Client, ClientSKU pair");

                if (clientAndClientSkus.contains(form.getClientId() + "," + form.getClientSkuId()))
                    throw new ApiException("Duplicate Client, ClientSKU");
                else
                    clientAndClientSkus.add(form.getClientId() + "," + form.getClientSkuId());

            } catch (ApiException e) {
                MessageData errorMessage = new MessageData();
                errorMessage.setMessage("Error in Line: " + i + ": " + e.getMessage() + "\n");
                errorMessages.add(errorMessage);
            }
        }

        if (errorMessages.size() != 0)
            throw new ApiException(FileWriteUtil.writeErrorsToFile("channelListing" + formList.hashCode(), errorMessages));
    }
}
