package com.increff.assure.dao;

import com.increff.assure.pojo.OrderItemPojo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class OrderItemDao extends AbstractDao<OrderItemPojo> {
    OrderItemDao() {
        super(OrderItemPojo.class);
    }

    @Transactional(readOnly = true)
    public List<OrderItemPojo> selectByOrderId(Long orderId) {
        CriteriaBuilder cb = entityManager().getCriteriaBuilder();
        CriteriaQuery<OrderItemPojo> q = cb.createQuery(OrderItemPojo.class);
        Root<OrderItemPojo> c = q.from(OrderItemPojo.class);
        q.select(c);
        ParameterExpression<Long> orderIdParam = cb.parameter(Long.class);
        q.where(
                cb.equal(c.get("orderId"), orderIdParam)
        );
        TypedQuery<OrderItemPojo> typedQuery = entityManager().createQuery(q);
        typedQuery.setParameter(orderIdParam, orderId);
        List<OrderItemPojo> resultList = typedQuery.getResultList();
        if (Objects.isNull(resultList))
            return new ArrayList<>();
        return resultList;
    }

    @Transactional(readOnly = true)
    public OrderItemPojo selectByOrderIdAndGlobalSku(Long orderId, Long globalSkuId) {
        CriteriaBuilder cb = entityManager().getCriteriaBuilder();
        CriteriaQuery<OrderItemPojo> q = cb.createQuery(OrderItemPojo.class);
        Root<OrderItemPojo> c = q.from(OrderItemPojo.class);
        q.select(c);
        ParameterExpression<Long> orderIdParam = cb.parameter(Long.class);
        ParameterExpression<Long> globalSkuIdParam = cb.parameter(Long.class);
        q.where(
                cb.equal(c.get("orderId"), orderIdParam),
                cb.equal(c.get("globalSkuId"), globalSkuIdParam)
        );
        TypedQuery<OrderItemPojo> typedQuery = entityManager().createQuery(q);
        typedQuery.setParameter(orderIdParam, orderId);
        typedQuery.setParameter(globalSkuIdParam, globalSkuId);
        return getSingle(typedQuery);
    }
}
