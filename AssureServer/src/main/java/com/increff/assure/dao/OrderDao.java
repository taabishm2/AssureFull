package com.increff.assure.dao;

import com.increff.assure.pojo.OrderPojo;
import model.data.OrderData;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class OrderDao extends AbstractDao<OrderPojo> {
    @PersistenceContext
    private EntityManager entityManager;

    OrderDao() {
        super(OrderPojo.class);
    }

    @Transactional(readOnly = true)
    public OrderPojo selectByChannelAndChannelOrderId(Long channelId, String channelOrderId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrderPojo> q = cb.createQuery(OrderPojo.class);
        Root<OrderPojo> c = q.from(OrderPojo.class);
        q.select(c);
        ParameterExpression<Long> channelIdParam = cb.parameter(Long.class);
        ParameterExpression<String> channelOrderIdParam = cb.parameter(String.class);
        q.where(
                cb.equal(c.get("channelId"), channelIdParam),
                cb.equal(c.get("channelOrderId"), channelOrderIdParam)
        );
        TypedQuery<OrderPojo> typedQuery = entityManager.createQuery(q);
        typedQuery.setParameter(channelIdParam, channelId);
        typedQuery.setParameter(channelOrderIdParam, channelOrderId);
        return getSingle(typedQuery);
    }

    public List<OrderPojo> selectByChannel(Long channelId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrderPojo> q = cb.createQuery(OrderPojo.class);
        Root<OrderPojo> c = q.from(OrderPojo.class);
        q.select(c);
        ParameterExpression<Long> channelIdParam = cb.parameter(Long.class);
        q.where(
                cb.equal(c.get("channelId"), channelIdParam)
        );
        TypedQuery<OrderPojo> typedQuery = entityManager.createQuery(q);
        typedQuery.setParameter(channelIdParam, channelId);
        return typedQuery.getResultList();
    }
}
