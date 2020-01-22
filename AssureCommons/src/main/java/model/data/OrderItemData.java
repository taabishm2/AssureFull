package model.data;

import lombok.Getter;
import lombok.Setter;
import model.form.OrderItemForm;

@Getter
@Setter
public class OrderItemData extends OrderItemForm {
    private Long id;

    private Long allocatedQuantity;

    private Long fulfilledQuantity;

    private String channelSkuId;
}
