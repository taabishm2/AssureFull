package com.increff.assure.service;

import com.increff.assure.dao.ChannelListingDao;
import com.increff.assure.pojo.ChannelListingPojo;
import com.increff.assure.pojo.ProductMasterPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChannelListingService extends AbstractService {
    @Autowired
    private ChannelListingDao channelListingDao;

    @Transactional(rollbackFor = ApiException.class)
    public void add(ChannelListingPojo listingPojo) throws ApiException {
        checkDuplicateChannelListing(listingPojo);

        channelListingDao.insert(listingPojo);
    }

    private void checkDuplicateChannelListing(ChannelListingPojo listingPojo) throws ApiException {
        Long channelId = listingPojo.getChannelId();
        Long globalSkuId = listingPojo.getGlobalSkuId();
        String channelSkuId = listingPojo.getChannelSkuId();

        ChannelListingPojo duplicateByChannelAndChannelSku = channelListingDao.selectByChannelAndChannelSku(channelId, channelSkuId);
        checkNull(duplicateByChannelAndChannelSku, "Channel has already registered the Channel-SKU-ID: " + listingPojo.getChannelSkuId());

        ChannelListingPojo duplicateByChannelAndProduct = channelListingDao.selectByChannelAndGlobalSku(channelId, globalSkuId);
        checkNull(duplicateByChannelAndProduct, "ChannelID " + channelId + " and GSKU " + globalSkuId + " pair already exists");
    }

    public List<ChannelListingPojo> getAll() {
        return channelListingDao.selectAll();
    }

    public ChannelListingPojo getByChannelIdAndGlobalSku(Long channelId, Long globalSkuId) {
        return channelListingDao.selectByChannelAndGlobalSku(channelId, globalSkuId);
    }

    public ChannelListingPojo getCheckId(Long id) throws ApiException {
        ChannelListingPojo listingPojo = channelListingDao.select(id);
        checkNotNull(listingPojo, "Channel Listing (ID:" + id + ") does not exist.");
        return listingPojo;
    }

    @Transactional(rollbackFor = ApiException.class)
    public void addList(List<ChannelListingPojo> channelListingPojos) throws ApiException {
        for (ChannelListingPojo pojo : channelListingPojos)
            add(pojo);
    }

    public ChannelListingPojo getUnique(Long channelId, String channelSkuId, Long clientId) {
        return channelListingDao.selectUnique(channelId, channelSkuId, clientId);
    }
}