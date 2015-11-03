package org.cern.cms.dbloader.tests;

import java.math.BigInteger;
import org.cern.cms.dbloader.TestBase;

import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.condition.Run;
import org.cern.cms.dbloader.model.construct.KindOfPart;
import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.util.TableBuilder;
import org.junit.Test;

public class TablePrinterTest extends TestBase {

    @Test
    public void test() {

        final Dataset[] DATASETS = new Dataset[]{
            (Dataset) newDataset(new BigInteger("1"), "v1", "myPart", "myKind", "myRun"),
            (Dataset) newDataset(new BigInteger("2"), "v1", "myPart", "myKind", "myRun"),
            (Dataset) newDataset(new BigInteger("3"), "v1", "myPart", "myKind", "myRun"),
            (Dataset) newDataset(new BigInteger("4"), "v1", "myPart", "myKind", "myRun")
        };
        
        TableBuilder tp = new TableBuilder();

        for (Dataset d : DATASETS) {
            tp.startRow()
                    .col(d.getId())
                    .col(d.getVersion())
                    .col(d.getPart().getName() + "/" + d.getPart().getKindOfPart().getName())
                    .col(d.getRun().getName());
        }

        tp
                .startFirstRow()
                .colTitle("ID")
                .colTitle("Version")
                .colTitle("Part")
                .colTitle("Run")
                .print(System.out);

    }

    private Object newDataset(BigInteger id, String version, String partName, String kopName, String runName) {
        Dataset d = new Dataset();
        d.setId(id);
        d.setVersion(version);

        Part part = new Part();
        part.setName(partName);

        KindOfPart kop = new KindOfPart();
        kop.setName(kopName);
        part.setKindOfPart(kop);

        d.setPart(part);

        Run run = new Run();
        run.setName(runName);

        d.setRun(run);
        return d;
    }

}
