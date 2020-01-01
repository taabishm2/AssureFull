package com.increff.assure.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Table(
        uniqueConstraints = {@UniqueConstraint(columnNames = {"binId", "globalSkuId"})}
)
public class BinSkuPojo extends BaseEntity implements Comparable<BinSkuPojo> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long binId;

    @NotNull
    private Long globalSkuId;

    @NotNull
    private Long availableQuantity;

    @Override
    public int compareTo(BinSkuPojo binSkuPojo) {
        if (getAvailableQuantity() == null || binSkuPojo.getAvailableQuantity() == null) {
            return 0;
        }
        return -getAvailableQuantity().compareTo(-binSkuPojo.getAvailableQuantity());
    }
}
