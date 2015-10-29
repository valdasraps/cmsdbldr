package org.cern.cms.dbloader.manager;

import org.cern.cms.dbloader.PropertiesManager;
import java.io.FileInputStream;
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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;
import org.cern.cms.dbloader.DbLoader;
import org.cern.cms.dbloader.model.OptId;
import org.cern.cms.dbloader.util.PropertiesException;

import com.google.inject.Singleton;
import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;

@Singleton
@SuppressWarnings("static-access")
public class CLIPropertiesManager implements PropertiesManager {

    private static final String URL_PREFIX = "jdbc:oracle:thin:@";
    private static final Level DEFAULT_LEVEL = Level.ERROR;
    private static final String DBLOADER_PROPERTIES = "/dbloader.properties";
    private static final String VERSION_KEY = "version";

    private enum UserOption {

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
        SCHEMA("save XML schema files to path", true, false),
        COND_LIST("list available conditions", false, false),
        COND_DESC("describe single condition", true, false),
        COND_XML("print condition XML example", true, false),
        COND_CLASS("print generated condition class", true, false),
        COND_DATASETS("list condition datasets", true, false),
        COND_DATASET("get XML stream data", true, false),
        CHANNEL_LIST("list available channels", false, false),
        CHANNEL_DESC("describe single channel", true, false),
        CHANNEL_CLASS("print generated channel class", true, false),
        TEST("upload test - proceed with the full upload process but rollback the transaction", false, false),
        LOG_LEVEL("log level, possible values OFF,FATAL,ERROR,WARN,INFO,DEBUG,TRACE. Default is " + DEFAULT_LEVEL.toString(), true, false),
        PRINT_SQL("print SQL queries to stdout", false, false);

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

    private final static Options options;

    static {
        options = new Options();
        for (UserOption o : UserOption.values()) {
            options.addOption(Option.builder().hasArg(o.hasArg).longOpt(o.key).desc(o.description).build());
        }
    }

    private final String version;
    private Map<UserOption, String> values = new EnumMap<>(UserOption.class);
    private List<String> args = new ArrayList<>();

    private CLIPropertiesManager() throws IOException {
        Properties dbloaderp = new Properties();
        try (InputStream is = CLIPropertiesManager.class.getResourceAsStream(DBLOADER_PROPERTIES)) {
            dbloaderp.load(is);
        }
        this.version = dbloaderp.getProperty(VERSION_KEY);
    }
    
    @SuppressWarnings("unchecked")
    public CLIPropertiesManager(Properties props, String[] files) throws Exception {
        this();

        loadProperties(props);
        checkMissingOptions();

        if (files != null) {
            this.args.addAll(Arrays.asList(files));
        }

    }
    
    @SuppressWarnings("unchecked")
    public CLIPropertiesManager(String[] args) throws Exception {
        this();

        // Parse command line
        CommandLineParser parser = new DefaultParser();
        final CommandLine line;
        try {
            line = parser.parse(options, args);
        } catch (ParseException ex) {
            throw new PropertiesException(ex.getMessage());
        }

        // Fill in properties from file
        if (line.hasOption(UserOption.PROPERTIES.key)) {
            Properties p = new Properties();
            try (InputStream in = new FileInputStream(line.getOptionValue(UserOption.PROPERTIES.key))) {
                p.load(in);
                loadProperties(p);
            }
        }

        // Collect command line properties
        for (UserOption o : UserOption.values()) {
            if (line.hasOption(o.key)) {
                this.values.put(o, line.getOptionValue(o.key));
            }
        }

        // Check missing properties
        checkMissingOptions();

        this.args.addAll(line.getArgList());

    }

    private void loadProperties(Properties props) {
        for (UserOption o : UserOption.values()) {
            if (props.containsKey(o.key)) {
                this.values.put(o, props.getProperty(o.key));
            }
        }
    }
    
