package org.cern.cms.dbloader.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    ChannelLoadZipTest.class,
    PartLoadTest.class,
    PartLoadJsonTest.class,
    GenericTablesInfoTest.class,
    ConditionXmlTest.class,
    CondLoadTest.class,
    CondLoadJsonTest.class,
    CondLoadZipTest.class,
    LobManagerTest.class,
    PropertiesTest.class,
    PropertyTypeTest.class,
    TablePrinterTest.class,
    TrackRequestLoadTest.class,
    TrackShipmentLoadTest.class,
    AuthTest.class, 
    CondAppendTest.class,
    AssemblySensor1Test.class,
    AssemblySensor2Test.class,
    AssemblyModuleTest.class
})
public class TestSuite {

}
