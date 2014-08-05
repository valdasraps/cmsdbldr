package org.cern.cms.dbloader.metadata;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javassist.CtMethod;
import javassist.CtNewMethod;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;

import lombok.extern.log4j.Log4j;

import org.cern.cms.dbloader.metadata.ClassBuilder.PropertyBuilder;

@Log4j
public abstract class EntityFactory<T> {

	private static Map<String, EntityClass<?>> loadedClasses = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	public EntityClass<T> createClass(EntityHandler<T> tmd) throws Exception {
		
		if (loadedClasses.containsKey(tmd.getClassName())) {
			if (log.isDebugEnabled()) {
				log.debug(String.format("%s found, reusing...", tmd.getClassName())); 
			}
			return (EntityClass<T>) loadedClasses.get(tmd.getClassName());
		}

		if (log.isDebugEnabled()) {
			log.debug(String.format("About to create %s", tmd.getClassName())); 
		}
		
		ClassBuilder cb = new ClassBuilder(tmd.getClassName());
		cb.setSuper(tmd.getSuperClass());
		
        for (PropertyHandler cmd: tmd.getProperties()) {

        	PropertyBuilder pb = cb.newProperty(cmd.getPropertyClass(), cmd.getName());
        	
        	pb.newFieldAnnotation(Basic.class).build()
        	  .newFieldAnnotation(Column.class).addMember("name", cmd.getColumnName()).build()
	          .newFieldAnnotation(XmlElement.class).addMember("name", cmd.getColumnName()).build();

	        if (cmd.isLob()) {
	        	pb.newFieldAnnotation(Lob.class).build();
	        }
	        
	        if (cmd.isTemporal()) {
	        	pb.newFieldAnnotation(Temporal.class).addMember("value", TemporalType.TIMESTAMP).build();
	        }

	        pb.build();
	        
        }
        
        String toStringM = "public String toString() { return org.apache.commons.lang.builder.ToStringBuilder.reflectionToString(this);	}";
        CtMethod m = CtNewMethod.make(toStringM, cb.getCc());
        cb.getCc().addMethod(m);

        cb.newAnnotation(Entity.class).build()
          .newAnnotation(Table.class).addMember("name", tmd.getTableName()).addMember("schema", tmd.getSchema()).build();
        
        modifyClass(cb);

        EntityClass<T> ec = new EntityClass<>(cb.getCc(), cb.getCf(), (Class<? extends T>) cb.build());
        loadedClasses.put(tmd.getClassName(), ec);
        
		return ec;
		
	}

	/**
	 * Override to provide custom class modifications
	 * @param cb
	 */
	public void modifyClass(ClassBuilder cb) {	}
	
	public static String getJavaName(String dbName) {
		String ename = "";
		StringTokenizer tok = new StringTokenizer(dbName, "_");
		while (tok.hasMoreTokens()) {
			String t = tok.nextToken();
			ename += t.substring(0, 1).toUpperCase().concat(t.substring(1).toLowerCase());
		}
		return ename.substring(0, 1).toLowerCase().concat(ename.substring(1));
	}
	
}
