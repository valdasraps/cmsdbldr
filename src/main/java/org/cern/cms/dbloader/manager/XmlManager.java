package org.cern.cms.dbloader.manager;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.cern.cms.dbloader.manager.xml.ChannelBaseAdapter;
import org.cern.cms.dbloader.manager.xml.CondBaseAdapter;
import org.cern.cms.dbloader.manager.xml.DateAdapter;
import org.cern.cms.dbloader.manager.xml.EventHandler;
import org.cern.cms.dbloader.model.xml.Root;

public class XmlManager {
	
	private JAXBContext jaxb;
	private Set<Class<?>> boundedClasses = new HashSet<>();
	
	public XmlManager() {
		this.boundedClasses.add(Root.class);
	}
	
	protected JAXBContext getJAXBContext() throws JAXBException {
		if (jaxb == null) {
			this.jaxb = JAXBContext.newInstance(boundedClasses.toArray(new Class<?> []{}));
		}
		return this.jaxb;
	}
	
	protected final void boundClass(Class<?> classToBound) throws Exception {
		if (jaxb == null) {
			boundedClasses.add(classToBound);
		} else {
			throw new Exception("JAXB Context already initialized!");
		}
	}
	
	public Root unmarshal(File file) throws Exception {
		Unmarshaller ums = getJAXBContext().createUnmarshaller();
		
		ums.setEventHandler(new EventHandler());
		ums.setAdapter(new DateAdapter());
		ums.setAdapter(CondBaseAdapter.class, new CondBaseAdapter());
		ums.setAdapter(ChannelBaseAdapter.class, new ChannelBaseAdapter());
		
		return (Root) ums.unmarshal(file);
	}
	
	public void generateSchema(final String parentPath) throws Exception {
		getJAXBContext().generateSchema(new SchemaOutputResolver() {
			
			private File parent = new File(parentPath);
			
			@Override
			public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
				return new StreamResult(new File(parent, suggestedFileName));
			}
			
		});
	}
	
}
