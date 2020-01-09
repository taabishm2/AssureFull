package model.data;

import lombok.Getter;
import lombok.Setter;
import model.OrderStatus;
import model.form.OrderForm;

@Getter
@Setter
public class OrderData extends OrderForm {
    private Long id;

    private OrderStatus status;
}
