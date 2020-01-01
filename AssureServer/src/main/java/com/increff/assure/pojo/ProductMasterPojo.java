package com.increff.assure.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Table(
        uniqueConstraints = {@UniqueConstraint(columnNames = {"clientId","clientSkuId"})}
)
public class ProductMasterPojo extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String clientSkuId;

    @NotNull
    private Long clientId;

    @NotNull
    private String name;

    @NotNull
    private String brandId;

    @NotNull
    private Double mrp;

    private String description;
}
