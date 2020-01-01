package com.increff.assure.util;

import org.apache.fop.apps.*;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class PdfGenerateUtil {

    public static final String RESOURCES_DIR;
    public static final String OUTPUT_DIR;

    static {
        RESOURCES_DIR = "src//main//resources//";
        OUTPUT_DIR = "src//main//resources//output//";
    }

    public static void generate(Long orderId) {
        PdfGenerateUtil fOPPdfDemo = new PdfGenerateUtil();
        try {
            fOPPdfDemo.convertToPDF(orderId);
        } catch (FOPException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void convertToPDF(Long orderId) throws IOException, FOPException, TransformerException {
        File xsltFile = new File(RESOURCES_DIR + "//template.xsl");
        String xmlSourceName = "//" + orderId + ".xml";
        StreamSource xmlSource = new StreamSource(new File(OUTPUT_DIR + xmlSourceName));
        FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

        OutputStream out;
        out = new java.io.FileOutputStream(OUTPUT_DIR + "//" + orderId + ".pdf");

        try {
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(xsltFile));
            Result res = new SAXResult(fop.getDefaultHandler());

            transformer.transform(xmlSource, res);
        } finally {
            out.close();
        }
    }
}
