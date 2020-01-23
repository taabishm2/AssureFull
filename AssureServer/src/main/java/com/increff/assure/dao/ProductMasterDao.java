package com.increff.assure.dao;

import com.increff.assure.pojo.ProductMasterPojo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class ProductMasterDao extends AbstractDao<ProductMasterPojo> {
    ProductMasterDao() {
        super(ProductMasterPojo.class);
    }

    @Transactional(readOnly = true)
    public ProductMasterPojo selectByClientIdAndClientSku(Long clientId, String clientSkuId) {
        CriteriaBuilder cb = entityManager().getCriteriaBuilder();
        CriteriaQuery<ProductMasterPojo> q = cb.createQuery(ProductMasterPojo.class);
        Root<ProductMasterPojo> c = q.from(ProductMasterPojo.class);
        q.select(c);
        ParameterExpression<Long> clientIdParam = cb.parameter(Long.class);
        ParameterExpression<String> clientSkuIdParam = cb.parameter(String.class);
        q.where(
                cb.equal(c.get("clientId"), clientIdParam),
                cb.equal(c.get("clientSkuId"), clientSkuIdParam)
        );
        TypedQuery<ProductMasterPojo> typedQuery = entityManager().createQuery(q);
        typedQuery.setParameter(clientIdParam, clientId);
        typedQuery.setParameter(clientSkuIdParam, clientSkuId);
        return getSingle(typedQuery);
    }

    public List<ProductMasterPojo> selectByClientId(Long clientId) {
        CriteriaBuilder cb = entityManager().getCriteriaBuilder();
        CriteriaQuery<ProductMasterPojo> q = cb.createQuery(ProductMasterPojo.class);
        Root<ProductMasterPojo> c = q.from(ProductMasterPojo.class);
        q.select(c);
        ParameterExpression<Long> clientIdParam = cb.parameter(Long.class);
        q.where(
                cb.equal(c.get("clientId"), clientIdParam)
        );
        TypedQuery<ProductMasterPojo> typedQuery = entityManager().createQuery(q);
        typedQuery.setParameter(clientIdParam, clientId);
        return typedQuery.getResultList();
    }

    public List<ProductMasterPojo> selectByClientSku(String clientSkuId) {
        CriteriaBuilder cb = entityManager().getCriteriaBuilder();
        CriteriaQuery<ProductMasterPojo> q = cb.createQuery(ProductMasterPojo.class);
        Root<ProductMasterPojo> c = q.from(ProductMasterPojo.class);
        q.select(c);
        ParameterExpression<String> clientSkuIdParam = cb.parameter(String.class);
        q.where(
                cb.equal(c.get("clientSkuId"), clientSkuIdParam)
        );
        TypedQuery<ProductMasterPojo> typedQuery = entityManager().createQuery(q);
        typedQuery.setParameter(clientSkuIdParam, clientSkuId);
        return typedQuery.getResultList();
    }
}
