package org.cern.cms.dbloader.app;

import com.google.inject.Inject;
import com.google.inject.Injector;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import lombok.extern.log4j.Log4j;
import org.cern.cms.dbloader.dao.DatasetDao;
import org.cern.cms.dbloader.manager.DynamicEntityGenerator;
import org.cern.cms.dbloader.manager.HelpPrinter;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.manager.XmlManager;
import org.cern.cms.dbloader.metadata.CondEntityHandler;
import org.cern.cms.dbloader.model.OptId;
import org.cern.cms.dbloader.model.condition.CondBase;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.util.NotAuthorizedException;
import org.cern.cms.dbloader.util.OperatorAuth;

/**
 * Dataset helper class.
 * @author valdo
 */
@Log4j
public class DatasetApp extends AppBase {

    @Inject
    private Injector injector;
    
    @Override
    public void checkPermission(OperatorAuth auth) throws NotAuthorizedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean handleInfo() throws Exception {
        
        OperatorAuth auth = props.getOperatorAuth();

        if (props.isCondDatasets()) {
            
            OptId optId = props.getCondDatasets();
            DynamicEntityGenerator enG = injector.getInstance(DynamicEntityGenerator.class);
            CondEntityHandler ch = enG.getConditionHandler(optId);
            try (SessionManager sm = injector.getInstance(SessionManager.class)) {
                DatasetDao dao = rf.createDatasetDao(sm, auth);
                HelpPrinter.outputDatasetList(System.out, dao.getCondDatasets(ch));
            }
            
            return true;
            
        }
        
        if (props.isCondDatasetSamples()) {
            
            DynamicEntityGenerator enG = injector.getInstance(DynamicEntityGenerator.class);
            XmlManager xmlm = injector.getInstance(XmlManager.class);
            
            try (SessionManager sm = injector.getInstance(SessionManager.class)) {
                DatasetDao dao = rf.createDatasetDao(sm, auth);
                
                final File parent = new File(props.getCondDatasetSamples());
                SortedMap<String, String> sampleFiles = new TreeMap<>();
                
                for (CondEntityHandler ceh: enG.getConditionHandlers()) {
                    
                    Dataset dataset = null;
                    try {
                        
                        dataset = dao.getCondDatasetSample(ceh);
                        
                    } catch (Exception ex) {
                        log.warn("Error while retrieving dataset sample. Skipping...", ex);
                    }
                    
                    String fileName = ceh.getId().toString().concat(".xml");
                    
                    try (FileOutputStream out = new FileOutputStream(new File(parent, fileName))) {
                    
                        if (dataset != null) {
                            List<? extends CondBase> dataSetData = dao.getDatasetData(ceh, dataset);
                            xmlm.printDatasetDataXML(dataset, ceh, dataSetData, out);
                        } else {
                            xmlm.printCondExample(props, ceh, out);
                        }
                        
                    }
                    
                    sampleFiles.put(ceh.getName(), fileName);
                    
                }
                
                try (PrintWriter out = new PrintWriter(new File(parent, "index.html"))) {
                    out.append("<html><head><title>Condition data XML examples</title></head><body>");
                    out.append("<h3>Condition data XML examples</h3><ul>");
                    for (Map.Entry<String, String> e: sampleFiles.entrySet()) {
                        out.append("<li><a href=\"xml/");
                        out.append(e.getValue());
                        out.append("\">");
                        out.append(e.getKey());
                        out.append("</li>");
                    }
                    out.append("</li></body></html>");
                }
                
            }
            
            return true;
            
        }

        if (props.isCondDataset()) {
            
            BigInteger dataSetId = props.getCondDataset();
            DynamicEntityGenerator enG = injector.getInstance(DynamicEntityGenerator.class);

            try (SessionManager sm = injector.getInstance(SessionManager.class)) {
                    
                DatasetDao dao = rf.createDatasetDao(sm, auth);
                Dataset dataset = dao.getDataset(dataSetId);
                BigInteger id = dataset.getKindOfCondition().getId();
                CondEntityHandler ceh = enG.getConditionHandler(id);
                if (ceh == null) {
                    throw new IllegalArgumentException(String.format("[%s] dataset not found!", dataSetId));
                }

                List<? extends CondBase> dataSetData = dao.getDatasetData(ceh, dataset);
                if (dataSetData.isEmpty()) {
                    throw new IllegalArgumentException(String.format("[%s] dataset data not found!", dataSetId));
                }

                XmlManager xmlm = injector.getInstance(XmlManager.class);
                xmlm.printDatasetDataXML(dataset, ceh, dataSetData, System.out);

            }

            return true;
            
        }
        
        return false;
        
    }
    
}
