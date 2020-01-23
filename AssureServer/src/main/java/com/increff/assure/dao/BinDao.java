package com.increff.assure.dao;

import com.increff.assure.pojo.BinPojo;
import org.springframework.stereotype.Repository;

@Repository
public class BinDao extends AbstractDao<BinPojo> {
    BinDao() {
        super(BinPojo.class);
    }
}
