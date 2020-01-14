package com.increff.assure.util;

import com.increff.assure.model.data.ChannelOrderReceiptData;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;

public class XmlGenerateUtil {

    public static void generate(ChannelOrderReceiptData orderItems) {
        try {
            String receiptFileName = orderItems.getOrderId() + ".xml";
            File file = new File("C://Users//Tabish//Documents//Repos//Increff//ChannelApp//src//main//resources//output//" + receiptFileName);
            JAXBContext jaxbContext = JAXBContext.newInstance(ChannelOrderReceiptData.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(orderItems, file);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChannelOrderReceiptData data = new ChannelOrderReceiptData();
        data.setChannelName("Channel name");
        data.setChannelOrderId("Channel Order ID");
        data.setClientDetails("Client");
        data.setCustomerDetails("Customer");
        data.setOrderCreationTime("Created At");
        data.setOrderId(3243L);
        data.setOrderItems(null);
        generate(data);
    }
}