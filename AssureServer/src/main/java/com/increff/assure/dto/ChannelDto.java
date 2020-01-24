package com.increff.assure.dto;

import com.increff.assure.pojo.ChannelListingPojo;
import com.increff.assure.pojo.ChannelPojo;
import com.increff.assure.pojo.ProductMasterPojo;
import com.increff.assure.service.*;
import com.increff.assure.util.ConvertUtil;
import com.increff.assure.util.NormalizeUtil;
import model.InvoiceType;
import model.data.ChannelData;
import model.data.ChannelListingData;
import model.form.ChannelForm;
import model.form.ChannelListingForm;
import model.form.ChannelListingSearchForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static com.increff.assure.util.ConvertUtil.convert;

@Service
public class ChannelDto extends AbstractDto {
    @Autowired
    private ChannelService channelService;
    @Autowired
    private ProductMasterService productService;
    @Autowired
    private ChannelListingService channelListingService;
    @Autowired
    private ConsumerService consumerService;

    @Transactional(readOnly = true)
    public ChannelData get(Long id) throws ApiException {
        return convert(channelService.getCheckId(id), ChannelData.class);
    }

    @Transactional(rollbackFor = ApiException.class)
    public void add(ChannelForm channelForm) throws ApiException {
        NormalizeUtil.normalize(channelForm);
        checkValid(channelForm);

        channelService.add(convert(channelForm, ChannelPojo.class));
    }

    @Transactional(readOnly = true)
    public List<ChannelData> getAll() throws ApiException {
        return convert(channelService.getAll(), ChannelData.class);
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

    public void validateFormList(List<ChannelListingForm> formList, Long channelId, Long clientId) throws ApiException {
        channelService.getCheckId(channelId);
        consumerService.getCheckClient(clientId);

        StringBuilder errorDetailString = new StringBuilder();
        HashSet<String> channelSkus = new HashSet<>();
        HashSet<String> clientAndClientSkus = new HashSet<>();

        for (int index = 0; index < formList.size(); index++) {
            try {
                ChannelListingForm form = formList.get(index);
                checkValid(form);
                checkFalse(channelSkus.contains(form.getChannelSkuId()), "Duplicate Channel SKU present");
                checkFalse(clientAndClientSkus.contains(clientId + "," + form.getClientSkuId()),"Duplicate Client, ClientSKU present");

                channelSkus.add(form.getChannelSkuId());
                clientAndClientSkus.add(clientId + "," + form.getClientSkuId());
            } catch (ApiException e) {
                errorDetailString.append("Error in Line: ").append(index + 1).append(": ").append(e.getMessage()).append("<br \\>");
            }
        }

        if (errorDetailString.length() > 0)
            throw new ApiException(errorDetailString.toString());
    }

    public List<ChannelListingData> getSearch(ChannelListingSearchForm form) throws ApiException {
        if (Objects.nonNull(form.getClientId()))
            consumerService.getCheckClient(form.getClientId());

        if (Objects.nonNull(form.getChannelId()))
            channelService.getCheckId(form.getChannelId());

        List<ProductMasterPojo> productsByClientSku = productService.getByClientSku(form.getClientSkuId());
        if(productsByClientSku.isEmpty())
            return convertPojoToData(channelListingService.getSearch(form.getChannelId(), form.getClientId(),
                    form.getChannelSkuId(),null));

        List<ChannelListingData> searchResults = new ArrayList<>();
        for(ProductMasterPojo product:productsByClientSku)
            searchResults.addAll(convertPojoToData(channelListingService.getSearch(form.getChannelId(), form.getClientId(),
                    form.getChannelSkuId(), product.getId())));
        return searchResults;
    }

    private ChannelListingPojo convertFormToPojo(ChannelListingForm listingForm, Long channelId, Long clientId) throws ApiException {
        ChannelListingPojo listingPojo = ConvertUtil.convert(listingForm, ChannelListingPojo.class);
        listingPojo.setChannelId(channelId);
        listingPojo.setClientId(clientId);
        listingPojo.setGlobalSkuId(productService.getByClientAndClientSku(clientId, listingForm.getClientSkuId()).getId());

        return listingPojo;
    }

    private ChannelListingData convertPojoToData(ChannelListingPojo channelListingPojo) throws ApiException {
        ChannelListingData listingData = convert(channelListingPojo, ChannelListingData.class);
        listingData.setClientSkuId(productService.getCheckId(channelListingPojo.getGlobalSkuId()).getClientSkuId());
        listingData.setChannelName(channelService.getCheckId(channelListingPojo.getChannelId()).getName());
        listingData.setClientName(consumerService.getCheckId(channelListingPojo.getClientId()).getName());
        return listingData;
    }

    private List<ChannelListingData> convertPojoToData(List<ChannelListingPojo> listingPojoList) throws ApiException {
        List<ChannelListingData> listingData = new ArrayList<>();
        for (ChannelListingPojo listingPojo : listingPojoList) {
            listingData.add(convertPojoToData(listingPojo));
        }
        return listingData;
    }
}