package org.cern.cms.dbloader.metadata;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;

import lombok.Getter;
import lombok.ToString;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.cern.cms.dbloader.manager.PropertyType;
import org.cern.cms.dbloader.model.condition.CondBase;


@Getter
@ToString(callSuper=false, of={"columnName","typeName","className"})
public class PropertyHandler {
	
	private final String name;
	private final String columnName;
	private final String typeName;
	private final String className;
	private final PropertyType type;
	
	public PropertyHandler(ResultSetMetaData md, int i) throws SQLException {
		this.columnName = md.getColumnName(i);
		String tn = md.getColumnTypeName(i);
		if (tn.contains("TIMESTAMP")) {
			this.className = "java.util.Date";
			this.type = PropertyType.TEMPORAL;
		} else {
			if (tn.equals("CLOB")) {
				this.className = "java.lang.String";
				this.type = PropertyType.CLOB;
			} else if (tn.equals("BLOB")) {
				this.className = String.valueOf("[B");
				this.type = PropertyType.BLOB;
			} else {
				this.className = md.getColumnClassName(i);
				this.type = PropertyType.OTHER;
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
	
	public <T extends CondBase> Object getValue(T data) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return BeanUtilsBean.getInstance().getPropertyUtils().getProperty(data, this.name);
	}
	
	public <T extends CondBase, V> void setValue(T data,V value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		BeanUtilsBean.getInstance().getPropertyUtils().setProperty(data, this.name, value);
	}
}