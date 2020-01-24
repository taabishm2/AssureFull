package com.increff.assure.util;

import model.data.OrderReceiptData;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;

public class XmlGenerateUtil {

    public static File generate(OrderReceiptData orderItems) {
        try {
            String receiptFileName = orderItems.getOrderId() + ".xml";
            File file = new File("C://Users//Tabish//Documents//Repos//Increff//AssureServer//src//main//resources//output//" + receiptFileName);
            JAXBContext jaxbContext = JAXBContext.newInstance(OrderReceiptData.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(orderItems, file);
            return file;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }
}