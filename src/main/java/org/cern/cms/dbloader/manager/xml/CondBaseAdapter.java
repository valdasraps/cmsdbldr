package org.cern.cms.dbloader.manager.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.cern.cms.dbloader.model.condition.CondBase;
import org.w3c.dom.Element;

public class CondBaseAdapter extends XmlAdapter<Object, CondBase> {

	private final XmlAdapter<Object, CondBase> adapter;
	
	public CondBaseAdapter() {
		this.adapter = new NullAdapter<>();
	}
	
	public CondBaseAdapter(JAXBContext jaxb, Class<? extends CondBase> cbClass) {
		this.adapter = new ClassAdapter<>(jaxb, cbClass, "DATA");
	}

	@Override
	public CondBase unmarshal(Object v) throws Exception {
		return adapter.unmarshal(v);
	}

	@Override
	public Object marshal(CondBase v) throws Exception {
		return adapter.marshal(v);
	}

	
	
}
