package model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class OrderItemValidationForm {
    @NotNull
    private Long globalSkuId;

    @NotNull
    private Long clientId;

    @NotNull
    private Long customerId;

    @NotNull
    private Long channelId;

    @NotEmpty
    private String channelOrderId;

    @NotNull
    private Long orderedQuantity;
}
