package com.increff.assure.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames={"channelId", "channelOrderId"}),
        @UniqueConstraint(columnNames={"channelId", "globalSkuId"})
})
public class ChannelListingPojo extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long channelId;

    @NotNull
    private String channelOrderId;

    @NotNull
    private Long globalSkuId;
}
