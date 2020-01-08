package com.increff.assure.service;

import com.increff.assure.dao.ChannelDao;
import com.increff.assure.pojo.ChannelPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ChannelService extends AbstractService {
    @Autowired
    private ChannelDao channelDao;

    @Transactional(rollbackFor = ApiException.class)
    public void add(ChannelPojo channelPojo) throws ApiException {
        checkDuplicateChannelName(channelPojo.getName());

        channelDao.insert(channelPojo);
    }

    public void checkDuplicateChannelName(String channelName) throws ApiException {
        ChannelPojo duplicateChannel = channelDao.selectByName(channelName);
        checkNull(duplicateChannel, "Channel (NAME:" + channelName + ") already exists.");
    }

    public List<ChannelPojo> getAll() {
        return channelDao.selectAll();
    }

    public ChannelPojo getCheckId(Long id) throws ApiException {
        ChannelPojo channelPojo = channelDao.select(id);
        checkNotNull(channelPojo, "Channel (ID:" + id + ") does not exist");
        return channelPojo;
    }
}
