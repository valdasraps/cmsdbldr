package org.cern.cms.dbloader;

import org.cern.cms.dbloader.tests.CondInfoTest;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;
import org.cern.cms.dbloader.manager.CLIPropertiesManager;
import org.cern.cms.dbloader.manager.EntityModificationManager;
import org.cern.cms.dbloader.manager.ResourceFactory;
import org.cern.cms.dbloader.tests.PropertiesTest;
import org.junit.BeforeClass;
import org.junit.Ignore;

@Ignore
public abstract class TestBase {

    protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    
    protected static PropertiesManager pm;
    protected static Injector injector;
    protected static ResourceFactory rf;

    @BeforeClass
    public static void init() throws Exception {

        if (pm != null) return;
        
        Properties p = new Properties();
        try (InputStream is = CondInfoTest.class.getResourceAsStream("/properties/test.properties")) {
            p.load(is);
        }
        
        if (System.getenv("TRAVIS_CI") != null) {
            p.remove("print-sql");
        }

        pm = new CLIPropertiesManager(p, new String[]{"file1.xml", "file2.xml"});

        EntityModificationManager.modify(pm);

        injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {

                bind(PropertiesManager.class).toInstance(pm);
                install(new FactoryModuleBuilder().build(ResourceFactory.class));

            }
        });
        
        rf = injector.getInstance(ResourceFactory.class);

    }
    
    protected static Properties load(String propertiesResource, String... additionalProperties) throws IOException {
        Properties p = new Properties();
        try (InputStream is = PropertiesTest.class.getResourceAsStream(propertiesResource)) {
            p.load(is);
        }
        for (String ap: additionalProperties) {
            String[] split = ap.split("=");
            p.setProperty(split[0], split.length > 1 ? split[1] : null);
        }
        return p;
    }
    
}
