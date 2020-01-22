package com.increff.assure.service;

import com.increff.assure.dao.ConsumerDao;
import com.increff.assure.pojo.ConsumerPojo;
import model.ConsumerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConsumerService extends AbstractService {
    @Autowired
    private ConsumerDao consumerDao;

    @Transactional(rollbackFor = ApiException.class)
    public void add(ConsumerPojo consumerPojo) throws ApiException {
        checkIfConsumerExists(consumerPojo);
        consumerDao.insert(consumerPojo);
    }

    private void checkIfConsumerExists(ConsumerPojo consumerPojo) throws ApiException {
        checkNull(consumerDao.selectByNameAndType(consumerPojo.getName(), consumerPojo.getType()), consumerPojo.getName() + " already exists.");
    }

    public ConsumerPojo getCheckId(Long id) throws ApiException {
        ConsumerPojo consumer = consumerDao.select(id);
        checkNotNull(consumer, "Consumer (ID:" + id + ") does not exist.");
        return consumer;
    }

    public void getCheckClient(Long id) throws ApiException {
        if (!getCheckId(id).getType().equals(ConsumerType.CLIENT))
            throw new ApiException("Invalid Client");
    }

    public void getCheckCustomer(Long id) throws ApiException {
        if (!getCheckId(id).getType().equals(ConsumerType.CUSTOMER))
            throw new ApiException("Invalid Customer");
    }

    public List<ConsumerPojo> getAll() {
        return consumerDao.selectAll();
    }

    public List<ConsumerPojo> getAll(ConsumerType type) {
        return consumerDao.selectAll(type);
    }
}