package com.increff.assure.dao;

import com.increff.assure.pojo.ChannelPojo;
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
public class ChannelDao extends AbstractDao<ChannelPojo> {
    @PersistenceContext
    private EntityManager entityManager;

    ChannelDao() {
        super(ChannelPojo.class);
    }

    @Transactional(readOnly = true)
    public ChannelPojo selectByName(String name) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ChannelPojo> q = cb.createQuery(ChannelPojo.class);
        Root<ChannelPojo> c = q.from(ChannelPojo.class);
        q.select(c);
        ParameterExpression<String> nameParam = cb.parameter(String.class);
        q.where(
                cb.equal(c.get("name"), nameParam)
        );
        TypedQuery<ChannelPojo> typedQuery = entityManager.createQuery(q);
        typedQuery.setParameter(nameParam, name);
        return getSingle(typedQuery);
    }
}