package org.cern.cms.dbloader;

import java.math.BigInteger;
import java.util.List;
import org.apache.log4j.Level;
import org.cern.cms.dbloader.model.OptId;


public interface PropertiesManager {
    
    public static final String MODEL_PACKAGE = "org.cern.cms.dbloader.model";
    public static final String CONDITION_CORE_PACKAGE = MODEL_PACKAGE.concat(".condition");
    public static final String CONDITION_EXT_PACKAGE = CONDITION_CORE_PACKAGE.concat(".ext");
    public static final String CONDITION_XML_PACKAGE = MODEL_PACKAGE.concat(".xml");
    public static final String CONSTRUCT_PACKAGE = MODEL_PACKAGE.concat(".construct");
    public static final String IOV_CORE_PACKAGE = MODEL_PACKAGE.concat(".iov");
    public static final String MANAGEMNT_CORE_PACKAGE = MODEL_PACKAGE.concat(".managemnt");

    List<String> getArgs();

    String getChannelClass();

    String getChannelDesc();

    BigInteger getCondDataset();

    OptId getCondDatasets();

    OptId getConditionClass();

    OptId getConditionDesc();

    OptId getConditionXml();

    String getCoreAttributeSchemaName();

    String getCoreConditionSchemaName();

    String getCoreConditionTable(String tableName);

    String getCoreConstructSchemaName();

    String getCoreConstructTable(String tableName);

    String getCoreManagemntSchemaName();

    String getExtConditionSchemaName();

    String getExtConditionTable(String tableName);

    String getExtConstructSchemaName();

    String getExtConstructTable(String tableName);

    String getHostName();

    String getIovCoreSchemaName();

    Level getLogLevel();

    String getOsUser();

    String getPassword();

    BigInteger getRootPartId();

    String getSchemaParent();

    String getUrl();

    String getUsername();

    String getVersion();

    boolean isChannelClass();

    boolean isChannelDesc();

    boolean isChannelsList();

    boolean isCondDataset();

    boolean isCondDatasets();

    boolean isConditionClass();

    boolean isConditionDesc();

    boolean isConditionXml();

    boolean isConditionsList();

    boolean isPrintSQL();

    boolean isSchema();

    boolean isTest();

    boolean printHelp();

    boolean printVersion();
    
}
