package model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
public class BinSkuForm {
    @NotNull
    @PositiveOrZero
    private Long binId;

    @NotNull
    @PositiveOrZero
    private Long globalSkuId;

    @NotNull
    @Positive
    private Long quantity;
}
