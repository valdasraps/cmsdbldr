package org.cern.cms.dbloader.manager;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.cern.cms.dbloader.manager.xml.*;
import org.cern.cms.dbloader.metadata.ConstructEntityHandler;
import org.cern.cms.dbloader.model.serial.Root;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import javax.xml.bind.Marshaller;
import org.apache.commons.beanutils.BeanUtils;
import org.cern.cms.dbloader.metadata.ChannelEntityHandler;
import org.cern.cms.dbloader.metadata.CondEntityHandler;
import org.cern.cms.dbloader.metadata.PropertyHandler;
import org.cern.cms.dbloader.model.condition.CondBase;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.condition.KindOfCondition;
import org.cern.cms.dbloader.model.condition.Run;
import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.serial.Header;

@Singleton
public final class XmlManager {

    private final JAXBContext jaxb;
    private final DynamicEntityGenerator enGenerator;

    @Inject
    public XmlManager(DynamicEntityGenerator enGenerator) throws Exception {
        this.enGenerator = enGenerator;
        
        Set<Class<?>> boundedClasses = new HashSet<>();
        boundedClasses.add(Root.class);

        for (ConstructEntityHandler eh : enGenerator.getConstructHandlers()) {
            boundedClasses.add(eh.getEntityClass().getC());
        }
        
        for (CondEntityHandler ch: enGenerator.getConditionHandlers()) {
            boundedClasses.add(ch.getEntityClass().getC());
        }
        
        for (ChannelEntityHandler ch: enGenerator.getChannelHandlers()) {
            boundedClasses.add(ch.getEntityClass().getC());
        }
        
        this.jaxb = JAXBContext.newInstance(boundedClasses.toArray(new Class<?>[]{}));
        
    }

    public Root unmarshal(File file) throws Exception {
        Unmarshaller ums = jaxb.createUnmarshaller();

        ums.setEventHandler(new EventHandler());
        ums.setAdapter(new DateAdapter());
        
        ums.setAdapter(CondBaseProxyAdapter.class, new CondBaseProxyAdapter(jaxb));
        ums.setAdapter(ChannelBaseProxyAdapter.class, new ChannelBaseProxyAdapter(jaxb));
        ums.setAdapter(PartDetailsBaseProxyAdapter.class, new PartDetailsBaseProxyAdapter(jaxb));

        return (Root) ums.unmarshal(file);
        
    }
    
