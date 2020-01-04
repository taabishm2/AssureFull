package com.increff.assure.dto;

import com.increff.assure.pojo.ChannelPojo;
import com.increff.assure.service.ApiException;
import com.increff.assure.service.ChannelService;
import com.increff.assure.util.FormValidateUtil;
import com.increff.assure.util.NormalizeUtil;
import model.InvoiceType;
import model.data.ChannelData;
import model.form.ChannelForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.increff.assure.util.ConvertUtil.convert;

@Service
public class ChannelDto {
    @Autowired
    private ChannelService channelService;

    @Transactional(rollbackFor = ApiException.class)
    public void init() {
        ChannelPojo internalChannel = new ChannelPojo();
        internalChannel.setName("INTERNAL");
        internalChannel.setInvoiceType(InvoiceType.SELF);

        try {
            channelService.checkDuplicateChannelName("INTERNAL");
            channelService.add(internalChannel);
        } catch (ApiException ignored) {
        }
    }

    @Transactional(readOnly = true)
    public ChannelData get(Long id) throws ApiException {
        ChannelPojo channelPojo = channelService.getCheckId(id);
        return convert(channelPojo, ChannelData.class);
    }

    @Transactional(rollbackFor = ApiException.class)
    public void add(ChannelForm channelForm) throws ApiException {
        FormValidateUtil.validate(channelForm);
        ChannelPojo channelPojo = convert(channelForm, ChannelPojo.class);
        NormalizeUtil.normalize(channelPojo);

        channelService.add(channelPojo);
    }

    @Transactional(readOnly = true)
    public List<ChannelData> getAll() throws ApiException {
        return convert(channelService.getAll(), ChannelData.class);
    }
}