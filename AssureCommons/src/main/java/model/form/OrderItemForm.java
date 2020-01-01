package model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class OrderItemForm {
    @NotNull
    private Long globalSkuId;

    @NotNull
    private Long orderedQuantity;
}
