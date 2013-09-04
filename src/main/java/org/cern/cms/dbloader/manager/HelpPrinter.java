package org.cern.cms.dbloader.manager;

import java.io.PrintStream;

import org.cern.cms.dbloader.metadata.ChannelEntityHandler;
import org.cern.cms.dbloader.metadata.CondEntityHandler;
import org.cern.cms.dbloader.metadata.EntityHandler;
import org.cern.cms.dbloader.metadata.PropertyHandler;
import org.cern.cms.dbloader.model.condition.CondBase;

public class HelpPrinter {
	
	private static final String DASHES = String.format("%1$-300s", "").replace(' ', '-');
	private static final int COND_LIST_STATIC_WIDTH = 119;
	private static final int COND_DESC_WIDTH = 105;
	
	public static final void outputConditionList(PrintStream out, CondManager mdm) {
		int condWidth = 15;
		for (CondEntityHandler ceh: mdm.getConditionHandlers()) {
			if (ceh.getName().length() > condWidth) {
				condWidth = ceh.getName().length();
			}
		}

		outputConditionListCaption(out, condWidth);
		for (CondEntityHandler ceh: mdm.getConditionHandlers()) {
			outputConditionListItem(out, ceh.getName(), ceh, condWidth);
		}
		outputBreakLine(out, COND_LIST_STATIC_WIDTH + condWidth);
	}

	public static final void outputConditionDesc(PrintStream out, String condName, EntityHandler<?> tmd) {
		
		int condWidth = condName.length(); 
		outputConditionListCaption(out, condWidth);
		outputConditionListItem(out, condName, tmd, condWidth);
		outputBreakLine(out, COND_LIST_STATIC_WIDTH + condWidth);
		
		outputBreakLine(out, COND_DESC_WIDTH);
		out.println(String.format("| %1$-45s| %2$-55s|", "ORACLE column", "JAVA property"));
		outputBreakLine(out, COND_DESC_WIDTH);
		for (PropertyHandler cmd: tmd.getProperties()) {
			out.println(String.format(
					"| %1$-30s%2$-15s| %3$-25s%4$-30s|", 
					cmd.getColumnName(), cmd.getTypeName(), cmd.getClassName(), cmd.getName()));
		}
		outputBreakLine(out, COND_DESC_WIDTH);
		
	}

	private static final void outputConditionListCaption(PrintStream out, int condWidth) {
		outputBreakLine(out, COND_LIST_STATIC_WIDTH + condWidth);
		out.println(String.format(
				"| %1$-" + String.valueOf(condWidth) + "s| %2$-30s| %3$-70s| %4$-10s|", 
				"Title", "ORACLE table", "JAVA class", "# props"));
		outputBreakLine(out, COND_LIST_STATIC_WIDTH + condWidth);
	}
	
	private static final void outputConditionListItem(PrintStream out, String condName, EntityHandler<?> tmd, int condWidth) {
		out.println(String.format(
				"| %1$-" + String.valueOf(condWidth) + "s| %2$-30s| %3$-70s| %4$-10d|", 
				condName,
				tmd.getTableName(),
				tmd.getClassName(),
				tmd.getProperties().size()));
	}
	
	private static final void outputBreakLine(PrintStream out, int size) {
		out.println(DASHES.substring(0, size));
	}

	public static final void outputChannelList(PrintStream out, CondManager mdm) {
		int condWidth = 15;
		for (ChannelEntityHandler ceh: mdm.getChannelHandlers()) {
			if (ceh.getTableName().length() > condWidth) {
				condWidth = ceh.getTableName().length();
			}
		}

		outputConditionListCaption(out, condWidth);
		for (ChannelEntityHandler ceh: mdm.getChannelHandlers()) {
			outputConditionListItem(out, ceh.getTableName(), ceh, condWidth);
		}
		outputBreakLine(out, COND_LIST_STATIC_WIDTH + condWidth);
	}
	
}
