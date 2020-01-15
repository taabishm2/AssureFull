package model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ChannelListingForm {
    @Size(min=1, max=255)
    private String clientSkuId;

    @Size(min=1, max=255)
    private String channelSkuId;
}
