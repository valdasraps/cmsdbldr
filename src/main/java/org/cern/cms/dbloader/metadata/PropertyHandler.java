package org.cern.cms.dbloader.metadata;

import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;

import lombok.Getter;
import lombok.ToString;


@Getter
@ToString(callSuper=false, of={"columnName","typeName","className"})
public class PropertyHandler {
	
	private final String name;
	private final String columnName;
	private final String typeName;
	private final String className;
	private final boolean temporal;
	private final boolean lob;
		
	public PropertyHandler(ResultSetMetaData md, int i) throws SQLException {
		this.columnName = md.getColumnName(i);
		String tn = md.getColumnTypeName(i);
		
		if (tn.contains("TIMESTAMP")) {
			this.className = "java.util.Date";
			this.temporal = true;
			this.lob = false;
		} else {
			this.temporal = false;
			if (tn.equals("CLOB")) {
				this.className = "java.lang.String";
				this.lob = true;
			} else {
				this.className = md.getColumnClassName(i);
				this.lob = false;
			}
		}

		this.typeName = tn;
		this.name = EntityFactory.getJavaName(columnName);
	}
	
	public Class<?> getPropertyClass() throws ClassNotFoundException {
		return Class.forName(className);
	}
	
	public Object getSampleData() throws Exception {
		Class<?> clazz = getPropertyClass();
		if (clazz.equals(String.class)) {
			return "String value";
		}
		if (clazz.equals(Integer.class)) {
			return 155;
		}
		if (clazz.equals(Double.class) || clazz.equals(BigDecimal.class)) {
			return 155.55D;
		}
		if (clazz.equals(Date.class)) {
			return new Date();
		}
		return null;
	}
	
}