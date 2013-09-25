package org.cern.cms.dbloader.util;

import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.model.condition.Run;
import org.cern.cms.dbloader.model.construct.KindOfPart;
import org.cern.cms.dbloader.model.construct.Part;
import org.junit.Test;

public class TablePrinterTest {

	private static final Dataset[] DATASETS = new Dataset[] {
		newDataset(1L, "v1", "myPart", "myKind", "myRun"),
		newDataset(2L, "v1", "myPart", "myKind", "myRun"),
		newDataset(3L, "v1", "myPart", "myKind", "myRun"),
		newDataset(4L, "v1", "myPart", "myKind", "myRun")
	};
	
	@Test
	public void test() {
		
		TableBuilder tp = new TableBuilder();
		
		for (Dataset d: DATASETS) {
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

	private static Dataset newDataset(Long id, String version, String partName, String kopName, String runName) {
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
