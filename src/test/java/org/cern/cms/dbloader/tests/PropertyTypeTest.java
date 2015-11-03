package org.cern.cms.dbloader.tests;

import org.cern.cms.dbloader.manager.PropertyType;
import static org.junit.Assert.*;

import org.junit.Test;

public class PropertyTypeTest {

    PropertyType property;

    @Test
    public void test1() {

        PropertyTypeTest pt1 = new PropertyTypeTest();
        PropertyTypeTest pt2 = new PropertyTypeTest();

        pt1.property = PropertyType.BLOB;
        pt1.property = PropertyType.OTHER;
        assertNotEquals(pt1.property.ordinal(), 3);
        assertEquals(pt1.property.ordinal(), 0);

        pt2.property = PropertyType.CLOB;
        assertEquals(pt2.property.ordinal(), 2);
    }
}
