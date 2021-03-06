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
import org.cern.cms.dbloader.metadata.ConstructEntityHandler;
import org.cern.cms.dbloader.metadata.PropertyHandler;
import org.cern.cms.dbloader.model.condition.CondBase;
import org.cern.cms.dbloader.model.construct.PartDetailsBase;

@Getter
public class LobManager {

    public void lobParser(final CondBase data, final CondEntityHandler condeh, final File file) throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        if (data == null | condeh == null | file == null) {
            throw new NullArgumentException(null);
        }
        List<PropertyHandler> properties = condeh.getProperties();
        for (PropertyHandler prop : properties) {
            if ((prop.getType().equals(PropertyType.CLOB) || prop.getType().equals(PropertyType.BLOB))
                    && prop.getValue(data) == null) {
                PropertyHandler fileProp = condeh.getPropertyByName(prop.getName().replaceAll("[CB]lob$", "File"));
                if (fileProp != null) {
                    String fileName = (String) fileProp.getValue(data);
                    if (fileName != null) {
                        if ((prop.getType().equals(PropertyType.CLOB))) {
                            prop.setValue(data, fileProcessClob(buildPath(file.getAbsolutePath(), file.getName(), fileName)));
                        } else if (prop.getType().equals(PropertyType.BLOB)) {
                            prop.setValue(data, fileProcessBlob(buildPath(file.getAbsolutePath(), file.getName(), fileName)));
                        }
                    }
                }
            }
        }
    }

    public void lobParserParts(final PartDetailsBase partDetailsBase, final ConstructEntityHandler coneh, final DataFile file) throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        if (partDetailsBase == null | coneh == null | file == null) {
            throw new NullArgumentException(null);
        }
        List<PropertyHandler> properties = coneh.getProperties();
        if (partDetailsBase != null) {
            for (PropertyHandler prop : properties) {
                if ((prop.getType().equals(PropertyType.CLOB) || prop.getType().equals(PropertyType.BLOB))
                        && prop.getValue(partDetailsBase) == null) {
                    PropertyHandler fileProp = coneh.getPropertyByName(prop.getName().replaceAll("[CB]lob$", "File"));
                    if (fileProp != null) {
                        String fileName = (String) fileProp.getValue(partDetailsBase);
                        if (fileName != null) {
                            if ((prop.getType().equals(PropertyType.CLOB))) {
                                prop.setValue(partDetailsBase, fileProcessClob(buildPath(file.getFile().getAbsolutePath(), file.getFile().getName(), fileName)));
                            } else if (prop.getType().equals(PropertyType.BLOB)) {
                                prop.setValue(partDetailsBase, fileProcessBlob(buildPath(file.getFile().getAbsolutePath(), file.getFile().getName(), fileName)));
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
