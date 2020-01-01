package com.increff.assure.pojo;

import lombok.Getter;
import lombok.Setter;
import model.OrderStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Table(
        uniqueConstraints = {@UniqueConstraint(columnNames = {"channelId", "channelOrderId"})}
)
public class OrderPojo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long clientId;

    @NotNull
    private Long customerId;

    @NotNull
    private Long channelId;

    @NotNull
    private String channelOrderId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
