package org.cern.cms.dbloader.manager;

import org.apache.commons.beanutils.BeanUtils;
import org.cern.cms.dbloader.manager.xml.*;
import org.cern.cms.dbloader.metadata.ChannelEntityHandler;
import org.cern.cms.dbloader.metadata.CondEntityHandler;
import org.cern.cms.dbloader.metadata.ConstructEntityHandler;
import org.cern.cms.dbloader.metadata.PropertyHandler;
import org.cern.cms.dbloader.model.condition.CondBase;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.condition.KindOfCondition;
import org.cern.cms.dbloader.model.condition.Run;
import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.serial.Header;
import org.cern.cms.dbloader.model.serial.Root;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConstructXmlManager extends XmlManager {

    private final ConstructEntityHandler conseh;

//    public ConstructXmlManager(ConstructEntityHandler conseh) throws Exception {
//        this(conseh);
//    }

    public ConstructXmlManager(ConstructEntityHandler conseh) throws Exception {
        this.conseh = conseh;
        this.boundClass(conseh.getEntityClass().getC());
    }

    @Override
    public Root unmarshal(File file) throws Exception {
        Unmarshaller ums = getJAXBContext().createUnmarshaller();

        ums.setEventHandler(new EventHandler());
        ums.setAdapter(new DateAdapter());
        ums.setAdapter(new TimestampAdapter());
        PartDetailsBaseAdapter partDetailsBaseAdapter = new PartDetailsBaseAdapter(getJAXBContext(), conseh.getEntityClass().getC());
        ums.setAdapter(PartDetailsBaseAdapter.class, partDetailsBaseAdapter);

        return (Root) ums.unmarshal(file);
    }

//    public void printDatasetDataXML(Dataset datasetS, List<? extends CondBase> data2) throws Exception {
//        Root root = new Root();
//
//        Header header = new Header();
//        root.setHeader(header);
//
//        KindOfCondition kindOfCondition = new KindOfCondition();
//        header.setKindOfCondition(kindOfCondition);
//        kindOfCondition.setName(datasetS.getKindOfCondition().getName());
//        kindOfCondition.setExtensionTable(datasetS.getKindOfCondition().getExtensionTable());
//
//        Run run = new Run();
//        header.setRun(run);
//        run.setNumber(datasetS.getRun().getId().toString());
//        run.setBeginTime(datasetS.getRun().getBeginTime());
//        run.setInitiatedByUser(datasetS.getRun().getInitiatedByUser());
//        run.setLocation(datasetS.getRun().getLocation());
//        run.setComment(datasetS.getRun().getComment());
//
//        root.setDatasets(new ArrayList<Dataset>());
//
//        Dataset dataset = new Dataset();
//        root.getDatasets().add(dataset);
//        dataset.setVersion(datasetS.getVersion());
//        dataset.setComment(datasetS.getComment());
//        if (datasetS.getPart() != null) {
//            Part part = new Part();
//            part.setKindOfPartName(datasetS.getPart().getKindOfPart().getName());
//            part.setBarcode(datasetS.getPart().getBarcode());
//            dataset.setPart(part);
//        }
//        dataset.setData(data2);
//
//        Marshaller ms = getJAXBContext().createMarshaller();
//        ms.setEventHandler(new EventHandler());
//        ms.setAdapter(new DateAdapter());
//        ms.setAdapter(CondBaseAdapter.class, new CondBaseAdapter(getJAXBContext(), condeh.getEntityClass().getC()));
//        ms.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//        ms.marshal(root, System.out);
//    }

}
