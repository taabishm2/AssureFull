package com.increff.assure.dao;

import com.increff.assure.pojo.ConsumerPojo;
import model.ConsumerType;
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
public class ConsumerDao extends AbstractDao<ConsumerPojo> {
    ConsumerDao() {
        super(ConsumerPojo.class);
    }

    @Transactional(readOnly = true)
    public ConsumerPojo selectByNameAndType(String name, ConsumerType type) {
        CriteriaBuilder cb = entityManager().getCriteriaBuilder();
        CriteriaQuery<ConsumerPojo> q = cb.createQuery(ConsumerPojo.class);
        Root<ConsumerPojo> c = q.from(ConsumerPojo.class);
        q.select(c);
        ParameterExpression<String> nameParam = cb.parameter(String.class);
        ParameterExpression<ConsumerType> typeParam = cb.parameter(ConsumerType.class);
        q.where(
                cb.equal(c.get("name"), nameParam),
                cb.equal(c.get("type"), typeParam)
        );
        TypedQuery<ConsumerPojo> typedQuery = entityManager().createQuery(q);
        typedQuery.setParameter(nameParam, name);
        typedQuery.setParameter(typeParam, type);
        return getSingle(typedQuery);
    }

    @Transactional(readOnly = true)
    public List<ConsumerPojo> selectAll(ConsumerType type) {
        CriteriaBuilder cb = entityManager().getCriteriaBuilder();
        CriteriaQuery<ConsumerPojo> q = cb.createQuery(ConsumerPojo.class);
        Root<ConsumerPojo> c = q.from(ConsumerPojo.class);
        q.select(c);
        ParameterExpression<ConsumerType> typeParam = cb.parameter(ConsumerType.class);
        q.where(
                cb.equal(c.get("type"), typeParam)
        );
        TypedQuery<ConsumerPojo> typedQuery = entityManager().createQuery(q);
        typedQuery.setParameter(typeParam, type);
        List<ConsumerPojo> resultList = typedQuery.getResultList();
        if (Objects.isNull(resultList))
            return new ArrayList<>();
        return resultList;
    }
}
