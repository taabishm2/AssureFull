package model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class BinSkuForm {
    @NotNull
    private Long binId;

    @NotNull
    private Long globalSkuId;

    @NotNull
    private Long availableQuantity;
}
