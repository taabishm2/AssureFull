package com.increff.assure.dao;

import com.increff.assure.pojo.BinSkuPojo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class BinSkuDao extends AbstractDao<BinSkuPojo> {
    @PersistenceContext
    private EntityManager entityManager;

    BinSkuDao() {
        super(BinSkuPojo.class);
    }

    @Transactional(readOnly = true)
    public BinSkuPojo selectByBinIdAndGlobalSku(Long binId, Long globalSkuId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BinSkuPojo> q = cb.createQuery(BinSkuPojo.class);
        Root<BinSkuPojo> c = q.from(BinSkuPojo.class);
        q.select(c);
        ParameterExpression<Long> binIdParam = cb.parameter(Long.class);
        ParameterExpression<Long> globalSkuIdParam = cb.parameter(Long.class);
        q.where(
                cb.equal(c.get("binId"), binIdParam),
                cb.equal(c.get("globalSkuId"), globalSkuIdParam)
        );
        TypedQuery<BinSkuPojo> typedQuery = entityManager.createQuery(q);
        typedQuery.setParameter(binIdParam, binId);
        typedQuery.setParameter(globalSkuIdParam, globalSkuId);
        return getSingle(typedQuery);
    }

    @Transactional(readOnly = true)
    public List<BinSkuPojo> selectByGlobalSku(Long globalSkuId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BinSkuPojo> q = cb.createQuery(BinSkuPojo.class);
        Root<BinSkuPojo> c = q.from(BinSkuPojo.class);
        q.select(c);
        ParameterExpression<Long> globalSkuIdParam = cb.parameter(Long.class);
        q.where(
                cb.equal(c.get("globalSkuId"), globalSkuIdParam)
        );
        TypedQuery<BinSkuPojo> typedQuery = entityManager.createQuery(q);
        typedQuery.setParameter(globalSkuIdParam, globalSkuId);
        List<BinSkuPojo> resultList = typedQuery.getResultList();
        if (Objects.isNull(resultList))
            return new ArrayList<>();
        return resultList;
    }

    public List<BinSkuPojo> getSearchByBinAndProduct(Long binId, Long globalSkuId) {
        String queryStr = "SELECT c FROM BinSkuPojo c WHERE (:binId is null or c.binId = :binId) and (:globalSkuId is null or c.globalSkuId = :globalSkuId)";

        TypedQuery<BinSkuPojo> query = entityManager.createQuery(queryStr, BinSkuPojo.class);
        query.setParameter("binId", binId);
        query.setParameter("globalSkuId", globalSkuId);
        return query.getResultList();
    }
}
