package model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Setter
public class OrderItemValidationForm {
    @NotNull
    @Size(min=1, max=255)
    private String channelSkuId;

    @NotNull
    private Long clientId;

    @NotNull
    private Long customerId;

    @NotNull
    private Long channelId;

    @NotNull
    @Size(min=1, max=255)
    private String channelOrderId;

    @NotNull
    @Positive
    private Long orderedQuantity;
}
