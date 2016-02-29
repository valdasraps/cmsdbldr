package org.cern.cms.dbloader.manager;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.cern.cms.dbloader.util.PropertiesException;

import com.google.inject.Singleton;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.cern.cms.dbloader.DbLoader;
import org.cern.cms.dbloader.manager.PropertiesManager.UserOption;

@Singleton
@SuppressWarnings("static-access")
public class CLIPropertiesManager extends PropertiesManager {

    private final static Options options;

    static {
        options = new Options();
        for (UserOption o : UserOption.values()) {
            options.addOption(Option.builder().hasArg(o.hasArg).longOpt(o.key).desc(o.description).build());
        }
    }
    
    @SuppressWarnings("unchecked")
    public CLIPropertiesManager(String[] args) throws Exception {
        super();

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
    
    @Override
    public boolean printHelp() {
        if (this.values.containsKey(UserOption.HELP)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(DbLoader.class.getName(), options);
            return true;
        }
        return false;
    }
    
}
