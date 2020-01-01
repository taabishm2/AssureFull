package model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ProductUpdateForm {
    @NotNull
    private Double mrp;

    @NotEmpty
    private String name;

    @NotEmpty
    private String brandId;

    @NotEmpty
    private String description;
}
