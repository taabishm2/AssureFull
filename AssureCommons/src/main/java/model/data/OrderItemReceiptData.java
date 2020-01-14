package model.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemReceiptData {
    Long orderItemId;
    String clientSkuId;
    String channelSkuId;
    Long quantity;
    Double mrp;
    Long total;
}