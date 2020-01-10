package com.increff.assure.util;

import model.data.OrderReceiptData;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;

public class XmlGenerateUtil {

    public static void generate(OrderReceiptData orderItems) {
        try {
            String receiptFileName = orderItems.getOrderId() + ".xml";
            File file = new File("AssureServer//src//main//resources//output//" + receiptFileName);
            JAXBContext jaxbContext = JAXBContext.newInstance(OrderReceiptData.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(orderItems, file);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

/*    public static void main(String args[]){
        Instant now = Instant.now();
        ZoneId zoneId = ZoneId.of("America/Los_Angeles");
        ZonedDateTime dateAndTimeInLA = ZonedDateTime.ofInstant(now, zoneId);

        List<OrderItemReceiptData> orderItemList = new ArrayList<>();
        OrderItemReceiptData orderItem1 = new OrderItemReceiptData();
        orderItem1.setClientSkuId("CSKU01");
        orderItem1.setMrp(2946D);
        orderItem1.setOrderItemId(863L);
        orderItem1.setQuantity(5L);
        orderItem1.setTotal(28374L);
        orderItemList.add(orderItem1);

        OrderItemReceiptData orderItem2 = new OrderItemReceiptData();
        orderItem2.setClientSkuId("BRKU01");
        orderItem2.setMrp(5246D);
        orderItem2.setOrderItemId(763L);
        orderItem2.setQuantity(2L);
        orderItem2.setTotal(74674L);
        orderItemList.add(orderItem2);

        OrderItemReceiptData orderItem3 = new OrderItemReceiptData();
        orderItem3.setClientSkuId("NSYU32");
        orderItem3.setMrp(2388D);
        orderItem3.setOrderItemId(883L);
        orderItem3.setQuantity(12L);
        orderItem3.setTotal(98271L);
        orderItemList.add(orderItem3);

        OrderReceiptData orderData = new OrderReceiptData();
        orderData.setChannelName("TEST CHANNEL NAME");
        orderData.setChannelOrderId("CHANNEL01XORDER01XID");
        orderData.setClientDetails("Client Details Line");
        orderData.setCustomerDetails("Customer Details Line");
        orderData.setOrderId(54234L);
        orderData.setOrderCreationTime(dateAndTimeInLA.toString());

        orderData.setOrderItems(orderItemList);

        generate(orderData);
    }*/
}