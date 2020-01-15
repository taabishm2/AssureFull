package model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class BinSkuForm {
    @NotNull
    @Positive
    private Long binId;

    @NotNull
    @Positive
    private Long globalSkuId;

    @NotNull
    @Positive
    private Long quantity;
}
