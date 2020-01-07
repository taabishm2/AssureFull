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
    private Long quantity;

    //TODO: Move to DTO
    @Override
    public int compareTo(BinSkuPojo binSkuPojo) {
        if (getQuantity() == null || binSkuPojo.getQuantity() == null) {
            return 0;
        }
        return -getQuantity().compareTo(-binSkuPojo.getQuantity());
    }
}
