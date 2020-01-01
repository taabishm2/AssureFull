package model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class InventoryForm {
    @NotNull
    private Long globalSkuId;

    @NotNull
    private Long availableQuantity;

    @NotNull
    private Long allocatedQuantity;

    @NotNull
    private Long fulfilledQuantity;
}
