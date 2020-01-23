package com.increff.assure.dao;

import com.increff.assure.pojo.ChannelListingPojo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class ChannelListingDao extends AbstractDao<ChannelListingPojo> {
    ChannelListingDao() {
        super(ChannelListingPojo.class);
    }

    @Transactional(readOnly = true)
    public ChannelListingPojo selectByChannelAndGlobalSku(Long channelId, Long globalSkuId) {
        CriteriaBuilder cb = entityManager().getCriteriaBuilder();
        CriteriaQuery<ChannelListingPojo> q = cb.createQuery(ChannelListingPojo.class);
        Root<ChannelListingPojo> c = q.from(ChannelListingPojo.class);
        q.select(c);
        ParameterExpression<Long> channelIdParam = cb.parameter(Long.class);
        ParameterExpression<Long> globalSkuIdParam = cb.parameter(Long.class);
        q.where(
                cb.equal(c.get("channelId"), channelIdParam),
                cb.equal(c.get("globalSkuId"), globalSkuIdParam)
        );
        TypedQuery<ChannelListingPojo> typedQuery = entityManager().createQuery(q);
        typedQuery.setParameter(channelIdParam, channelId);
        typedQuery.setParameter(globalSkuIdParam, globalSkuId);
        return getSingle(typedQuery);
    }

    @Transactional(readOnly = true)
    public ChannelListingPojo selectByChannelAndChannelSku(Long channelId, String channelSkuId) {
        CriteriaBuilder cb = entityManager().getCriteriaBuilder();
        CriteriaQuery<ChannelListingPojo> q = cb.createQuery(ChannelListingPojo.class);
        Root<ChannelListingPojo> c = q.from(ChannelListingPojo.class);
        q.select(c);
        ParameterExpression<Long> channelIdParam = cb.parameter(Long.class);
        ParameterExpression<String> channelSkuIdParam = cb.parameter(String.class);
        q.where(
                cb.equal(c.get("channelId"), channelIdParam),
                cb.equal(c.get("channelSkuId"), channelSkuIdParam)
        );
        TypedQuery<ChannelListingPojo> typedQuery = entityManager().createQuery(q);
        typedQuery.setParameter(channelIdParam, channelId);
        typedQuery.setParameter(channelSkuIdParam, channelSkuId);
        return getSingle(typedQuery);
    }

    public ChannelListingPojo selectByChannelIdChannelSkuAndClient(Long channelId, String channelSkuId, Long clientId) {
        CriteriaBuilder cb = entityManager().getCriteriaBuilder();
        CriteriaQuery<ChannelListingPojo> q = cb.createQuery(ChannelListingPojo.class);
        Root<ChannelListingPojo> c = q.from(ChannelListingPojo.class);
        q.select(c);
        ParameterExpression<Long> clientIdParam = cb.parameter(Long.class);
        ParameterExpression<Long> channelIdParam = cb.parameter(Long.class);
        ParameterExpression<String> channelSkuIdParam = cb.parameter(String.class);
        q.where(
                cb.equal(c.get("channelId"), channelIdParam),
                cb.equal(c.get("clientId"), clientIdParam),
                cb.equal(c.get("channelSkuId"), channelSkuIdParam)
        );
        TypedQuery<ChannelListingPojo> typedQuery = entityManager().createQuery(q);
        typedQuery.setParameter(channelIdParam, channelId);
        typedQuery.setParameter(clientIdParam, clientId);
        typedQuery.setParameter(channelSkuIdParam, channelSkuId);
        return getSingle(typedQuery);
    }

    public List<ChannelListingPojo> getSearch(Long channelId, Long clientId, String channelSkuId, Long globalSkuId) {
        String queryStr = "SELECT c FROM ChannelListingPojo c WHERE (:clientId is null or c.clientId = :clientId) and " +
                "(:channelSkuId is '' or c.channelSkuId = :channelSkuId) and " +
                "(:globalSkuId is null or c.globalSkuId = :globalSkuId) and " +
                "(:channelId is null or c.channelId = :channelId)";

        TypedQuery<ChannelListingPojo> query = entityManager().createQuery(queryStr, ChannelListingPojo.class);
        query.setParameter("clientId", clientId);
        query.setParameter("channelId", channelId);
        query.setParameter("globalSkuId", globalSkuId);
        query.setParameter("channelSkuId", channelSkuId);
        return query.getResultList();
    }
}
