package com.increff.assure.dao;

import com.increff.assure.pojo.ChannelListingPojo;
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
public class ChannelListingDao extends AbstractDao<ChannelListingPojo> {
    @PersistenceContext
    private EntityManager entityManager;

    ChannelListingDao() {
        super(ChannelListingPojo.class);
    }

    @Transactional(readOnly = true)
    public ChannelListingPojo selectByChannelAndGlobalSku(Long channelId, Long globalSkuId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ChannelListingPojo> q = cb.createQuery(ChannelListingPojo.class);
        Root<ChannelListingPojo> c = q.from(ChannelListingPojo.class);
        q.select(c);
        ParameterExpression<Long> channelIdParam = cb.parameter(Long.class);
        ParameterExpression<Long> globalSkuIdParam = cb.parameter(Long.class);
        q.where(
                cb.equal(c.get("channelId"), channelIdParam),
                cb.equal(c.get("globalSkuId"), globalSkuIdParam)
        );
        TypedQuery<ChannelListingPojo> typedQuery = entityManager.createQuery(q);
        typedQuery.setParameter(channelIdParam, channelId);
        typedQuery.setParameter(globalSkuIdParam, globalSkuId);
        return getSingle(typedQuery);
    }

    @Transactional(readOnly = true)
    public ChannelListingPojo selectByChannelAndChannelSku(Long channelId, String channelOrderId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ChannelListingPojo> q = cb.createQuery(ChannelListingPojo.class);
        Root<ChannelListingPojo> c = q.from(ChannelListingPojo.class);
        q.select(c);
        ParameterExpression<Long> channelIdParam = cb.parameter(Long.class);
        ParameterExpression<String> channelOrderIdParam = cb.parameter(String.class);
        q.where(
                cb.equal(c.get("channelId"), channelIdParam),
                cb.equal(c.get("channelOrderId"), channelOrderIdParam)
        );
        TypedQuery<ChannelListingPojo> typedQuery = entityManager.createQuery(q);
        typedQuery.setParameter(channelIdParam, channelId);
        typedQuery.setParameter(channelOrderIdParam, channelOrderId);
        return getSingle(typedQuery);
    }
}
