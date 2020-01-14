package model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class OrderItemForm {
    @NotEmpty
    private String clientSkuId;

    @NotNull
    private Long orderedQuantity;
}
