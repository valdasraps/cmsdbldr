package org.cern.cms.dbloader.tests;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.cern.cms.dbloader.TestBase;

import org.cern.cms.dbloader.tests.model.HfPmtIvGain;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConditionXmlTest extends TestBase {

    @Test
    public void load() throws Exception {

        JAXBContext jaxb = JAXBContext.newInstance(HfPmtIvGain.class);
        Unmarshaller ums = jaxb.createUnmarshaller();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse("src/test/serial/HF_PMT_Batch1-8_Gain.serial");

        NodeList nodes = doc.getElementsByTagName("DATA");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) n;
                JAXBElement<HfPmtIvGain> jel = ums.unmarshal(el, HfPmtIvGain.class);
                HfPmtIvGain d = jel.getValue();
            }
        }

    }

}
