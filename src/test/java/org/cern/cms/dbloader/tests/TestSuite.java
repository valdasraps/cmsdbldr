package org.cern.cms.dbloader.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    ChannelLoadZipTest.class,
    PartLoadTest.class,
    CondInfoTest.class,
    ConditionXmlTest.class,
    CondLoadTest.class,
    CondLoadZipTest.class,
    LobManagerTest.class,
    PropertiesTest.class,
    PropertyTypeTest.class,
    TablePrinterTest.class
})
public class TestSuite {

}
