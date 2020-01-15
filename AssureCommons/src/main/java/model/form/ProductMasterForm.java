package model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ProductMasterForm {
    @Size(min=1, max=255)
    private String name;

    @Size(min=1, max=255)
    private String clientSkuId;

    @NotNull
    @Positive
    private Double mrp;

    @Size(max=255)
    private String brandId;

    @Size(max=5000)
    private String description;
}
