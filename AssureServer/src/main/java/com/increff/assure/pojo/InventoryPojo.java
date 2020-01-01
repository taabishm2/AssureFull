package com.increff.assure.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Table(
        uniqueConstraints = {@UniqueConstraint(columnNames = {"globalSkuId"})}
)
public class InventoryPojo extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long globalSkuId;

    @NotNull
    private Long availableQuantity;

    @NotNull
    private Long allocatedQuantity;

    @NotNull
    private Long fulfilledQuantity;
}
