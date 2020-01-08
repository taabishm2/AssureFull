package com.increff.assure.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames={"channelId", "channelSkuId"}),
        @UniqueConstraint(columnNames={"channelId", "globalSkuId"}),
        @UniqueConstraint(columnNames={"clientId", "globalSkuId"})
})
public class ChannelListingPojo extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long channelId;

    @NotNull
    private String channelSkuId;

    @NotNull
    private Long globalSkuId;

    @NotNull
    private Long clientId;
}
