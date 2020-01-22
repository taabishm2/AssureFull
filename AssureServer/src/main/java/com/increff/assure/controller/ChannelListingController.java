package com.increff.assure.controller;

import com.increff.assure.dto.ChannelListingDto;
import com.increff.assure.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import model.data.ChannelListingData;
import model.form.ChannelListingForm;
import model.form.ChannelListingSearchForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
public class ChannelListingController {
    @Autowired
    private ChannelListingDto channelListingDto;

    @ApiOperation(value = "Add a Channel Listings for given Channel and Client")
    @RequestMapping(path = "/api/channelListing/list/{channelId}/{clientId}", method = RequestMethod.POST)
    public void addList(@PathVariable Long channelId, @PathVariable Long clientId, @RequestBody List<ChannelListingForm> formList) throws ApiException {
        channelListingDto.addList(formList, channelId, clientId);
    }

    @ApiOperation(value = "Validate List of Channel Listings for a Channel")
    @RequestMapping(path = "/api/channelListing/validate/{channelId}/{clientId}", method = RequestMethod.POST)
    public void validateList(@PathVariable Long channelId, @PathVariable Long clientId, @RequestBody List<ChannelListingForm> formList) throws ApiException {
        channelListingDto.validateFormList(formList, channelId, clientId);
    }

    @ApiOperation(value = "Search Channel Listing Entries")
    @RequestMapping(path = "/api/channelListing/search", method = RequestMethod.POST)
    public List<ChannelListingData> getSearch(@RequestBody ChannelListingSearchForm form) throws ApiException {
        return channelListingDto.getSearch(form);
    }
}