package model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ChannelListingSearchForm {
    @NotNull
    private Long clientId;

    @NotNull
    private Long channelId;

    @NotNull
    @Size(min=1, max=255)
    private String clientSkuId;

    @NotNull
    @Size(min=1, max=255)
    private String channelSkuId;
}
