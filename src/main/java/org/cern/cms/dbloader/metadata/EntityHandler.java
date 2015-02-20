package org.cern.cms.dbloader.metadata;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(of={"schema", "tableName"})
@EqualsAndHashCode(callSuper=false, of={"schema", "tableName"})
public abstract class EntityHandler<T> {
	
	protected final String schema;
	protected final String tableName;
	protected final String className;
	protected final List<PropertyHandler> properties;
	private final EntityFactory<T> entityFactory;
	protected EntityClass<T> entityClass;
	
	abstract public String getSuperClass();
	abstract public String getPackage();
	abstract public EntityFactory<T> getEntityFactory();
	
	public EntityHandler(String schema, String tableName, ResultSetMetaData md) throws Exception {
		this.schema = schema;
		this.tableName = tableName;
		this.className = getPackage().concat(".")
				         .concat(ClassBuilder.firstUpper(EntityFactory.getJavaName(this.tableName)));
		
		// Collect properties		
		this.properties = new ArrayList<PropertyHandler>();
		for (int i = 1; i <= md.getColumnCount(); i++) {
			this.properties.add(new PropertyHandler(md, i));
		}

		this.entityFactory = getEntityFactory();
		
	}

	/**
	 * Get EntityClass and construct it at the first access.
	 * This method has to be LAZY!
	 * @return
	 * @throws Exception
	 */
	public EntityClass<T> getEntityClass() throws Exception {
		if (entityClass == null) {
			this.entityClass = entityFactory.createClass(this);
		}
		return entityClass;
	}
	
	public PropertyHandler getPropertyByName(String name) {
		for (PropertyHandler p: properties) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}
	
}
