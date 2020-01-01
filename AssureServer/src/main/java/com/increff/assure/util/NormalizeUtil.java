package com.increff.assure.util;

import com.increff.assure.pojo.ChannelPojo;
import com.increff.assure.pojo.ConsumerPojo;
import model.form.ConsumerForm;

public class NormalizeUtil {
    public static void normalize(ConsumerPojo consumerPojo) {
        consumerPojo.setName(StringUtil.toUpperCase(consumerPojo.getName()));
    }

    public static void normalize(ChannelPojo channelPojo) {
        channelPojo.setName(StringUtil.toUpperCase(channelPojo.getName()));
    }

    public static void normalize(ConsumerForm consumerForm) {
        consumerForm.setName(StringUtil.toUpperCase(consumerForm.getName()));
    }
}