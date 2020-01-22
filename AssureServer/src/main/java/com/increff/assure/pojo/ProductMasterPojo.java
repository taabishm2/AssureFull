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
    @TableGenerator(name = "Address_pGen", table = "ID_pGEN", pkColumnName = "GEN_pNAME", valueColumnName = "GEN_pVAL", pkColumnValue = "Addr_pGen", initialValue = 10000, allocationSize = 100)
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "Address_pGen")
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
