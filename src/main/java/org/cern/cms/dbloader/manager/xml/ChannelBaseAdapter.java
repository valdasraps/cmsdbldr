package org.cern.cms.dbloader.manager.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.cern.cms.dbloader.model.condition.ChannelBase;

public class ChannelBaseAdapter extends XmlAdapter<Object, ChannelBase> {
	
	private final XmlAdapter<Object, ChannelBase> adapter;
	
	public ChannelBaseAdapter() {
		this.adapter = new NullAdapter<>();
	}
	
	public ChannelBaseAdapter(JAXBContext jaxb, Class<? extends ChannelBase> cbClass) {
		this.adapter = new ClassAdapter<>(jaxb, cbClass, "CHANNEL");
	}

	@Override
	public ChannelBase unmarshal(Object v) throws Exception {
		return adapter.unmarshal(v);
	}

	@Override
	public Object marshal(ChannelBase v) throws Exception {
		return adapter.marshal(v);
	}

}
