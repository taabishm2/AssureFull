package com.increff.assure.dto;

import com.increff.assure.pojo.ConsumerPojo;
import com.increff.assure.service.ApiException;
import com.increff.assure.service.ConsumerService;
import com.increff.assure.util.NormalizeUtil;
import model.ConsumerType;
import model.data.ConsumerData;
import model.form.ConsumerForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.increff.assure.util.ConvertUtil.convert;

@Service
public class ConsumerDto extends AbstractDto {
    @Autowired
    private ConsumerService consumerService;

    @Transactional(rollbackFor = ApiException.class)
    public void add(ConsumerForm consumerForm) throws ApiException {
        NormalizeUtil.normalize(consumerForm);
        checkValid(consumerForm);

        consumerService.add(convert(consumerForm, ConsumerPojo.class));
    }

    @Transactional(readOnly = true)
    public ConsumerData get(Long id) throws ApiException {
        return convert(consumerService.getCheckId(id), ConsumerData.class);
    }

    @Transactional(readOnly = true)
    public List<ConsumerData> getAll() throws ApiException {
        return convert(consumerService.getAll(), ConsumerData.class);
    }

    @Transactional(readOnly = true)
    public List<ConsumerData> getAllClients() throws ApiException {
        return convert(consumerService.getAll(ConsumerType.CLIENT), ConsumerData.class);
    }

    @Transactional(readOnly = true)
    public List<ConsumerData> getAllCustomers() throws ApiException {
        return convert(consumerService.getAll(ConsumerType.CUSTOMER), ConsumerData.class);
    }
}