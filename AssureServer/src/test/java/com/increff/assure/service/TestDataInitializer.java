package com.increff.assure.service;

import com.increff.assure.dao.ConsumerDao;
import com.increff.assure.dao.ProductMasterDao;
import com.increff.assure.pojo.ConsumerPojo;
import com.increff.assure.pojo.ProductMasterPojo;
import lombok.Getter;
import lombok.Setter;
import model.ConsumerType;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
public class TestDataInitializer {

    @Autowired
    private static ConsumerDao consumerDao;
    @Autowired
    private static ProductMasterDao productMasterDao;

    public ConsumerPojo customerTabish, clientPuma;
    public ProductMasterPojo productPuma;

    public void init(){
        customerTabish = new ConsumerPojo();
        customerTabish.setName("Tabish");
        customerTabish.setType(ConsumerType.CUSTOMER);

        clientPuma = new ConsumerPojo();
        clientPuma.setName("Puma");
        clientPuma.setType(ConsumerType.CLIENT);

        productPuma = new ProductMasterPojo();
        productPuma.setName("PumaX");
        productPuma.setClientId(clientPuma.getId());
        productPuma.setClientSkuId("PUMASKU01");
        productPuma.setMrp(1200D);
        productPuma.setBrandId("PumaX Brand ID");
        productPuma.setDescription("PumaX Description");
    }

    public void populate(){
        //Consumers
        consumerDao.insert(customerTabish);
        consumerDao.insert(clientPuma);

        //Products
        productMasterDao.insert(productPuma);
    }
}
