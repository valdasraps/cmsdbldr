package org.cern.cms.dbloader.manager;

import java.io.InputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Level;
import org.cern.cms.dbloader.model.OptId;
import org.cern.cms.dbloader.util.PropertiesException;

import java.io.IOException;
import java.util.Arrays;

@SuppressWarnings("static-access")
public abstract class PropertiesManager {

    public static final String MODEL_PACKAGE = "org.cern.cms.dbloader.model";
    public static final String CONDITION_CORE_PACKAGE = MODEL_PACKAGE.concat(".condition");
    public static final String CONDITION_EXT_PACKAGE = CONDITION_CORE_PACKAGE.concat(".ext");
    public static final String CONDITION_XML_PACKAGE = MODEL_PACKAGE.concat(".xml");
    public static final String CONSTRUCT_PACKAGE = MODEL_PACKAGE.concat(".construct");
    public static final String IOV_CORE_PACKAGE = MODEL_PACKAGE.concat(".iov");
    public static final String CONFIG_CORE_PACKAGE = MODEL_PACKAGE.concat(".config");
    public static final String MANAGEMNT_CORE_PACKAGE = MODEL_PACKAGE.concat(".managemnt");
    
    protected static final String URL_PREFIX = "jdbc:oracle:thin:@";
    protected static final Level DEFAULT_LEVEL = Level.ERROR;
    protected static final String DBLOADER_PROPERTIES = "/dbloader.properties";
    protected static final String VERSION_KEY = "version";

    protected enum UserOption {

        HELP("print usage", false, false),
        VERSION("print version", false, false),
        PROPERTIES("path to connection properties file", true, false),
        URL("database connection URL", true, true),
        USERNAME("database username", true, true),
        PASSWORD("database password", true, true),
        ROOT_PART_ID("ROOT Part ID", true, true),
        CONSTRUCT_CORE_SCHEMA("CONSTRUCT: core schema name", true, true),
        CONSTRUCT_EXT_SCHEMA("CONSTRUCT: extension schema name", true, true),
        CONDITION_CORE_SCHEMA("CONDITION: core schema name", true, true),
        CONDITION_EXT_SCHEMA("CONDITION: extension schema name", true, true),
        MANAGEMNT_CORE_SCHEMA("MANAGEMNT: core schema name", true, true),
        IOV_CORE_SCHEMA("MANAGEMNT: iov schema name", true, true),
        ATTRIBUTE_CORE_SCHEMA("MANAGEMNT: attribute schema name", true, true),
        VIEW_EXT_SCHEMA("VIEW: extension schema name", true, false),
        SCHEMA("save XML schema files to path", true, false),
        COND_LIST("list available conditions", false, false),
        COND_DESC("describe single condition", true, false),
        COND_XML("print condition XML example", true, false),
        COND_CLASS("print generated condition class", true, false),
        COND_DATASETS("list condition dataset", true, false),
        COND_DATASET("get XML stream data", true, false),
        CHANNEL_LIST("list available channels", false, false),
        CHANNEL_DESC("describe single channel", true, false),
        CHANNEL_CLASS("print generated channel class", true, false),
        TEST("upload test - proceed with the full upload process but rollback the transaction", false, false),
        LOG_LEVEL("log level, possible values OFF,FATAL,ERROR,WARN,INFO,DEBUG,TRACE. Default is " + DEFAULT_LEVEL.toString(), true, false),
        PRINT_SQL("print SQL queries to stdout", false, false),
        FILE_USER("set file user, DEFAULT: process user", true, false);

        public final String key;
        public final String description;
        public final boolean hasArg;
        public final boolean required;

        UserOption(String description, boolean hasArg, boolean required) {
            this.description = description;
            this.hasArg = hasArg;
            this.required = required;
            this.key = this.name().toLowerCase().replaceAll("_", "-");
        }

    }

    protected final String version;
    protected final Map<UserOption, String> values = new EnumMap<>(UserOption.class);
    protected final List<String> args = new ArrayList<>();

    public PropertiesManager() throws IOException {
        Properties dbloaderp = new Properties();
        try (InputStream is = PropertiesManager.class.getResourceAsStream(DBLOADER_PROPERTIES)) {
            dbloaderp.load(is);
        }
        this.version = dbloaderp.getProperty(VERSION_KEY);
    }
    
    @SuppressWarnings("unchecked")
    public PropertiesManager(Properties props, String[] files) throws Exception {
        this();

        loadProperties(props);
        checkMissingOptions();

        if (files != null) {
            this.args.addAll(Arrays.asList(files));
        }

    }

    protected final void loadProperties(Properties props) {
        for (UserOption o : UserOption.values()) {
            if (props.containsKey(o.key)) {
                this.values.put(o, props.getProperty(o.key));
            }
        }
    }
    
    protected final void checkMissingOptions() throws PropertiesException {
        if (!this.values.containsKey(UserOption.HELP) && !this.values.containsKey(UserOption.VERSION)) {
            for (UserOption o : UserOption.values()) {
                if (o.required) {
                    if (!values.containsKey(o) || values.get(o) == null || values.get(o).isEmpty()) {
                        throw new PropertiesException("Argument %s (%s) is not provided. Type -help for instructions.", o.key, o.description);
                    }
                }
            }
        }
    }
    
