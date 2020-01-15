package model.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ProductUpdateForm {
    @NotNull
    @Positive
    private Double mrp;

    @Size(min=1, max=255)
    private String name;

    @Size(max=255)
    private String brandId;

    @Size(max=5000)
    private String description;
}
