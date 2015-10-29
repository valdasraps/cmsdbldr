package org.cern.cms.dbloader.manager;

import java.io.PrintStream;
import java.util.List;

import org.cern.cms.dbloader.metadata.ChannelEntityHandler;
import org.cern.cms.dbloader.metadata.CondEntityHandler;
import org.cern.cms.dbloader.metadata.EntityHandler;
import org.cern.cms.dbloader.metadata.PropertyHandler;
import org.cern.cms.dbloader.model.condition.Dataset;
import org.cern.cms.dbloader.util.TableBuilder;

public class HelpPrinter {

    public static final void outputConditionList(PrintStream out, CondManager mdm) {
        TableBuilder tb = new TableBuilder();
        for (CondEntityHandler ceh : mdm.getConditionHandlers()) {
            tb.startRow()
                .col(ceh.getId())
                .col(ceh.getName())
                .col(ceh.getTableName())
                .col(ceh.getClassName())
                .col(ceh.getProperties().size());
        }
        tb.startFirstRow()
                .colTitle("ID")
                .colTitle("Title")
                .colTitle("ORACLE table")
                .colTitle("JAVA class")
                .colTitle("# props")
                .print(out);
    }

    public static final void outputConditionDatasets(PrintStream out, List<Dataset> dsa) {
        TableBuilder tb = new TableBuilder();
        for (Dataset d : dsa) {
            String partName = null;
            String kopName = null;
            if (d.getPart() != null) {
                partName = d.getPart().getName();
                kopName = d.getPart().getKindOfPart().getName();
            }
            tb.startRow()
                    .col(d.getId())
                    .col(d.getVersion())
                    .col(partName)
                    .col(kopName)
                    .col(d.getRun().getName())
                    .col(d.getRun().getNumber())
                    .col(d.getRun().getRunType());
        }
        tb
                .startFirstRow()
                .colTitle("ID")
                .colTitle("Version")
                .colTitle("Part")
                .colTitle("Kind of part")
                .colTitle("Run")
                .colTitle("Number")
                .colTitle("Runtime")
                .print(out);
    }

    public static final void outputConditionDesc(PrintStream out, String condName, EntityHandler<?> tmd) {
        TableBuilder tb = new TableBuilder();
        tb.startRow()
                .col(condName)
                .col(tmd.getTableName())
                .col(tmd.getClassName())
                .col(tmd.getProperties().size())
                .startFirstRow()
                .colTitle("Title")
                .colTitle("ORACLE table")
                .colTitle("JAVA class")
                .colTitle("# props")
                .print(System.out);

        tb = new TableBuilder();
        for (PropertyHandler cmd : tmd.getProperties()) {
            tb.startRow()
                    .col(cmd.getColumnName())
                    .col(cmd.getTypeName())
                    .col(cmd.getName())
                    .col(cmd.getClassName());
        }
        tb.startFirstRow()
                .colTitle("ORACLE column")
                .colTitle("ORACLE type")
                .colTitle("JAVA property")
                .colTitle("JAVA type")
                .print(out);
    }

    public static final void outputChannelList(PrintStream out, CondManager mdm) {
        TableBuilder tb = new TableBuilder();
        for (ChannelEntityHandler ceh : mdm.getChannelHandlers()) {
            tb.startRow()
                    .col(ceh.getTableName())
                    .col(ceh.getClassName())
                    .col(ceh.getProperties().size());
        }
        tb.startFirstRow()
                .colTitle("ORACLE table")
                .colTitle("JAVA class")
                .colTitle("# props")
                .print(out);
    }

    public static final void outputDatasetList(PrintStream out, List<Dataset> datasets) {
        TableBuilder tb = new TableBuilder();
        for (Dataset ds : datasets) {
            tb.startRow()
                    .col(ds.getId())
                    .col(ds.getInsertTime())
                    .col(ds.getInsertUser());
        }
        tb.startFirstRow()
                .colTitle("ID")
                .colTitle("Insert time")
                .colTitle("User")
                .print(out);
    }

}
