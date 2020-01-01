package model.form;

import lombok.Getter;
import lombok.Setter;
import model.ConsumerType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ConsumerForm {
    @NotBlank
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ConsumerType type;
}
