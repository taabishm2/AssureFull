package model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ChannelOrderItemForm {
    @NotEmpty
    private String channelSkuId;

    @NotNull
    private Long orderedQuantity;
}
