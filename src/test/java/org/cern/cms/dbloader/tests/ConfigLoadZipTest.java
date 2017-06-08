package org.cern.cms.dbloader.tests;

import org.cern.cms.dbloader.DbLoader;
import org.cern.cms.dbloader.TestBase;
import org.cern.cms.dbloader.manager.FilesManager;
import org.cern.cms.dbloader.manager.XmlManager;
import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.manager.file.FileBase;
import org.cern.cms.dbloader.model.xml.Root;
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
        for(FileBase fb : FilesManager.getFiles(Collections.singletonList("src/test/zip/config-test.zip"))) {

            Assert.assertEquals(6, fb.getDataFiles().size());

            for (DataFile df: fb.getDataFiles()) {
                Root root = xmlm.unmarshal(df.getFile());
                boolean loaded = false;
                switch (df.getType()) {
                    case 0:
                        Assert.assertNull(root.getDatasets());
                        Assert.assertNull(root.getMaps());
                        Assert.assertNull(root.getChannelUpdates());
                        Assert.assertNull(root.getHeader());
                        Assert.assertNull(root.getElements());
                        Assert.assertNotNull(root.getParts());
                        break;
                    case 1:
                        Assert.assertNull(root.getDatasets());
                        Assert.assertNull(root.getMaps());
                        Assert.assertNull(root.getHeader());
                        Assert.assertNull(root.getElements());
                        Assert.assertNull(root.getParts());
                        Assert.assertNotNull(root.getChannelUpdates());
                        break;
                    case 2:
                        Assert.assertNull(root.getMaps());
                        Assert.assertNull(root.getParts());
                        Assert.assertNull(root.getChannelUpdates());
                        break;
                    case 3:
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
                        Assert.assertNotNull(root.getMaps().getVersionAlias());
                        break;
                    case 4:
                        //check other roots
                        Assert.assertNull(root.getDatasets());
                        Assert.assertNull(root.getHeader());
                        Assert.assertNull(root.getElements());
                        Assert.assertNull(root.getParts());

                        //check what is inside Maps
                        Assert.assertNull(root.getMaps().getKeyAlias());
                        Assert.assertNull(root.getMaps().getTags());
                        Assert.assertNotNull(root.getMaps().getKey());
                        Assert.assertNull(root.getMaps().getVersionAlias());
                        break;
                    case 5:
                        //check other roots
                        Assert.assertNull(root.getDatasets());
                        Assert.assertNull(root.getHeader());
                        Assert.assertNull(root.getElements());
                        Assert.assertNull(root.getParts());

                        //check what is inside Maps
                        Assert.assertNull(root.getMaps().getVersionAlias());
                        Assert.assertNull(root.getMaps().getTags());
                        Assert.assertNull(root.getMaps().getKey());
                        Assert.assertNotNull(root.getMaps());
                        Assert.assertNotNull(root.getMaps().getKeyAlias());
                        break;
                    case 999:
                        //should fail here if found type numb 999,
                        // it means that FileTypeManager can't recognise file type.
                        Assert.assertNull(root);
                    default:
                        break;
                }
            }
        }

    }
}
