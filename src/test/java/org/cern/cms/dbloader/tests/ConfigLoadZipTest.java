package org.cern.cms.dbloader.tests;

import org.cern.cms.dbloader.TestBase;
import org.cern.cms.dbloader.manager.FilesManager;
import org.cern.cms.dbloader.manager.XmlManager;
import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.manager.file.FileBase;
import org.cern.cms.dbloader.model.serial.Root;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

/**
 * Created by Aivaras on 5/23/17.
 */

public class ConfigLoadZipTest extends TestBase{

    @Test
    public void loadConfigZipTest() throws Exception {

        XmlManager xmlm = injector.getInstance(XmlManager.class);
        FilesManager fm = injector.getInstance(FilesManager.class);
        for(FileBase fb : fm.getFiles(Collections.singletonList("src/test/zip/config-test.zip"))) {

            Assert.assertEquals(6, fb.getDataFiles().size());

            for (DataFile df: fb.getDataFiles()) {
                Root root = xmlm.unmarshal(df.getFile());
                boolean loaded = false;
                switch (df.getType()) {
                    case PART:
                        Assert.assertNull(root.getDatasets());
                        Assert.assertNull(root.getMaps());
                        Assert.assertNull(root.getChannelUpdates());
                        Assert.assertNull(root.getHeader());
                        Assert.assertNull(root.getElements());
                        Assert.assertNotNull(root.getParts());
                        break;
                    case CHANNEL:
                        Assert.assertNull(root.getDatasets());
                        Assert.assertNull(root.getMaps());
                        Assert.assertNull(root.getHeader());
                        Assert.assertNull(root.getElements());
                        Assert.assertNull(root.getParts());
                        Assert.assertNotNull(root.getChannelUpdates());
                        break;
                    case CONDITION:
                        Assert.assertNull(root.getMaps());
                        Assert.assertNull(root.getParts());
                        Assert.assertNull(root.getChannelUpdates());
                        if (root.getHeader() == null & root.getDatasets() == null && root.getElements() == null){
                            Assert.fail("Did not find anything like HEADER, DATASET or ELEMENTS as expected");
                        }
                        break;
                    case VERSION_ALIAS:
                        //check other roots
                        Assert.assertNull(root.getDatasets());
                        Assert.assertNull(root.getHeader());
                        Assert.assertNull(root.getElements());
                        Assert.assertNull(root.getParts());

                        //check what is inside Maps
                        Assert.assertNull(root.getMaps().getKey());
                        Assert.assertNull(root.getMaps().getKeyAlias());
                        Assert.assertNull(root.getMaps().getTags());
                        Assert.assertNotNull(root.getMaps());
                        Assert.assertNotNull(root.getMaps().getVersionAliases());
                        break;
                    case KEY:
                        //check other roots
                        Assert.assertNull(root.getDatasets());
                        Assert.assertNull(root.getHeader());
                        Assert.assertNull(root.getElements());
                        Assert.assertNull(root.getParts());

                        //check what is inside Maps
                        Assert.assertNull(root.getMaps().getKeyAlias());
                        Assert.assertNull(root.getMaps().getTags());
                        Assert.assertNotNull(root.getMaps().getKey());
                        Assert.assertNull(root.getMaps().getVersionAliases());
                        break;
                    case KEY_ALIAS:
                        //check other roots
                        Assert.assertNull(root.getDatasets());
                        Assert.assertNull(root.getHeader());
                        Assert.assertNull(root.getElements());
                        Assert.assertNull(root.getParts());

                        //check what is inside Maps
                        Assert.assertNull(root.getMaps().getVersionAliases());
                        Assert.assertNull(root.getMaps().getTags());
                        Assert.assertNull(root.getMaps().getKey());
                        Assert.assertNotNull(root.getMaps());
                        Assert.assertNotNull(root.getMaps().getKeyAlias());
                        break;
                }
            }
        }

    }
}
