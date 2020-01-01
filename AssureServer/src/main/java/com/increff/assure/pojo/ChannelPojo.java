package com.increff.assure.pojo;

import lombok.Getter;
import lombok.Setter;
import model.InvoiceType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Table(
        uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "invoiceType"})}
)
public class ChannelPojo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private InvoiceType invoiceType;
}
