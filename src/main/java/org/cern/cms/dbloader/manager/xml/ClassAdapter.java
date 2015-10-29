package org.cern.cms.dbloader.manager.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ClassAdapter <T> extends XmlAdapter<Object, T> {

	private final JAXBContext jaxb;
	private final Class<? extends T> cbClass;
	private final String wrapperElementName;
	private DocumentBuilderFactory dbf;
	private DocumentBuilder db;

	public ClassAdapter(JAXBContext jaxb, Class<? extends T> cbClass, String wrapperElementName) {
		this.jaxb = jaxb;
		this.cbClass = cbClass;
		this.wrapperElementName = wrapperElementName;
	}
	
	private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		if (db == null) {
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
		}
		return db;
	}
	
	@Override
	public Object marshal(T o) throws Exception {
		if (o == null) {
			return null;
		}
		QName qname = new QName(wrapperElementName);
		JAXBElement<?> jaxbEl = new JAXBElement(qname, o.getClass(), o);
		Document doc = getDocumentBuilder().newDocument();
		Marshaller ms = jaxb.createMarshaller();
		ms.marshal(jaxbEl, doc);
		Element element = doc.getDocumentElement();
		return element;
	}

	@Override
	public T unmarshal(Object el) throws Exception {
		if (el == null) {
			return null;
		}
		DOMSource source = new DOMSource((Element) el);
		Unmarshaller ums = jaxb.createUnmarshaller();
		ums.setEventHandler(new EventHandler());
		JAXBElement jaxbElement = ums.unmarshal(source, cbClass);
		return (T) jaxbElement.getValue();
	}

}
