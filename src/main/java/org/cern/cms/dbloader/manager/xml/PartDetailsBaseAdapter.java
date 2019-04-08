package org.cern.cms.dbloader.manager.xml;

import org.cern.cms.dbloader.model.construct.PartDetailsBase;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class PartDetailsBaseAdapter extends XmlAdapter<Object, PartDetailsBase> {

	private final XmlAdapter<Object, PartDetailsBase> adapter;

	public PartDetailsBaseAdapter() {
		this.adapter = new NullAdapter<>();
	}

	public PartDetailsBaseAdapter(JAXBContext jaxb, Class<? extends PartDetailsBase> cbClass) {
		this.adapter = new ClassAdapter<>(jaxb, cbClass, "PART_EXTENSION");
	}

	@Override
	public PartDetailsBase unmarshal(Object v) throws Exception {
		return adapter.unmarshal(v);
	}

	@Override
	public Object marshal(PartDetailsBase v) throws Exception {
		return adapter.marshal(v);
	}

	
	
}