    public void generateSchema(String folder) throws Exception {
        final File parent = new File(folder);
        jaxb.generateSchema(new SchemaOutputResolver() {

            @Override
            public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
                return new StreamResult(new File(parent, suggestedFileName));
            }

        });
    }
    
    private static Dataset createDatasetExample(CondEntityHandler condeh, Consumer<Part> setupPart) throws Exception {
       
        Dataset dataset = new Dataset();
        dataset.setVersion("1.0");
        dataset.setComment("Sample dataset");
        Part part = new Part();
        dataset.setPart(part);
        part.setChildren(null);
        part.setAttributes(null);
        setupPart.accept(part);

        List<CondBase> data = new ArrayList<>();
        for (int j = 0; j < 2; j++) {
            Constructor<? extends CondBase> constructor = condeh.getEntityClass().getC().getConstructor();
            CondBase d = constructor.newInstance();
            for (PropertyHandler pm : condeh.getProperties()) {
                BeanUtils.setProperty(d, pm.getName(), pm.getSampleData());
            }
            data.add(d);
        }
        dataset.setData(data);
        return dataset;
        
    }
    
    public void printCondExample(PropertiesManager props, 
                                 CondEntityHandler condeh,
                                 OutputStream out) throws Exception {
        Root root = new Root();

        Header header = new Header();
        root.setHeader(header);

        KindOfCondition kindOfCondition = new KindOfCondition();
        header.setKindOfCondition(kindOfCondition);
        kindOfCondition.setName(condeh.getName());
        kindOfCondition.setExtensionTable(condeh.getTableName());

        Run run = new Run();
        header.setRun(run);
        run.setNumber(BigInteger.valueOf(123456));
        run.setRunType("example");
        run.setBeginTime(new Date());
        run.setInitiatedByUser("John Data Uploader");
        run.setLocation("CERN, CH");
        run.setComment("Sample XML data fragment");

        root.setDatasets(new ArrayList<>());
        root.getDatasets().add(createDatasetExample(condeh, (Part p) -> {
            p.setId(props.getRootPartId());
        }));
        
        root.getDatasets().add(createDatasetExample(condeh, (Part p) -> {
            p.setBarcode("1234567890");
        }));

        root.getDatasets().add(createDatasetExample(condeh, (Part p) -> {
            p.setKindOfPartName("Kind of Part");
            p.setSerialNumber("12345-serial#");
        }));

        root.getDatasets().add(createDatasetExample(condeh, (Part p) -> {
            p.setKindOfPartName("Kind of Part");
            p.setName("some-name-label");
        }));
        
        Marshaller ms = jaxb.createMarshaller();
        ms.setAdapter(CondBaseProxyAdapter.class, new CondBaseProxyAdapter(jaxb, condeh.getEntityClass().getC()));
        ms.setAdapter(ChannelBaseProxyAdapter.class, new ChannelBaseProxyAdapter(jaxb));
        ms.setAdapter(PartDetailsBaseProxyAdapter.class, new PartDetailsBaseProxyAdapter(jaxb));

        ms.setEventHandler(new EventHandler());
        ms.setAdapter(new DateAdapter());
        ms.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ms.marshal(root, out);

    }

    public void printDatasetDataXML(Dataset datasetS, 
                                    CondEntityHandler condeh,
                                    List<? extends CondBase> data2,
                                    OutputStream out) throws Exception {
        Root root = new Root();

        Header header = new Header();
        root.setHeader(header);

        KindOfCondition kindOfCondition = new KindOfCondition();
        header.setKindOfCondition(kindOfCondition);
        kindOfCondition.setName(datasetS.getKindOfCondition().getName());
        kindOfCondition.setExtensionTable(datasetS.getKindOfCondition().getExtensionTable());

        Run run = new Run();
        header.setRun(run);
        run.setNumber(datasetS.getRun().getNumber());
        run.setBeginTime(datasetS.getRun().getBeginTime());
        run.setInitiatedByUser(datasetS.getRun().getInitiatedByUser());
        run.setLocation(datasetS.getRun().getLocation());
        run.setComment(datasetS.getRun().getComment());

        root.setDatasets(new ArrayList<>());

        Dataset dataset = new Dataset();
        root.getDatasets().add(dataset);
        dataset.setVersion(datasetS.getVersion());
        dataset.setComment(datasetS.getComment());
        dataset.setAttributes(null);
        if (datasetS.getPart() != null) {
            Part part = new Part();
            part.setId(datasetS.getPart().getId());
            part.setKindOfPartName(datasetS.getPart().getKindOfPart().getName());
            part.setBarcode(datasetS.getPart().getBarcode());
            part.setName(datasetS.getPart().getName());
            part.setChildren(null);
            part.setAttributes(null);
            dataset.setPart(part);
        }
        
        for (CondBase d: data2) {
            for (PropertyHandler pm : condeh.getProperties()) {
                if (pm.getType() == PropertyType.BLOB || pm.getType() == PropertyType.CLOB) {
                    BeanUtils.setProperty(d, pm.getName(), null);
                }
            }
        }
        
        dataset.setData(data2);
        
        Marshaller ms = jaxb.createMarshaller();
        ms.setEventHandler(new EventHandler());
        ms.setAdapter(new DateAdapter());
        ms.setAdapter(CondBaseProxyAdapter.class, new CondBaseProxyAdapter(jaxb, condeh.getEntityClass().getC()));
        ms.setAdapter(ChannelBaseProxyAdapter.class, new ChannelBaseProxyAdapter(jaxb));
        ms.setAdapter(PartDetailsBaseProxyAdapter.class, new PartDetailsBaseProxyAdapter(jaxb));
        ms.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ms.marshal(root, out);
    }


}
