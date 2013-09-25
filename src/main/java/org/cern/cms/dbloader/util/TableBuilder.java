package org.cern.cms.dbloader.util;

import java.io.PrintStream;
import java.util.ArrayList;

public class TableBuilder {
	
	private ArrayList<ArrayList<Object>> table = new ArrayList<>();

	public TableBuilder startRow() {
		table.add(new ArrayList<Object>());
		return this;
	}
	
	public TableBuilder startFirstRow() {
		table.add(0, new ArrayList<Object>());
		return this;
	}
	
	public TableBuilder colTitle(Object title) {
		table.get(0).add(title);
		return this;
	}
	
	public TableBuilder col(Object data) {
		table.get(table.size()-1).add(data);
		return this;
	}
	
	public void print(PrintStream dest) {
		TablePrinter tp = new TablePrinter(dest);
		
		final int listSize = table.size();
		String[][] darr = new String[listSize][];
		for(int i = 0; i < listSize; i++) {
		    ArrayList<Object> sublist = table.get(i);
		    final int sublistSize = sublist.size();
		    darr[i] = new String[sublistSize];
		    for(int j = 0; j < sublistSize; j++) {
		        darr[i][j] = String.valueOf(sublist.get(j));
		    }
		}

		tp.print(darr);
	}
	
}
