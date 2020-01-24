package com.increff.assure.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Table(
        uniqueConstraints = {@UniqueConstraint(columnNames = {"orderId","globalSkuId"})}
)
public class OrderItemPojo extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long orderId;

    @NotNull
    private Long globalSkuId;

    @NotNull
    private Long orderedQuantity;

    @NotNull
    private Long allocatedQuantity = 0L;

    @NotNull
    private Long fulfilledQuantity = 0L;
}
