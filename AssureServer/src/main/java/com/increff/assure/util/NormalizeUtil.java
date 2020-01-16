package com.increff.assure.util;

import com.increff.assure.pojo.ChannelPojo;
import com.increff.assure.pojo.ConsumerPojo;
import model.form.ChannelForm;
import model.form.ChannelListingForm;
import model.form.ConsumerForm;
import model.form.ProductMasterForm;

public class NormalizeUtil {
    public static void normalize(ConsumerPojo consumerPojo) {
        consumerPojo.setName(StringUtil.toLowerCase(consumerPojo.getName()));
    }

    public static void normalize(ChannelForm channelForm) {
        channelForm.setName(StringUtil.toLowerCase(channelForm.getName()));
    }

    public static void normalize(ConsumerForm consumerForm) {
        consumerForm.setName(StringUtil.toLowerCase(consumerForm.getName()));
    }

    public static void normalize(ProductMasterForm productForm) {
        productForm.setName(StringUtil.trimSpaces(productForm.getName()));
    }

    public static void normalize(ChannelListingForm listingForm) {
        listingForm.setChannelSkuId(StringUtil.trimSpaces(listingForm.getChannelSkuId()));
        listingForm.setClientSkuId(StringUtil.trimSpaces(listingForm.getClientSkuId()));
    }
}