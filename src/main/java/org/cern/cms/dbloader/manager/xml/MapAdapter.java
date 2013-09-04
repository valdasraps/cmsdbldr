package org.cern.cms.dbloader.manager.xml;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MapAdapter extends XmlAdapter<Element, Entry<String, String>> {

    @Override
    public Element marshal(Entry<String, String> e) throws Exception {
    	if (e == null) {
    		return null;
    	}
        
    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        
        Element el = doc.createElement(e.getKey());
        el.setTextContent(e.getValue());
        
        return el;
    }

    @Override
    public Entry<String, String> unmarshal(Element el) throws Exception {
    	if (el == null) {
    		return null;
    	}

    	Map<String, String> m = new HashMap<>();
    	m.put(el.getNodeName(), el.getTextContent());

		return m.entrySet().iterator().next();
    }

}