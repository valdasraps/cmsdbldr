package org.cern.cms.dbloader.tests;

import junit.framework.TestCase;
import org.cern.cms.dbloader.DbLoader;
import org.cern.cms.dbloader.TestBase;
import org.cern.cms.dbloader.manager.*;
import org.cern.cms.dbloader.manager.file.FileBase;
import org.cern.cms.dbloader.metadata.ChannelEntityHandler;
import org.cern.cms.dbloader.metadata.CondEntityHandler;
import org.cern.cms.dbloader.model.condition.*;
import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.iov.Iov;
import org.cern.cms.dbloader.model.iov.Tag;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.serial.Elements;
import org.cern.cms.dbloader.model.serial.Header;
import org.cern.cms.dbloader.model.serial.Hint;
import org.cern.cms.dbloader.model.serial.Root;
import org.cern.cms.dbloader.model.serial.map.*;
import org.cern.cms.dbloader.model.serial.part.PartAssembly;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CondLoadJsonTest extends TestBase {

    JsonManager jmanager;
    // case0
    String case0 = "src/test/json/02_condition.json";
    // case1
    String case1 = "src/test/json/03_condition.json";
    // case2
    String case2 = "src/test/json/04_condition.json";

    @Before
    public void setUp() {
        this.jmanager = new JsonManager();
    }

    /*
        Serializing Part bean to JSON string case0

        IMPORTANT! Root fields that annotated with @Transient
        filled by hand

        Channel and Data properties filled manualy
    */
    @Ignore
    public void testBeanToJsonCase0() throws Throwable {

        Integer val = 1;
        Root root = new Root();

        Header header = new Header();

        // Hint
        Hint hint = new Hint();
        hint.setChannelMap("TEST_CHANNELS");

        // Type
        KindOfCondition koc = new KindOfCondition();
        koc.setExtensionTable("TEST_IV");
        koc.setName("IV");

        //Run
        Run run = new Run();
        run.setNumber("1");
        run.setRunType("1");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = sdf.parse("2015-11-03 19:00:00");
        run.setBeginTime(d);

        // Header SetUp
        header.setHint(hint);
        header.setKindOfCondition(koc);
        header.setRun(run);

        // Elements
        Elements elements = new Elements();

        // Dataset
        Dataset dataset = new Dataset();
        dataset.setId(new BigInteger("-1"));

        // Iov
        Iov iov = new Iov();
        iov.setId(new BigInteger("1"));
        iov.setIovBegin(new BigInteger("1"));
        iov.setIovEnd(new BigInteger("-1"));

        Tag tag = new Tag();
        tag.setId(new BigInteger("2"));
        tag.setName("Some Test Tag");
        tag.setDetectorName("TEST");
        tag.setComment("This is some comment");

        // Elements setUp
        elements.setTags(new HashSet<Tag>() {{
            add(tag);
        }});
        elements.setIovs(new HashSet<Iov>() {{
            add(iov);
        }});
        elements.setDatasets(new HashSet<Dataset>() {{
            add(dataset);
        }});

        // Maps
        Maps maps = new Maps();

        MapDataset mapDataset = new MapDataset();
        mapDataset.setRefid(-1);
        MapIov mapIov = new MapIov();
        mapIov.setRefid(2);
        mapIov.setDatasets(new HashSet<MapDataset>() {{
            add(mapDataset);
        }});
        MapTag mapTag = new MapTag();
        mapTag.setRefid(3);
        mapTag.setIovs(new HashSet<MapIov>() {{
            add(mapIov);
        }});
        // Maps setUp
        maps.setTags(new HashSet<MapTag>() {{
            add(mapTag);
        }});

        // Dataset
        Dataset ds = new Dataset();
        ds.setComment("Any comment");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf1.parse("2011-06-17 00:00:00");
        ds.setCreateTimestamp(date);
        ds.setCreatedByUser("joshi");
        ds.setVersion("JUN_7_2011");
        ds.setSubversion("2");

        // Root setUp
        root.setHeader(header);
        root.setElements(elements);
        root.setMaps(maps);
        root.setDatasets(new ArrayList<Dataset>() {{
            add(ds);
        }});

        String json = this.jmanager.serialize(root);
        System.out.println(json);
    }

    /*
        Serializing Part bean to JSON string case1

        IMPORTANT! Root fields that annotated with @Transient
        filled by hand

        Channel and Data properties filled manualy

    */
    @Ignore
    public void testBeanToJsonStringCase1() throws Throwable {
        Root root = new Root();

        Header header = new Header();
        // Hint
        Hint hint = new Hint();
        hint.setChannelMap("TEST_CHANNELS");

        // Type
        KindOfCondition koc = new KindOfCondition();
        koc.setExtensionTable("TEST_IV");
        koc.setName("IV");

        //Run
        Run run = new Run();
        run.setNumber("2");
        run.setRunType("1");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = sdf.parse("2015-11-03 19:00:00");
        run.setBeginTime(d);

        // Header SetUp
        header.setHint(hint);
        header.setKindOfCondition(koc);
        header.setRun(run);

        // PartAssembly
        PartAssembly pa = new PartAssembly();
        ChildUnique cu = new ChildUnique();

        Attribute attr = new Attribute();
        attr.setName("TEST Position");
        attr.setValue("3");
        cu.setAttribute(attr);

        Part part = new Part();
        part.setKindOfPartName("TEST Pack");
        part.setSerialNumber("serial 01");
        pa.setUniqueChild(cu);
        pa.setParentPart(part);

        // Dataset
        Dataset ds = new Dataset();
        ds.setComment("Any comment");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf1.parse("2015-11-03 19:00:00");
        ds.setCreateTimestamp(date);
        ds.setCreatedByUser("apoluden");
        ds.setVersion("JUL_16_2011");
        ds.setSubversion("3");
        ds.setPartAssembly(pa);
        // Root setUp
        root.setDatasets(new ArrayList<Dataset>() {{
            add(ds);
        }});
        root.setHeader(header);

        String json = this.jmanager.serialize(root);
        System.out.println(json);

    }

    /*
        Serializing Part bean to JSON string case2

        IMPORTANT! Root fields that annotated with @Transient
        filled by hand

        Channel and Data properties filled manualy
    */
    @Ignore
    public void testBeanToJsonStringCase2() throws Throwable {
        Root root = new Root();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Header header = new Header();

        // Hint
        Hint hint = new Hint();
        // Type
        KindOfCondition koc = new KindOfCondition();
        koc.setExtensionTable("TEST_IV");
        koc.setName("IV");

        //Run
        Run run = new Run();
        run.setNumber("164654");
        run.setRunType("LOCAL-RUN");
        Date d = sdf.parse("2009-01-01 00:00:00");
        run.setBeginTime(d);
        run.setComment("TEST LED CALIB data");
        run.setLocation("P5");
        run.setInitiatedByUser("dma");

        // Header SetUp
        header.setHint(hint);
        header.setKindOfCondition(koc);
        header.setRun(run);

        // Dataset
        Dataset ds = new Dataset();
        ds.setSetNumber(new Long(1));
        ds.setSetBeginTime(sdf.parse("2009-01-01 00:00:00"));
        ds.setSetEndTime(sdf.parse("2009-01-01 00:00:00"));
        ds.setEventsInSet(new Long(2000));
        ds.setComment("Automatic DQM output");
        ds.setDataFilename("HcalDetDiagLEDCalib_164654_1.xml");
        ds.setImageFilename("data plot url or file path");
        ds.setSetBeginTime(sdf.parse("2011-05-16 12:09:28"));
        ds.setCreatedByUser("dma");
        ds.setVersion("1646541");
        ds.setSubversion("1");

        Attribute attr = new Attribute();
        attr.setName("TEST Dataset status");
        attr.setValue("VALID");
        ds.setAttributes(new ArrayList<Attribute>() {{
            add(attr);
        }});

        // Root setUp
        root.setHeader(header);
        root.setDatasets(new ArrayList<Dataset>() {{
            add(ds);
        }});

        String json = this.jmanager.serialize(root);
        System.out.println(json);
    }


    /*
        Test JSON case0 map to Part class
     */
    @Test
    public void testJsonToBeanCase0() throws Exception {

        // Root root = this.jmanager.deserialize(new File(case0)); // paprastas deserializatorius
        DynamicEntityGenerator enGenerator = new DynamicEntityGenerator(pm);

        // ChannelEntityHandler chaneh = enGenerator.getChannelHandler(root.getHeader().getHint().getChannelMap());
        ChannelEntityHandler chaneh = enGenerator.getChannelHandler("TEST_CHANNELS");
        CondEntityHandler condeh = enGenerator.getConditionHandler("IV");

        JsonManager jmanager = new JsonManager();
        Root root = jmanager.deserialize(new File(case0));
        assertNotNull(root.getHeader());
        assertNotNull(root.getElements());
        assertNotNull(root.getMaps());
        assertNotNull(root.getDatasets());

        // Asserting Header
        Header header = root.getHeader();
        assertNotNull(header.getKindOfCondition());
        // Type
        KindOfCondition koc = header.getKindOfCondition();
        assertEquals("TEST_IV", koc.getExtensionTable());
        assertEquals("IV", koc.getName());
        assertNotNull(header.getRun());
        // Run
        Run run = header.getRun();
        assertEquals("1", run.getNumber());
        assertEquals("1", run.getRunType());
        assertEquals(DATE_FORMAT.parse("2015-11-03 19:00:00 GMT"), run.getBeginTime());
        // Hint
        assertNotNull(header.getHint());
        Hint hint = header.getHint();
        assertEquals("TEST_CHANNELS", hint.getChannelMap());

        // Asserting Elements
        Elements elements = root.getElements();
        // Dataset
        assertNotNull(elements.getDatasets());
        Set<Dataset> dsSet = elements.getDatasets();
        assertEquals(1, dsSet.size());
        Dataset ds = dsSet.iterator().next();
        assertEquals(new BigInteger("-1"), ds.getId());
        // Iov
        assertNotNull(elements.getTags());
        Set<Tag> tags =  elements.getTags();
        assertEquals(1, tags.size());
        Tag tag = tags.iterator().next();
        assertEquals("Any Tag", tag.getName());
        assertEquals("TEST", tag.getDetectorName());
        assertEquals("This is tag comment", tag.getComment());

        // Asserting Maps
        Maps maps = root.getMaps();
        Set<MapTag> mapTags = maps.getTags();
        assertEquals(1, mapTags.size());
        MapTag mapTag = mapTags.iterator().next();
        assertEquals(new Integer(2), mapTag.getRefid());
        assertNotNull(mapTag.getIovs());
        Set<MapIov> mapIovs = mapTag.getIovs();
        assertEquals(1, mapIovs.size());
        MapIov mapIov = mapIovs.iterator().next();
        assertEquals(new Integer(1), mapIov.getRefid());
        assertNotNull(mapIov.getDatasets());
        Set<MapDataset> datasets = mapIov.getDatasets();
        assertEquals(1, datasets.size());
        MapDataset mapDataset = datasets.iterator().next();
        assertEquals( new Integer(-1), mapDataset.getRefid());

        // Asserting Dataset
        List<Dataset>  datasetList = root.getDatasets();
        assertNotNull(datasetList);
        assertEquals(1, datasetList.size());
        Dataset dataset = datasetList.get(0);
        assertEquals("Any comment", dataset.getComment());
        assertEquals(DATE_FORMAT.parse("2011-06-17 00:00:00 GMT"), dataset.getCreateTimestamp());
        assertEquals("apoluden", dataset.getCreatedByUser());
        assertEquals("AUG_20_2018", dataset.getVersion());
        assertEquals("2", dataset.getSubversion());
        
        // Channel
        ChannelBase cb = dataset.getChannel().getDelegate(chaneh.getEntityClass().getC());
        assertEquals("org.cern.cms.dbloader.model.condition.ext.TestChannels", cb.getClass().getTypeName());
        
        // Data
        assertNotNull(dataset.getData());
        List<? extends CondBase> datas = dataset.getData();
        assertEquals(3, datas.size());
        for (int i = 0; i <= 2; i++) {
            CondBase d = datas.get(i).getDelegate(condeh.getEntityClass().getC());
            assertEquals("org.cern.cms.dbloader.model.condition.ext.TestIv", d.getClass().getTypeName());

        }
    }

    /*
        Test JSON case1 map to Part class
     */
    @Test
    public void testJsonToBeanCase1() throws Exception {
        DynamicEntityGenerator enGenerator = null;
        try {
            enGenerator = new DynamicEntityGenerator(pm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ChannelEntityHandler chaneh = enGenerator.getChannelHandler(root.getHeader().getHint().getChannelMap());
        ChannelEntityHandler chaneh = enGenerator.getChannelHandler("TEST_CHANNELS");
        CondEntityHandler condeh = enGenerator.getConditionHandler("IV");

        JsonManager jmanager = new JsonManager();

        Root root = jmanager.deserialize(new File(case1));

        // Asserting Header
        Header header = root.getHeader();
        assertNotNull(header.getKindOfCondition());
        // Type
        KindOfCondition koc = header.getKindOfCondition();
        assertEquals("TEST_IV", koc.getExtensionTable());
        assertEquals("IV", koc.getName());
        assertNotNull(header.getRun());
        // Run
        Run run = header.getRun();
        assertEquals("1", run.getNumber());
        assertEquals("1", run.getRunType());
        assertEquals(DATE_FORMAT.parse("2018-08-20 19:00:00 GMT"), run.getBeginTime());
        // Hint
        assertNotNull(header.getHint());
        Hint hint = header.getHint();
        assertEquals("TEST_CHANNELS", hint.getChannelMap());

        // Asserting Dataset
        List<Dataset>  datasetList = root.getDatasets();
        assertNotNull(datasetList);
        assertEquals(1, datasetList.size());
        Dataset dataset = datasetList.get(0);
        assertEquals("Any comment", dataset.getComment());
        assertEquals(DATE_FORMAT.parse("2018-08-20 19:00:00 GMT"), dataset.getCreateTimestamp());
        assertEquals("apoluden", dataset.getCreatedByUser());
        assertEquals("AUG_21_2018", dataset.getVersion());
        assertEquals("1", dataset.getSubversion());
        // PartAssembly
        assertNotNull(dataset.getPartAssembly());
        PartAssembly pa = dataset.getPartAssembly();
        assertNotNull(pa.getParentPart());
        Part part = pa.getParentPart();
        assertEquals("TEST Pack" , part.getKindOfPartName());
        assertEquals("serial 01", part.getSerialNumber());
        assertNotNull(pa.getUniqueChild());
        ChildUnique cu = pa.getUniqueChild();
        assertNotNull(cu.getAttribute());
        Attribute attr = cu.getAttribute();
        assertEquals("TEST Position", attr.getName());
        assertEquals("3", attr.getValue());

        assertNotNull(dataset.getData());
        List<? extends CondBase> datas = dataset.getData();
        assertEquals(3, datas.size());
        for (int i = 0; i <= 2; i++) {
            assertEquals("org.cern.cms.dbloader.model.condition.ext.TestIv",
                    datas.get(i).getDelegate(condeh.getEntityClass().getC()).getClass().getTypeName());

        }
    }

    /*
        Test JSON case2 map to Part class
     */
    @Test
    public void testJsonToBeanCase2() throws Exception {
        DynamicEntityGenerator enGenerator = null;
        try {
            enGenerator = new DynamicEntityGenerator(pm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ChannelEntityHandler chaneh = enGenerator.getChannelHandler(root.getHeader().getHint().getChannelMap());
        ChannelEntityHandler chaneh = enGenerator.getChannelHandler("TEST_CHANNELS");
        CondEntityHandler condeh = enGenerator.getConditionHandler("IV");

        JsonManager jmanager = new JsonManager();
        Root root = jmanager.deserialize(new File(case2));

        // Asserting Header
        Header header = root.getHeader();
        assertNotNull(header.getKindOfCondition());
        // Type
        KindOfCondition koc = header.getKindOfCondition();
        assertEquals("TEST_IV", koc.getExtensionTable());
        assertEquals("IV", koc.getName());
        assertNotNull(header.getRun());
        // Run
        Run run = header.getRun();
        assertEquals("164654", run.getNumber());
        assertEquals("LOCAL-RUN", run.getRunType());
        assertEquals(DATE_FORMAT.parse("2009-01-01 00:00:00 GMT"), run.getBeginTime());
        assertEquals("LOCAL-RUN", run.getRunType());
        assertEquals("apoluden", run.getInitiatedByUser());
        assertEquals("P5", run.getLocation());
        // Hint
        // Asserting Dataset
        List<Dataset>  datasetList = root.getDatasets();
        assertNotNull(datasetList);
        assertEquals(1, datasetList.size());
        Dataset dataset = datasetList.get(0);
        assertEquals("dataset comment", dataset.getComment());
        assertEquals(DATE_FORMAT.parse("2009-01-01 00:00:00 GMT"), dataset.getSetBeginTime());
        assertEquals(DATE_FORMAT.parse("2009-01-01 00:00:00 GMT"), dataset.getSetEndTime());
        assertEquals("apoluden", dataset.getCreatedByUser());
        assertEquals("AUG_22_2018", dataset.getVersion());
        assertEquals("1", dataset.getSubversion());
        assertEquals("HcalDetDiagLEDCalib_164654_1.xml", dataset.getDataFilename());
        assertEquals("data plot url or file path", dataset.getImageFilename());
        assertEquals(new Long(2000), dataset.getEventsInSet());
        assertEquals(new Long(1), dataset.getSetNumber());
        // PredefinedAttributes
        List<Attribute> attrs = dataset.getAttributes();
        assertNotNull(attrs);
        assertEquals(1, attrs.size());
        Attribute attr = attrs.get(0);
        assertEquals("TEST Dataset status", attr.getName());
        assertEquals("VALID", attr.getValue());
        // Data
        assertNotNull(dataset.getData());
        List<? extends CondBase> datas = dataset.getData();
        assertEquals(3, datas.size());

        for (int i = 0; i <= 2; i++) {
            assertEquals("org.cern.cms.dbloader.model.condition.ext.TestIv",
                    datas.get(i).getDelegate(condeh.getEntityClass().getC()).getClass().getTypeName());
        }
    }

    @Test
    public void testJsonUploadToDb() throws Throwable {

        FilesManager fm = injector.getInstance(FilesManager.class);

        String [] files = new String [] { case0, case1, case2 };

        DbLoader loader = new DbLoader(pm);
        for (FileBase fb: fm.getFiles(Arrays.asList(files))) {

            loader.loadArchive(injector, fb);

        }

        try (HbmManager hbm = injector.getInstance(HbmManager.class)) {
            Session session = hbm.getSession();
            try {

                // Check 02_condition.xml

                Dataset ds = (Dataset) session.createCriteria(Dataset.class)
                        .add(Restrictions.eq("version", "AUG_20_2018"))
                        .createCriteria("kindOfCondition")
                        .add(Restrictions.eq("name", "IV"))
                        .uniqueResult();

                assertEquals("TEST_CHANNELS", ds.getChannelMap().getExtensionTableName());
                assertEquals("1", ds.getRun().getNumber());
                assertEquals("1", ds.getRun().getRunType());
                assertEquals("Any comment", ds.getComment());
                assertEquals("apoluden", ds.getCreatedByUser());
                assertEquals("2", ds.getSubversion());
                Iov iov = ds.getIovs().iterator().next();
                assertEquals(new BigInteger("1"), iov.getIovBegin());
                assertEquals(new BigInteger("-1"), iov.getIovEnd());
                Tag tag = iov.getTags().iterator().next();
                assertEquals("Any Tag", tag.getName());
                assertEquals("TEST", tag.getDetectorName());
                assertEquals("This is tag comment", tag.getComment());

                AuditLog alog = (AuditLog) session.createCriteria(AuditLog.class)
                        .add(Restrictions.eq("archiveFileName", "02_condition.json"))
                        .uniqueResult();

                assertEquals("AUG_20_2018", alog.getVersion());
                assertEquals((Integer) 2, alog.getSubversion());
                assertEquals("1", alog.getRunType());
                assertEquals((Integer) 1, alog.getRunNumber());
                assertEquals((Integer) 1, alog.getDatasetCount());
                assertEquals((Integer) 3, alog.getDatasetRecordCount());
                assertEquals(new BigInteger("1"), alog.getIntervalOfValidityBegin());
                assertEquals(new BigInteger("-1"), alog.getIntervalOfValidityEnd());
                assertEquals("Any Tag", alog.getTagName());

                // Check all logs

                for (String file: files) {
                    File f = new File(file);
                    alog = (AuditLog) session.createCriteria(AuditLog.class)
                            .add(Restrictions.eq("archiveFileName", f.getName()))
                            .uniqueResult();

                    TestCase.assertNotNull(alog.getInsertTime());
                    TestCase.assertNotNull(alog.getInsertUser());
                    TestCase.assertNotNull(alog.getLastUpdateTime());
                    TestCase.assertNotNull(alog.getLastUpdateUser());
                    //assertEquals((Integer) 13, alog.getDatasetRecordCount());
                    assertEquals("TEST", alog.getSubdetectorName());
                    assertEquals("IV", alog.getKindOfConditionName());
                    assertEquals("TEST_IV", alog.getExtensionTableName());

                }

            } finally {
                session.close();
            }

        }

    }

}
