package org.cern.cms.dbloader.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NullArgumentException;
import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.metadata.CondEntityHandler;
import org.cern.cms.dbloader.metadata.PropertyHandler;
import org.cern.cms.dbloader.model.condition.CondBase;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.xml.Root;

@Getter
public class LobManager {

    public void lobParser(final Root root, final CondEntityHandler condeh, final DataFile file) throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        if (root == null | condeh == null | file == null) {
            throw new NullArgumentException(null);
        }
        List<PropertyHandler> properties = condeh.getProperties();
        List<Dataset> dataSet = root.getDatasets();
        for (Dataset set : dataSet) {
            for (CondBase data : set.getData()) {
                for (PropertyHandler prop : properties) {
                    if ((prop.getType().equals(PropertyType.CLOB) || prop.getType().equals(PropertyType.BLOB))
                            && prop.getValue(data) == null) {
                        PropertyHandler fileProp = condeh.getPropertyByName(prop.getName().replaceAll("[CB]lob$", "File"));
                        if (fileProp != null) {
                            String fileName = (String) fileProp.getValue(data);
                            if (fileName != null) {
                                if ((prop.getType().equals(PropertyType.CLOB))) {
                                    prop.setValue(data, fileProcessClob(buildPath(file.getData().getAbsolutePath(), file.getData().getName(), fileName)));
                                } else if (prop.getType().equals(PropertyType.BLOB)) {
                                    prop.setValue(data, fileProcessBlob(buildPath(file.getData().getAbsolutePath(), file.getData().getName(), fileName)));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private String fileProcessClob(String path) throws IOException {
        if (path == null) {
            return null;
        }
        String tempLine;
        StringBuilder strBuff = new StringBuilder();
        try (BufferedReader istr = new BufferedReader(new FileReader(new File(path)))) {
            while ((tempLine = istr.readLine()) != null) {
                strBuff.append(tempLine).append("\n");
            }
        }
        return strBuff.toString();
    }

    private byte[] fileProcessBlob(String path) throws IOException {
        try (InputStream istr = new FileInputStream(new File(path))) {
            return IOUtils.toByteArray(istr);
        }
    }

    private String buildPath(String path, String replaceable, String replacement) {
        if (path == null || replacement == null || replaceable == null) {
            return null;
        }
        return path.replace(replaceable, replacement);
    }
}
