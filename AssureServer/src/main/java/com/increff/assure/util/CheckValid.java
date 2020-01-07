package com.increff.assure.util;

import com.increff.assure.service.ApiException;
import model.form.*;

public class CheckValid {
    public static void validate(ConsumerForm consumerForm) throws ApiException {
        if(consumerForm.getName().length() < 3)
            throw new ApiException("Name is too Short. Min. length is 3");

        if(consumerForm.getName().length() > 20)
            throw new ApiException("Name is too Long. Max. length is 20");
    }

    public static void validate(ProductMasterForm productForm) throws ApiException {
        if(productForm.getMrp() <= 0)
            throw new ApiException("MRP must be non-zero and positive.");
    }

    public static void validate(BinSkuForm binSkuForm) throws ApiException {
        if(binSkuForm.getQuantity() < 0L)
            throw new ApiException("Bin Quantity cannot be negative");
    }

    public static void validate(ChannelForm channelForm) throws ApiException {
        if(channelForm.getName().length() < 3)
            throw new ApiException("Name is too Short. Min. length is 3");

        if(channelForm.getName().length() > 20)
            throw new ApiException("Name is too Long. Max. length is 20");
    }

    public static void validate(OrderForm orderForm) throws ApiException {
        if(orderForm.getChannelOrderId().length() < 3)
            throw new ApiException("Name is too Short. Min. length is 3");

        if(orderForm.getChannelOrderId().length() > 50)
            throw new ApiException("Name is too Long. Max. length is 20");
    }
}
