package org.cern.cms.dbloader.manager.xml;

import org.cern.cms.dbloader.model.construct.PartDetailsBase;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class PartDetailsBaseAdapter extends XmlAdapter<Object, PartDetailsBase> {

	private final XmlAdapter<Object, PartDetailsBase> adapter;
	private final JAXBContext jaxb;

	public PartDetailsBaseAdapter() {
		this.adapter = new NullAdapter<>();
		this.jaxb = null;
	}

	public PartDetailsBaseAdapter(JAXBContext jaxb) {
		this.adapter = new NullAdapter<>();
		this.jaxb = jaxb;
	}

	@Override
	public PartDetailsBase unmarshal(Object v) throws Exception {
		return new PartDetailsBase() {

			private final Object xmlObject = v;
			private final JAXBContext jaxbCache = jaxb;

			public <T extends PartDetailsBase> T getRealClass(Class<T> clazz) throws Exception {
				return new ClassAdapter<>(jaxbCache, clazz, "PART_EXTENSION").unmarshal(xmlObject);
			}

		};
	}

	@Override
	public Object marshal(PartDetailsBase v) throws Exception {
//		throw new UnsupportedOperationException();
		return null;
	}

}
