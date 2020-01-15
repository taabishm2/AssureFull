package model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
public class InventoryForm {
    @NotNull
    private Long globalSkuId;

    @NotNull
    @PositiveOrZero
    private Long availableQuantity;

    @NotNull
    @PositiveOrZero
    private Long allocatedQuantity;

    @NotNull
    @PositiveOrZero
    private Long fulfilledQuantity;
}