    public abstract boolean printHelp();

    public boolean printVersion() {
        if (this.values.containsKey(UserOption.VERSION)) {
            System.out.println(String.format("DBLoader version %s", this.version));
            return true;
        }
        return false;
    }

    public String getUrl() {
        return URL_PREFIX.concat(this.values.get(UserOption.URL));
    }

    public String getUsername() {
        return this.values.get(UserOption.USERNAME);
    }

    public String getPassword() {
        return this.values.get(UserOption.PASSWORD);
    }

    public BigInteger getRootPartId() {
        return new BigInteger(this.values.get(UserOption.ROOT_PART_ID));
    }

    public boolean isConditionsList() {
        return this.values.containsKey(UserOption.COND_LIST);
    }

    public boolean isChannelsList() {
        return this.values.containsKey(UserOption.CHANNEL_LIST);
    }

    public boolean isConditionDesc() {
        return this.values.containsKey(UserOption.COND_DESC);
    }

    public boolean isChannelDesc() {
        return this.values.containsKey(UserOption.CHANNEL_DESC);
    }

    public OptId getConditionDesc() {
        return new OptId(this.values.get(UserOption.COND_DESC));
    }

    public String getChannelDesc() {
        return this.values.get(UserOption.CHANNEL_DESC);
    }

    public boolean isCondDatasets() {
        return this.values.containsKey(UserOption.COND_DATASETS);
    }

    public OptId getCondDatasets() {
        return new OptId(this.values.get(UserOption.COND_DATASETS));
    }

    public boolean isCondDataset() {
        return this.values.containsKey(UserOption.COND_DATASET);
    }

    public BigInteger getCondDataset() {
        return new BigInteger(this.values.get(UserOption.COND_DATASET));
    }

    public boolean isConditionClass() {
        return this.values.containsKey(UserOption.COND_CLASS);
    }

    public OptId getConditionClass() {
        return new OptId(this.values.get(UserOption.COND_CLASS));
    }

    public boolean isChannelClass() {
        return this.values.containsKey(UserOption.CHANNEL_CLASS);
    }

    public String getChannelClass() {
        return this.values.get(UserOption.CHANNEL_CLASS);
    }

    public boolean isConditionXml() {
        return this.values.containsKey(UserOption.COND_XML);
    }

    public OptId getConditionXml() {
        return new OptId(this.values.get(UserOption.COND_XML));
    }

    public boolean isPrintSQL() {
        return this.values.containsKey(UserOption.PRINT_SQL);
    }

    public boolean isSchema() {
        return this.values.containsKey(UserOption.SCHEMA);
    }

    public String getSchemaParent() {
        return this.values.get(UserOption.SCHEMA);
    }

    public List<String> getArgs() {
        return Collections.unmodifiableList(this.args);
    }

    public Level getLogLevel() {
        if (this.values.containsKey(UserOption.LOG_LEVEL)) {
            return Level.toLevel(this.values.get(UserOption.LOG_LEVEL));
        } else {
            return DEFAULT_LEVEL;
        }
    }

    public boolean isTest() {
        return this.values.containsKey(UserOption.TEST);
    }

    public String getOsUser() {
        return System.getProperty("user.name");
    }
    
    public String getFileUser() {
        if (this.values.containsKey(UserOption.FILE_USER)) {
            return this.values.get(UserOption.FILE_USER);
        } else {
            return getOsUser();
        }
    }

    public String getVersion() {
        return version;
    }

    public String getHostName() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            return addr.getHostName();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }

    public String getCoreConstructSchemaName() {
        return this.values.get(UserOption.CONSTRUCT_CORE_SCHEMA);
    }

    public String getExtConstructSchemaName() {
        return this.values.get(UserOption.CONSTRUCT_EXT_SCHEMA);
    }

    public String getCoreConditionSchemaName() {
        return this.values.get(UserOption.CONDITION_CORE_SCHEMA);
    }

    public String getExtConditionSchemaName() {
        return this.values.get(UserOption.CONDITION_EXT_SCHEMA);
    }

    public String getCoreManagemntSchemaName() {
        return this.values.get(UserOption.MANAGEMNT_CORE_SCHEMA);
    }

    public String getIovCoreSchemaName() {
        return this.values.get(UserOption.IOV_CORE_SCHEMA);
    }

    public String getCoreAttributeSchemaName() {
        return this.values.get(UserOption.ATTRIBUTE_CORE_SCHEMA);
    }

    public String getCoreConstructTable(String tableName) {
        return getCoreConstructSchemaName().concat(".").concat(tableName);
    }

    public String getExtConstructTable(String tableName) {
        return getExtConstructSchemaName().concat(".").concat(tableName);
    }

    public String getCoreConditionTable(String tableName) {
        return getCoreConditionSchemaName().concat(".").concat(tableName);
    }

    public String getExtConditionTable(String tableName) {
        return getExtConditionSchemaName().concat(".").concat(tableName);
    }

    public String getExtViewSchemaName() {
        return this.values.get(UserOption.VIEW_EXT_SCHEMA);
    }

    public boolean isExtViewSchemaName() {
        return this.values.containsKey(UserOption.VIEW_EXT_SCHEMA);
    }

}
