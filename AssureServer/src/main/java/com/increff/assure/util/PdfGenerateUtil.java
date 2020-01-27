package com.increff.assure.util;

import com.increff.assure.service.ApiException;
import org.apache.fop.apps.*;
import org.w3c.dom.Document;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

public class PdfGenerateUtil {

    public static final String RESOURCES_DIR;
    public static final String OUTPUT_DIR;

    static {
        RESOURCES_DIR = "C://Users//Tabish//Documents//Repos//Increff//AssureServer//src//main//resources//";
        OUTPUT_DIR = "C://Users//Tabish//Documents//Repos//Increff//AssureServer//src//main//resources//output//";
    }

    public static byte[] generate(Document file, Long fileId) throws ApiException {
        PdfGenerateUtil fOPPdfDemo = new PdfGenerateUtil();
        try {
            return fOPPdfDemo.convertToPDF(file, fileId);
        } catch (Exception e) {
            throw new ApiException("Couldn't generate Invoice PDF:" + e.getMessage());
        }
    }

    public byte[] convertToPDF(Document doc, Long fileId) throws IOException, FOPException, TransformerException {
        File xsltFile = new File(RESOURCES_DIR + "//template.xsl");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Source xmlSource = new DOMSource(doc);
        Result outputTarget = new StreamResult(outputStream);
        TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
        InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
        StreamSource xmlStreamSource = new StreamSource(is);
        FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

        ByteArrayOutputStream out;
        out = new ByteArrayOutputStream();

        try {
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(xsltFile));
            Result res = new SAXResult(fop.getDefaultHandler());
            transformer.transform(xmlStreamSource, res);
            byte[] arr = out.toByteArray();
            return arr;
        } finally {
            out.close();
        }
    }

/*    public static void main(String args[]) throws ApiException {
        generate(54234L);
    }*/
}
