package org.cern.cms.dbloader.manager;

import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import java.beans.PropertyEditorManager;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import lombok.extern.log4j.Log4j;
import org.cern.cms.dbloader.metadata.EntityHandler;
import org.cern.cms.dbloader.metadata.PropertyHandler;
import org.cern.cms.dbloader.util.BigDecimalPropertyEditor;

@Log4j
public class CsvManager {

    // Set property editors for CSV converter
    static {
        PropertyEditorManager.registerEditor(BigDecimal.class, BigDecimalPropertyEditor.class);
    }
    
    private class Mapping {
        
        boolean header = false;
        String[] columns;
        
    }

    public <T> List<T> read(EntityHandler<T> eh, String fileName) throws Exception {
        
        Mapping mapping = getMapping(eh, fileName);
        
        ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy();
        strategy.setType(eh.getEntityClass().getC());
        strategy.setColumnMapping(mapping.columns);
        
        CsvToBean csvToBean = new CsvToBean();
        try (FileReader fr = new FileReader(fileName)) {
            CSVReader reader = new CSVReader(fr, ',', '"', mapping.header ? 1 : 0);
            return csvToBean.parse(strategy, reader);
        }
        
    }
    
    private <T> Mapping getMapping(EntityHandler<T> eh, String filename) throws IOException {
        
        boolean header = true;
        
        // Read first line from file (aka header)
        String[] fields;
        try (FileReader fr = new FileReader(filename)) {
            CSVReader reader = new CSVReader(fr, ',', '"', 0);
            fields = reader.readNext();
        }
        
        // Loop fields found
        for (int i = 0; i < fields.length; i++) {
            String f = fields[i];
            String v = null;
            
            // Check if property exists... set right stuff
            for (PropertyHandler ph: eh.getProperties()) {
                if (ph.getName().equalsIgnoreCase(f) || ph.getColumnName().equalsIgnoreCase(f)) {
                    v = ph.getName();
                    break;
                }
            }
            
            // If field not found, return default mapping
            if (v == null) {
                fields = eh.getProperties().stream().map(p -> p.getName()).toArray(s -> new String[s]);
                header = false;
                log.debug(String.format("Mapping for field %s not found. Using default mapping for file %s.", f, filename));
            } else {
                fields[i] = v;
            }
        }
        
        log.debug(String.format("Mapping for file %s: %s", filename, fields));
        
        return new Mapping(){{
            this.header = header;
            this.columns = columns;
        }};
    }
    
}