    private void checkMissingOptions() throws PropertiesException {
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
    
    @Override
    public boolean printHelp() {
        if (this.values.containsKey(UserOption.HELP)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(DbLoader.class.getName(), options);
            return true;
        }
        return false;
    }

    @Override
    public boolean printVersion() {
        if (this.values.containsKey(UserOption.VERSION)) {
            System.out.println(String.format("DBLoader version %s", this.version));
            return true;
        }
        return false;
    }

    @Override
    public String getUrl() {
        return URL_PREFIX.concat(this.values.get(UserOption.URL));
    }

    @Override
    public String getUsername() {
        return this.values.get(UserOption.USERNAME);
    }

    @Override
    public String getPassword() {
        return this.values.get(UserOption.PASSWORD);
    }

    @Override
    public BigInteger getRootPartId() {
        return new BigInteger(this.values.get(UserOption.ROOT_PART_ID));
    }

    @Override
    public boolean isConditionsList() {
        return this.values.containsKey(UserOption.COND_LIST);
    }

    @Override
    public boolean isChannelsList() {
        return this.values.containsKey(UserOption.CHANNEL_LIST);
    }

    @Override
    public boolean isConditionDesc() {
        return this.values.containsKey(UserOption.COND_DESC);
    }

    @Override
    public boolean isChannelDesc() {
        return this.values.containsKey(UserOption.CHANNEL_DESC);
    }

    @Override
    public OptId getConditionDesc() {
        return new OptId(this.values.get(UserOption.COND_DESC));
    }

    @Override
    public String getChannelDesc() {
        return this.values.get(UserOption.CHANNEL_DESC);
    }

    @Override
    public boolean isCondDatasets() {
        return this.values.containsKey(UserOption.COND_DATASETS);
    }

    @Override
    public OptId getCondDatasets() {
        return new OptId(this.values.get(UserOption.COND_DATASETS));
    }

    @Override
    public boolean isCondDataset() {
        return this.values.containsKey(UserOption.COND_DATASET);
    }

    @Override
    public BigInteger getCondDataset() {
        return new BigInteger(this.values.get(UserOption.COND_DATASET));
    }

    @Override
    public boolean isConditionClass() {
        return this.values.containsKey(UserOption.COND_CLASS);
    }

    @Override
    public OptId getConditionClass() {
        return new OptId(this.values.get(UserOption.COND_CLASS));
    }

    @Override
    public boolean isChannelClass() {
        return this.values.containsKey(UserOption.CHANNEL_CLASS);
    }

    @Override
    public String getChannelClass() {
        return this.values.get(UserOption.CHANNEL_CLASS);
    }

    @Override
    public boolean isConditionXml() {
        return this.values.containsKey(UserOption.COND_XML);
    }

    @Override
    public OptId getConditionXml() {
        return new OptId(this.values.get(UserOption.COND_XML));
    }

    @Override
    public boolean isPrintSQL() {
        return this.values.containsKey(UserOption.PRINT_SQL);
    }

    @Override
    public boolean isSchema() {
        return this.values.containsKey(UserOption.SCHEMA);
    }

    @Override
    public String getSchemaParent() {
        return this.values.get(UserOption.SCHEMA);
    }

    @Override
    public List<String> getArgs() {
        return Collections.unmodifiableList(this.args);
    }

    @Override
    public Level getLogLevel() {
        if (this.values.containsKey(UserOption.LOG_LEVEL)) {
            return Level.toLevel(this.values.get(UserOption.LOG_LEVEL));
        } else {
            return DEFAULT_LEVEL;
        }
    }

    @Override
    public boolean isTest() {
        return this.values.containsKey(UserOption.TEST);
    }

    @Override
    public String getOsUser() {
        return System.getProperty("user.name");
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getHostName() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            return addr.getHostName();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }

    @Override
    public String getCoreConstructSchemaName() {
        return this.values.get(UserOption.CONSTRUCT_CORE_SCHEMA);
    }

    @Override
    public String getExtConstructSchemaName() {
        return this.values.get(UserOption.CONSTRUCT_EXT_SCHEMA);
    }

    @Override
    public String getCoreConditionSchemaName() {
        return this.values.get(UserOption.CONDITION_CORE_SCHEMA);
    }

    @Override
    public String getExtConditionSchemaName() {
        return this.values.get(UserOption.CONDITION_EXT_SCHEMA);
    }

    @Override
    public String getCoreManagemntSchemaName() {
        return this.values.get(UserOption.MANAGEMNT_CORE_SCHEMA);
    }

    @Override
    public String getIovCoreSchemaName() {
        return this.values.get(UserOption.IOV_CORE_SCHEMA);
    }

    @Override
    public String getCoreAttributeSchemaName() {
        return this.values.get(UserOption.ATTRIBUTE_CORE_SCHEMA);
    }

    @Override
    public String getCoreConstructTable(String tableName) {
        return getCoreConstructSchemaName().concat(".").concat(tableName);
    }

    @Override
    public String getExtConstructTable(String tableName) {
        return getExtConstructSchemaName().concat(".").concat(tableName);
    }

    @Override
    public String getCoreConditionTable(String tableName) {
        return getCoreConditionSchemaName().concat(".").concat(tableName);
    }

    @Override
    public String getExtConditionTable(String tableName) {
        return getExtConditionSchemaName().concat(".").concat(tableName);
    }

}
