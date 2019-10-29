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

import org.cern.cms.dbloader.manager.xml.*;
import org.cern.cms.dbloader.metadata.ConstructEntityHandler;
import org.cern.cms.dbloader.model.serial.Root;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class XmlManager {

    @Inject
    private PropertiesManager props;

    private JAXBContext jaxb;
    private final Set<Class<?>> boundedClasses = new HashSet<>();

    @Inject
    public XmlManager(DynamicEntityGenerator enGenerator) throws Exception {
        this.boundedClasses.add(Root.class);

        for (ConstructEntityHandler eh : enGenerator.getConstructHandlers()) {
            this.boundedClasses.add(eh.getEntityClass().getC());
        }
    }

    public JAXBContext getJAXBContext() throws JAXBException {
        if (jaxb == null) {
            this.jaxb = JAXBContext.newInstance(boundedClasses.toArray(new Class<?>[]{}));
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
        ums.setAdapter(PartDetailsBaseAdapter.class, new PartDetailsBaseAdapter(getJAXBContext()));

        return (Root) ums.unmarshal(file);
    }

    public void generateSchema(String folder) throws Exception {
        final File parent = new File(folder);
        getJAXBContext().generateSchema(new SchemaOutputResolver() {

            @Override
            public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
                return new StreamResult(new File(parent, suggestedFileName));
            }

        });
    }

}
