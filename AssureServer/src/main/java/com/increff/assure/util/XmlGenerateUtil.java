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
            File file = new File("src//main//resources//output//" + receiptFileName);
            JAXBContext jaxbContext = JAXBContext.newInstance(OrderReceiptData.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(orderItems, file);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}