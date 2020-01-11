package com.increff.assure.util;

import com.increff.assure.service.ApiException;
import model.form.*;

import java.util.Objects;

public class CheckValid {
    public static void validate(ConsumerForm consumerForm) throws ApiException {
        if(consumerForm.getName().length() < 1)
            throw new ApiException("Name is too Short. Min. length is 1");

        if(consumerForm.getName().length() > 255)
            throw new ApiException("Name is too Long. Max. length is 255");
    }

    public static void validate(ProductMasterForm productForm) throws ApiException {
        if(productForm.getClientSkuId().isEmpty() ||
                Objects.isNull(productForm.getMrp()) ||
                productForm.getName().isEmpty())
            throw new ApiException("Some fields are EMPTY");

        if(productForm.getMrp() <= 0)
            throw new ApiException("MRP must be non-zero and positive.");
    }

    public static void validate(BinSkuForm binSkuForm) throws ApiException {
        if(binSkuForm.getQuantity() < 0L)
            throw new ApiException("Bin Quantity cannot be negative");
    }

    public static void validate(ChannelForm channelForm) throws ApiException {
        if(channelForm.getName().length() < 1)
            throw new ApiException("Name is too Short. Min. length is 1");

        if(channelForm.getName().length() > 255)
            throw new ApiException("Name is too Long. Max. length is 255");
    }

    public static void validate(OrderForm orderForm) throws ApiException {
        if(orderForm.getChannelOrderId().length() < 1)
            throw new ApiException("Name is too Short. Min. length is 2");

        if(orderForm.getChannelOrderId().length() > 255)
            throw new ApiException("Name is too Long. Max. length is 255");
    }

    public static void validate(ChannelListingForm listingForm) throws ApiException {
        if(listingForm.getChannelSkuId().length() < 1)
            throw new ApiException("Channel Sku is too Short. Min. length is 255");
    }
}
