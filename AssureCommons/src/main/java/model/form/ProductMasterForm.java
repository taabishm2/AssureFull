package model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ProductMasterForm {
    @NotBlank(message = "[NAME] cannot be Blank.")
    private String name;

    @NotBlank(message = "[ClientSKU] cannot be Blank.")
    private String clientSkuId;

    @NotNull
    private Double mrp;

    private String brandId;

    private String description;
}
