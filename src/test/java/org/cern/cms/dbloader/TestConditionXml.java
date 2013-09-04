package org.cern.cms.dbloader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.cern.cms.dbloader.model.condition.ext.HfPmtIvGain;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TestConditionXml {

	public static void main(String[] args) {
		try {
			
			JAXBContext jaxb = JAXBContext.newInstance(HfPmtIvGain.class);
			Unmarshaller ums = jaxb.createUnmarshaller();
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse("example/HF_PMT_Batch1-8_Gain.xml");
			
			NodeList nodes = doc.getElementsByTagName("DATA");
			for (int i = 0; i < nodes.getLength(); i++) {
				Node n = nodes.item(i);
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element el = (Element) n;
					JAXBElement<HfPmtIvGain> jel = ums.unmarshal(el, HfPmtIvGain.class);
					HfPmtIvGain d = jel.getValue();
					System.out.println(d);
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
	}

}
