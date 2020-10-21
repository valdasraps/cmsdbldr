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
    AssemblyZipTest.class,
    CondAppendTest.class
})
public class TestSuite {

}
