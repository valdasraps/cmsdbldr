package org.cern.cms.dbloader.manager;

import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import java.beans.PropertyEditorManager;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.List;
import org.cern.cms.dbloader.util.BigDecimalPropertyEditor;

public class CsvManager {

    // Set property editors for CSV converter
    static {
        PropertyEditorManager.registerEditor(BigDecimal.class, BigDecimalPropertyEditor.class);
    }

    public <T> List<T> read(Class<T> beanClass, String[] mapping, String fileName) throws FileNotFoundException {
        
        ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy();
        strategy.setType(beanClass);
        strategy.setColumnMapping(mapping);
        
        CsvToBean csvToBean = new CsvToBean();
        CSVReader reader = new CSVReader(new FileReader(fileName), ',', '"', 0);
        return csvToBean.parse(strategy, reader);
        
    }
    
}
