package org.cern.cms.dbloader.metadata;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.fasterxml.jackson.annotation.JsonProperty;
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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import lombok.extern.log4j.Log4j;

import org.cern.cms.dbloader.manager.PropertyType;
import org.cern.cms.dbloader.manager.xml.DateAdapter;
import org.cern.cms.dbloader.metadata.ClassBuilder.PropertyBuilder;

@Log4j
public abstract class EntityFactory<T> {

    private static final String TO_STRING_METHOD
            = "public String toString() { "
            + "return org.apache.commons.lang.builder.ToStringBuilder.reflectionToString(this);	"
            + "}";

    private static final Map<String, EntityClass<?>> LOADED_CLASSES = new HashMap<>();

    @SuppressWarnings("unchecked")
    public EntityClass<T> createClass(EntityHandler<T> tmd) throws Exception {

        if (LOADED_CLASSES.containsKey(tmd.getClassName())) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("%s found, reusing...", tmd.getClassName()));
            }
            return (EntityClass<T>) LOADED_CLASSES.get(tmd.getClassName());
        }

        if (log.isDebugEnabled()) {
            log.debug(String.format("About to create %s", tmd.getClassName()));
        }

        ClassBuilder cb = new ClassBuilder(tmd.getClassName());
        cb.setSuper(tmd.getSuperClass());

        for (PropertyHandler cmd : tmd.getProperties()) {

            PropertyBuilder pb = cb.newProperty(cmd.getPropertyClass(), cmd.getName());

            pb.newFieldAnnotation(Basic.class).build()
                    .newFieldAnnotation(Column.class).addMember("name", cmd.getColumnName()).build()
                    .newFieldAnnotation(XmlElement.class).addMember("name", cmd.getColumnName()).addMember("nillable", true).build();

            pb.newFieldAnnotation(JsonProperty.class).addMember("value", cmd.getColumnName().toLowerCase()).build();

            if (cmd.getType().equals(PropertyType.CLOB)) {
                pb.newFieldAnnotation(Lob.class).build();
            }

            if (cmd.getType().equals(PropertyType.TEMPORAL)) {
                pb.newFieldAnnotation(Temporal.class).addMember("value", TemporalType.TIMESTAMP).build();
                pb.newFieldAnnotation(XmlJavaTypeAdapter.class).addMember("value", DateAdapter.class).build();
            }
            
            modifyProperty(pb);

            pb.build();

        }

        CtMethod m = CtNewMethod.make(TO_STRING_METHOD, cb.getCc());
        cb.getCc().addMethod(m);

        cb.newAnnotation(Entity.class).build()
                .newAnnotation(Table.class).addMember("name", tmd.getTableName()).addMember("schema", tmd.getSchema()).build();

        modifyClass(cb);

        EntityClass<T> ec = new EntityClass<>(cb.getCc(), cb.getCf(), (Class<? extends T>) cb.build());
        LOADED_CLASSES.put(tmd.getClassName(), ec);

        return ec;

    }

    /**
     * Custom class modification
     * @param cb 
     */
    public void modifyClass(ClassBuilder cb) { }

    /**
     * Custom property modification
     * @param pb 
     */
    public void modifyProperty(PropertyBuilder pb) { }

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
