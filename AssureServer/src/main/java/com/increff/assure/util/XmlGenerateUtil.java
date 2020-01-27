package com.increff.assure.util;

import com.increff.assure.service.ApiException;
import model.data.OrderReceiptData;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XmlGenerateUtil {

    public static Document generate(OrderReceiptData orderItems) throws ApiException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(OrderReceiptData.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();
            jaxbMarshaller.marshal(orderItems, doc);
            return doc;
        } catch (JAXBException | ParserConfigurationException e) {
            e.printStackTrace();
            throw new ApiException("Error generating Invoice XML files");
        }
    }
}