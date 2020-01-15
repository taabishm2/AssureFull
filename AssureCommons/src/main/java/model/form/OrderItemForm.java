package model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Setter
public class OrderItemForm {
    @Size(min=1, max=255)
    private String clientSkuId;

    @NotNull
    @Positive
    private Long orderedQuantity;
}
