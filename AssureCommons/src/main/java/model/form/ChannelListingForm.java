package model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ChannelListingForm {
    @NotNull
    private Long channelId;

    @NotNull
    private Long globalSkuId;

    @Size(min=1)
    private String channelSkuId;
}
