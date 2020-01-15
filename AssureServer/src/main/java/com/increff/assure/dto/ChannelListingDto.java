package com.increff.assure.dto;

import com.increff.assure.pojo.ChannelListingPojo;
import com.increff.assure.service.*;
import com.increff.assure.util.ConvertUtil;
import com.increff.assure.util.FileWriteUtil;
import com.increff.assure.util.NormalizeUtil;
import model.data.ChannelListingData;
import model.data.MessageData;
import model.form.ChannelListingForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.increff.assure.util.ConvertUtil.convert;

@Service
public class ChannelListingDto extends AbstractDto {
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
        return convertPojoToData(channelListingService.getCheckId(id));
    }

    private ChannelListingData convertPojoToData(ChannelListingPojo channelListingPojo) throws ApiException {
        ChannelListingData listingData = convert(channelListingPojo, ChannelListingData.class);
        listingData.setClientSkuId(productService.getCheckId(channelListingPojo.getGlobalSkuId()).getClientSkuId());
        listingData.setChannelName(channelService.getCheckId(channelListingPojo.getChannelId()).getName());
        listingData.setClientName(consumerService.getCheckId(channelListingPojo.getClientId()).getName());
        return listingData;
    }

    @Transactional(readOnly = true)
    public List<ChannelListingData> getAll() throws ApiException {
        List<ChannelListingData> allListingData = new ArrayList<>();
        for (ChannelListingPojo listingPojo : channelListingService.getAll())
            allListingData.add(convertPojoToData(listingPojo));
        return allListingData;
    }


    public void addList(List<ChannelListingForm> formList, Long channelId, Long clientId) throws ApiException {
        channelService.getCheckId(channelId);
        consumerService.getCheckClient(clientId);

        List<ChannelListingPojo> channelListingPojos = new ArrayList<>();
        for (ChannelListingForm listingForm : formList) {
            NormalizeUtil.normalize(listingForm);
            checkValid((listingForm));

            channelListingPojos.add(convertFormToPojo(listingForm, channelId, clientId));
        }
        channelListingService.addList(channelListingPojos);
    }

    private ChannelListingPojo convertFormToPojo(ChannelListingForm listingForm, Long channelId, Long clientId) throws ApiException {
        ChannelListingPojo listingPojo = ConvertUtil.convert(listingForm, ChannelListingPojo.class);
        listingPojo.setChannelId(channelId);
        listingPojo.setClientId(clientId);
        listingPojo.setGlobalSkuId(productService.getByClientAndClientSku(clientId, listingForm.getClientSkuId()).getId());

        return listingPojo;
    }

    public void validateFormList(List<ChannelListingForm> formList, Long channelId, Long clientId) throws ApiException {
        channelService.getCheckId(channelId);
        consumerService.getCheckClient(clientId);

        List<MessageData> errorMessages = new ArrayList<>();
        HashSet<String> channelSkus = new HashSet<>();
        HashSet<String> clientAndClientSkus = new HashSet<>();

        for (int index = 0; index < formList.size(); index++) {
            try {
                ChannelListingForm form = formList.get(index);
                checkValid(form);

                if (channelSkus.contains(form.getChannelSkuId()))
                    throw new ApiException("Duplicate Channel SKU");
                else
                    channelSkus.add(form.getChannelSkuId());

                if (clientAndClientSkus.contains(clientId + "," + form.getClientSkuId()))
                    throw new ApiException("Duplicate Client, ClientSKU");
                else
                    clientAndClientSkus.add(clientId + "," + form.getClientSkuId());

            } catch (ApiException e) {
                MessageData errorMessage = new MessageData();
                errorMessage.setMessage("Error in Line: " + index + ": " + e.getMessage() + "\n");
                errorMessages.add(errorMessage);
            }
        }

        if (errorMessages.size() != 0)
            throw new ApiException(FileWriteUtil.writeErrorsToFile("channelListing" + formList.hashCode(), errorMessages));
    }
}
