package com.increff.assure.controller;

import com.increff.assure.dto.ChannelDto;
import com.increff.assure.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import model.data.ChannelData;
import model.form.ChannelForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.List;

@Api
@RestController
public class ChannelController {
    @Autowired
    private ChannelDto channelDto;

    @ApiOperation(value = "Add a Channel")
    @RequestMapping(path = "/api/channel", method = RequestMethod.POST)
    public void add(@RequestBody ChannelForm form) throws ApiException {
        channelDto.add(form);
    }

    @ApiOperation(value = "Get list of all channels")
    @RequestMapping(path = "/api/channel", method = RequestMethod.GET)
    public List<ChannelData> getAll() throws ApiException {
        return channelDto.getAll();
    }
}