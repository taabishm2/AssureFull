package model.form;

import lombok.Getter;
import lombok.Setter;
import model.ConsumerType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ConsumerForm {
    @NotNull
    @Size(min=1, max=255)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ConsumerType type;
}
