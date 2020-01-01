package com.increff.assure.dao;

import com.increff.assure.pojo.BinPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class BinDao extends AbstractDao<BinPojo> {
    @PersistenceContext
    private EntityManager entityManager;

    BinDao() {
        super(BinPojo.class);
    }
}
