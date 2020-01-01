package com.increff.assure.dao;

import com.increff.assure.pojo.InventoryPojo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

@Repository
public class InventoryDao extends AbstractDao<InventoryPojo> {
    @PersistenceContext
    private EntityManager entityManager;

    InventoryDao() {
        super(InventoryPojo.class);
    }

    @Transactional(readOnly = true)
    public InventoryPojo selectByGlobalSku(Long globalSkuId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<InventoryPojo> q = cb.createQuery(InventoryPojo.class);
        Root<InventoryPojo> c = q.from(InventoryPojo.class);
        q.select(c);
        ParameterExpression<Long> globalSkuIdParam = cb.parameter(Long.class);
        q.where(
                cb.equal(c.get("globalSkuId"), globalSkuIdParam)
        );
        TypedQuery<InventoryPojo> typedQuery = entityManager.createQuery(q);
        typedQuery.setParameter(globalSkuIdParam, globalSkuId);
        return getSingle(typedQuery);
    }
}
