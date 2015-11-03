package org.cern.cms.dbloader.manager;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.beanutils.BeanUtils;
import org.cern.cms.dbloader.PropertiesManager;
import org.cern.cms.dbloader.manager.xml.ChannelBaseAdapter;
import org.cern.cms.dbloader.manager.xml.CondBaseAdapter;
import org.cern.cms.dbloader.manager.xml.DateAdapter;
import org.cern.cms.dbloader.manager.xml.EventHandler;
import org.cern.cms.dbloader.metadata.ChannelEntityHandler;
import org.cern.cms.dbloader.metadata.CondEntityHandler;
import org.cern.cms.dbloader.metadata.PropertyHandler;
import org.cern.cms.dbloader.model.condition.CondBase;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.condition.KindOfCondition;
import org.cern.cms.dbloader.model.condition.Run;
import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.xml.Header;
import org.cern.cms.dbloader.model.xml.Root;

public class CondXmlManager extends XmlManager {

    private final CondEntityHandler condeh;
    private final ChannelEntityHandler channeleh;

    public CondXmlManager(CondEntityHandler condeh) throws Exception {
        this(condeh, null);
    }

    public CondXmlManager(CondEntityHandler condeh, ChannelEntityHandler channeleh) throws Exception {
        this.condeh = condeh;
        this.channeleh = channeleh;
        this.boundClass(condeh.getEntityClass().getC());
        if (channeleh != null) {
            this.boundClass(channeleh.getEntityClass().getC());
        }
    }

    @Override
    public Root unmarshal(File file) throws Exception {
        Unmarshaller ums = getJAXBContext().createUnmarshaller();

        ums.setEventHandler(new EventHandler());
        ums.setAdapter(new DateAdapter());

        CondBaseAdapter condBaseAdapter = new CondBaseAdapter(getJAXBContext(), condeh.getEntityClass().getC());
        ums.setAdapter(CondBaseAdapter.class, condBaseAdapter);

        ChannelBaseAdapter channelBaseAdapter;
        if (channeleh != null) {
            channelBaseAdapter = new ChannelBaseAdapter(getJAXBContext(), channeleh.getEntityClass().getC());
        } else {
            channelBaseAdapter = new ChannelBaseAdapter();
        }
        ums.setAdapter(ChannelBaseAdapter.class, channelBaseAdapter);

        return (Root) ums.unmarshal(file);
    }

    public void printExample(PropertiesManager props, OutputStream out) throws Exception {
        Root root = new Root();

        Header header = new Header();
        root.setHeader(header);

        KindOfCondition kindOfCondition = new KindOfCondition();
        header.setKindOfCondition(kindOfCondition);
        kindOfCondition.setName(condeh.getName());
        kindOfCondition.setExtensionTable(condeh.getTableName());

        Run run = new Run();
        header.setRun(run);
        run.setNumber("123456");
        run.setRunType("example");
        run.setBeginTime(new Date());
        run.setInitiatedByUser("John Data Uploader");
        run.setLocation("CERN, CH");
        run.setComment("Sample XML data fragment");

        root.setDatasets(new ArrayList<Dataset>());
        for (int i = 0; i < 3; i++) {
            Dataset dataset = new Dataset();
            root.getDatasets().add(dataset);
            dataset.setVersion("1.0");
            dataset.setComment("Sample dataset");
            Part part = new Part();
            dataset.setPart(part);
            part.setId(props.getRootPartId());

            List<CondBase> data = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                CondBase d = condeh.getEntityClass().getC().newInstance();
                for (PropertyHandler pm : condeh.getProperties()) {
                    BeanUtils.setProperty(d, pm.getName(), pm.getSampleData());
                }
                data.add(d);
            }
            dataset.setData(data);

        }

        Marshaller ms = getJAXBContext().createMarshaller();
        if (channeleh != null) {
            ms.setAdapter(ChannelBaseAdapter.class, new ChannelBaseAdapter(getJAXBContext(), channeleh.getEntityClass().getC()));
        }
        if (condeh != null) {
            ms.setAdapter(CondBaseAdapter.class, new CondBaseAdapter(getJAXBContext(), condeh.getEntityClass().getC()));
        }
        ms.setEventHandler(new EventHandler());
        ms.setAdapter(new DateAdapter());
        ms.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ms.marshal(root, out);

    }

    public void printDatasetDataXML(Dataset datasetS, List<? extends CondBase> data2) throws Exception {
        Root root = new Root();

        Header header = new Header();
        root.setHeader(header);

        KindOfCondition kindOfCondition = new KindOfCondition();
        header.setKindOfCondition(kindOfCondition);
        kindOfCondition.setName(datasetS.getKindOfCondition().getName());
        kindOfCondition.setExtensionTable(datasetS.getKindOfCondition().getExtensionTable());

        Run run = new Run();
        header.setRun(run);
        run.setNumber(datasetS.getRun().getId().toString());
        run.setBeginTime(datasetS.getRun().getBeginTime());
        run.setInitiatedByUser(datasetS.getRun().getInitiatedByUser());
        run.setLocation(datasetS.getRun().getLocation());
        run.setComment(datasetS.getRun().getComment());

        root.setDatasets(new ArrayList<Dataset>());

        Dataset dataset = new Dataset();
        root.getDatasets().add(dataset);
        dataset.setVersion(datasetS.getVersion());
        dataset.setComment(datasetS.getComment());
        if (datasetS.getPart() != null) {
            Part part = new Part();
            part.setKindOfPartName(datasetS.getPart().getKindOfPart().getName());
            part.setBarcode(datasetS.getPart().getBarcode());
            dataset.setPart(part);
        }
        dataset.setData(data2);

        Marshaller ms = getJAXBContext().createMarshaller();
        ms.setEventHandler(new EventHandler());
        ms.setAdapter(new DateAdapter());
        ms.setAdapter(CondBaseAdapter.class, new CondBaseAdapter(getJAXBContext(), condeh.getEntityClass().getC()));
        ms.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ms.marshal(root, System.out);
    }

}
