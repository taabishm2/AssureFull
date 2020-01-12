package com.increff.assure.controller;

import com.increff.assure.dto.ChannelListingDto;
import com.increff.assure.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import model.data.ChannelListingData;
import model.form.ChannelListingForm;
import model.form.ProductMasterForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api
@RestController
public class ChannelListingController {
    @Autowired
    private ChannelListingDto channelListingDto;

    @ApiOperation(value = "Add a List of Channel Listings for a Channel")
    @RequestMapping(path = "/api/channelListing/list/{channelId}", method = RequestMethod.POST)
    public void addList(@PathVariable Long channelId, @Valid @RequestBody List<ChannelListingForm> formList) throws ApiException {
        channelListingDto.addList(formList, channelId);
    }

    @ApiOperation(value = "Gets a Channel-Listing by ID")
    @RequestMapping(path = "/api/channelListing/{id}", method = RequestMethod.GET)
    public ChannelListingData get(@PathVariable Long id) throws ApiException {
        return channelListingDto.get(id);
    }

    @ApiOperation(value = "Gets list of all Channel-Listing")
    @RequestMapping(path = "/api/channelListing", method = RequestMethod.GET)
    public List<ChannelListingData> getAll() throws ApiException {
        return channelListingDto.getAll();
    }

    @ApiOperation(value = "Validate List of Channel Listings for a Channel")
    @RequestMapping(path = "/api/channelListing/validate/{channelId}", method = RequestMethod.POST)
    public void validateList(@PathVariable Long channelId, @Valid @RequestBody List<ChannelListingForm> formList) throws ApiException {
        channelListingDto.validateList(formList, channelId);
    }
}
