package org.cern.cms.dbloader;

import com.sun.org.omg.SendingContext.CodeBase;
import junit.framework.TestCase;
import org.cern.cms.dbloader.app.PartApp;
import org.cern.cms.dbloader.dao.PartDao;
import org.cern.cms.dbloader.handler.AuditLogHandler;
import org.cern.cms.dbloader.manager.*;
import org.cern.cms.dbloader.manager.file.ArchiveFile;
import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.manager.xml.CondJsonManager;
import org.cern.cms.dbloader.metadata.ChannelEntityHandler;
import org.cern.cms.dbloader.metadata.CondEntityHandler;
import org.cern.cms.dbloader.metadata.EntityClass;
import org.cern.cms.dbloader.model.condition.*;
import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.construct.PartTree;
import org.cern.cms.dbloader.model.iov.Iov;
import org.cern.cms.dbloader.model.iov.Tag;
import org.cern.cms.dbloader.model.serial.Elements;
import org.cern.cms.dbloader.model.serial.Header;
import org.cern.cms.dbloader.model.serial.Hint;
import org.cern.cms.dbloader.model.serial.Root;
import org.cern.cms.dbloader.model.serial.map.*;
import org.cern.cms.dbloader.model.serial.part.PartAssembly;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Attr;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
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
        iov.setIovBegin(new BigInteger("1"));
        iov.setIovEnd(new BigInteger("-1"));

        Tag tag = new Tag();
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

        String json = this.jmanager.serialiaze(root);
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

        String json = this.jmanager.serialiaze(root);
        System.out.println(json);

    }

    /*
        Serializing Part bean to JSON string case2

        IMPORTANT! Root fields that annotated with @Transient
        filled by hand
    */
    @Ignore
    public void testBeanToJsonStringCase2() throws Throwable {
        // TODO
    }


    /*
        Test JSON case0 map to Part class
     */
    @Test
    public void testJsonToBeanCase0() throws ParseException, IOException {

        // Root root = this.jmanager.deserialize(new File(case0)); // paprastas deserializatorius
        CondManager condMngr = null;
        try {
            condMngr = new CondManager(pm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ChannelEntityHandler chaneh = condMngr.getChannelHandler(root.getHeader().getHint().getChannelMap());
        ChannelEntityHandler chaneh = condMngr.getChannelHandler("TEST_CHANNELS");
        CondEntityHandler condeh = condMngr.getConditionHandler("IV");

        CondJsonManager jmanager = new CondJsonManager(chaneh, condeh);

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
        assertEquals(DATE_FORMAT.parse("2015-11-03 19:00:00"), run.getBeginTime());
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
        assertEquals("Some Test Tag", tag.getName());
        assertEquals("TEST", tag.getDetectorName());
        assertEquals("This is some comment", tag.getComment());

        // Asserting Maps
        Maps maps = root.getMaps();
        Set<MapTag> mapTags = maps.getTags();
        assertEquals(1, mapTags.size());
        MapTag mapTag = mapTags.iterator().next();
        assertEquals(new Integer(3), mapTag.getRefid());
        assertNotNull(mapTag.getIovs());
        Set<MapIov> mapIovs = mapTag.getIovs();
        assertEquals(1, mapIovs.size());
        MapIov mapIov = mapIovs.iterator().next();
        assertEquals(new Integer(2), mapIov.getRefid());
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
        assertEquals(DATE_FORMAT.parse("2011-06-17 00:00:00"), dataset.getCreateTimestamp());
        assertEquals("joshi", dataset.getCreatedByUser());
        assertEquals("JUN_7_2011", dataset.getVersion());
        assertEquals("2", dataset.getSubversion());
        // Channel
        ChannelBase cb = dataset.getChannel();
        assertEquals("org.cern.cms.dbloader.model.condition.ext.TestChannels", cb.getClass().getTypeName());
        // Data
        assertNotNull(dataset.getData());
        List<? extends CondBase> datas = dataset.getData();
        assertEquals(3, datas.size());
        for (int i = 0; i <= 2; i++) {
            assertEquals("org.cern.cms.dbloader.model.condition.ext.TestIv",
                    datas.get(i).getClass().getTypeName());

        }
    }

    /*
        Test JSON case1 map to Part class
     */
    @Test
    public void testJsonToBeanCase1() throws ParseException, IOException {
        CondManager condMngr = null;
        try {
            condMngr = new CondManager(pm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ChannelEntityHandler chaneh = condMngr.getChannelHandler(root.getHeader().getHint().getChannelMap());
        ChannelEntityHandler chaneh = condMngr.getChannelHandler("TEST_CHANNELS");
        CondEntityHandler condeh = condMngr.getConditionHandler("IV");

        CondJsonManager jmanager = new CondJsonManager(chaneh, condeh);

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
        assertEquals("2", run.getNumber());
        assertEquals("1", run.getRunType());
        assertEquals(DATE_FORMAT.parse("2015-11-03 19:00:00"), run.getBeginTime());
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
        assertEquals(DATE_FORMAT.parse("2015-11-03 19:00:00"), dataset.getCreateTimestamp());
        assertEquals("apoluden", dataset.getCreatedByUser());
        assertEquals("JUL_16_2011", dataset.getVersion());
        assertEquals("3", dataset.getSubversion());
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
                    datas.get(i).getClass().getTypeName());

        }
    }

    /*
        Test JSON case2 map to Part class
     */
    @Test
    public void testJsonToBeanCase2() throws IOException {
    }

    /*
        Test JSON case0 data upload to DB
     */
    @Ignore
    public void testJsonCase0UploadToDb() throws Exception {
    }

    /*
        Test JSON case1 data upload to Db
     */
    @Ignore
    public void testJsonCase1UploadToDb() throws Exception {

    }

    /*
        Test JSON case2 data upload to Db

        Before loading part KindOfPart
        ATTR_CATALOG, ATTR_BASES
     */
    @Test
    public void testJsonCase2UploadToDb() throws Exception {

    }
}
