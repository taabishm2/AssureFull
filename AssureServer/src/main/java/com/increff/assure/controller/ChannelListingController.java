package com.increff.assure.controller;

import com.increff.assure.dto.ChannelListingDto;
import com.increff.assure.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import model.data.ChannelListingData;
import model.form.ChannelListingForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api
@RestController
public class ChannelListingController {
    @Autowired
    private ChannelListingDto channelListingDto;

    @ApiOperation(value = "Adds a Channel-Listing")
    @RequestMapping(path = "/api/channelListing", method = RequestMethod.POST)
    //TODO: Remove @Valid, use CheckValid()
    public void add(@Valid @RequestBody ChannelListingForm form) throws ApiException {
        channelListingDto.add(form);
    }

    @ApiOperation(value = "Gets a Channel-Listing by ID")
    @RequestMapping(path = "/api/channelListing/{id}", method = RequestMethod.GET)
    public ChannelListingData get(@PathVariable long id) throws ApiException {
        return channelListingDto.get(id);
    }

    @ApiOperation(value = "Gets list of all Channel-Listing")
    @RequestMapping(path = "/api/channelListing", method = RequestMethod.GET)
    public List<ChannelListingData> getAll() throws ApiException {
        return channelListingDto.getAll();
    }
}
