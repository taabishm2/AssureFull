package com.increff.assure.dao;

import com.increff.assure.pojo.ChannelPojo;
import model.InvoiceType;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

@Repository
public class ChannelDao extends AbstractDao<ChannelPojo> {
    ChannelDao() {
        super(ChannelPojo.class);
    }

    public ChannelPojo selectByNameAndType(String channelName, InvoiceType channelType) {
        CriteriaBuilder cb = entityManager().getCriteriaBuilder();
        CriteriaQuery<ChannelPojo> q = cb.createQuery(ChannelPojo.class);
        Root<ChannelPojo> c = q.from(ChannelPojo.class);
        q.select(c);
        ParameterExpression<String> nameParam = cb.parameter(String.class);
        ParameterExpression<InvoiceType> typeParam = cb.parameter(InvoiceType.class);
        q.where(
                cb.equal(c.get("name"), nameParam),
                cb.equal(c.get("invoiceType"), typeParam)
        );
        TypedQuery<ChannelPojo> typedQuery = entityManager().createQuery(q);
        typedQuery.setParameter(nameParam, channelName);
        typedQuery.setParameter(typeParam, channelType);
        return getSingle(typedQuery);
    }
}