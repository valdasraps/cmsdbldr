package org.cern.cms.dbloader.tests;

import junit.framework.TestCase;
import org.cern.cms.dbloader.DbLoader;
import org.cern.cms.dbloader.TestBase;
import org.cern.cms.dbloader.manager.FilesManager;
import org.cern.cms.dbloader.manager.HbmManager;
import org.cern.cms.dbloader.manager.file.FileBase;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.condition.Run;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

public class CondAutoRunLoadTest extends TestBase {

    private BigInteger getMaxRun(String runType) throws Throwable {
        try (HbmManager hbm = injector.getInstance(HbmManager.class)) {
            Session session = hbm.getSession();
            try {

                return (BigInteger) session.createCriteria(Run.class)
                        .add(Restrictions.eq("runType", runType))
                        .add(Restrictions.eq("deleted", Boolean.FALSE))
                        .add(Restrictions.isNotNull("number"))
                        .setProjection(Projections.max("number"))
                        .uniqueResult();

            } finally {
                session.close();
            }
        }
    }

    @Test
    public void loadConditionRunAutoNumber() throws Throwable {

        FilesManager fm = injector.getInstance(FilesManager.class);
        DbLoader loader = new DbLoader(pm);

        BigInteger maxRunNumber = getMaxRun("SOME_AUTO_NUMBER");

        for (FileBase fb: fm.getFiles(Collections.singletonList("src/test/xml/21_auto_run_number_condition.xml"))) {
            loader.loadArchive(injector, fb, pm.getOperatorAuth());
        }

        if (maxRunNumber == null) {
            maxRunNumber = BigInteger.ONE;
        } else {
            maxRunNumber = maxRunNumber.add(BigInteger.ONE);
        }

        TestCase.assertTrue(maxRunNumber.equals(getMaxRun("SOME_AUTO_NUMBER")));

        for (FileBase fb: fm.getFiles(Collections.singletonList("src/test/xml/21_auto_run_number_condition.xml"))) {
            loader.loadArchive(injector, fb, pm.getOperatorAuth());
        }

        maxRunNumber = maxRunNumber.add(BigInteger.ONE);
        TestCase.assertTrue(maxRunNumber.equals(getMaxRun("SOME_AUTO_NUMBER")));

        for (FileBase fb: fm.getFiles(Collections.singletonList("src/test/xml/22_same_run_number_condition.xml"))) {
            loader.loadArchive(injector, fb, pm.getOperatorAuth());
        }

        TestCase.assertTrue(maxRunNumber.equals(getMaxRun("SOME_AUTO_NUMBER")));

    }

    @Test
    public void loadConditionRunSeqNumber() throws Throwable {

        FilesManager fm = injector.getInstance(FilesManager.class);
        DbLoader loader = new DbLoader(pm);

        BigInteger maxRunNumber = getMaxRun("SOME_SEQ_NUMBER");

        for (FileBase fb: fm.getFiles(Collections.singletonList("src/test/xml/23_sequence_run_number_condition.xml"))) {
            loader.loadArchive(injector, fb, pm.getOperatorAuth());
        }

        if (maxRunNumber == null) {
            maxRunNumber = BigInteger.ONE;
        } else {
            maxRunNumber = maxRunNumber.add(BigInteger.ONE);
        }

        TestCase.assertTrue(maxRunNumber.equals(getMaxRun("SOME_SEQ_NUMBER")));

        for (FileBase fb: fm.getFiles(Collections.singletonList("src/test/xml/23_sequence_run_number_condition.xml"))) {
            loader.loadArchive(injector, fb, pm.getOperatorAuth());
        }

        maxRunNumber = maxRunNumber.add(BigInteger.ONE);
        TestCase.assertTrue(maxRunNumber.equals(getMaxRun("SOME_SEQ_NUMBER")));

        for (FileBase fb: fm.getFiles(Collections.singletonList("src/test/xml/23_sequence_run_number_condition.xml"))) {
            loader.loadArchive(injector, fb, pm.getOperatorAuth());
        }

        maxRunNumber = maxRunNumber.add(BigInteger.ONE);
        TestCase.assertTrue(maxRunNumber.equals(getMaxRun("SOME_SEQ_NUMBER")));

    }

}
