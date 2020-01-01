package model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ChannelListingForm {
    @NotNull
    private Long channelId;

    @NotNull
    private Long globalSkuId;

    @NotEmpty
    private String channelSkuId;
}
