package org.cern.cms.dbloader.manager;

import java.io.PrintWriter;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ClassFilePrinter;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

import javax.persistence.JoinTable;
import javax.persistence.SequenceGenerator;
import org.cern.cms.dbloader.PropertiesManager;

public class EntityModificationManager {

    private final static String[] SQGENERATOR_SCHEMA_CORECONDITION = new String[] {
        "org.cern.cms.dbloader.model.condition.CondBase",
        "org.cern.cms.dbloader.model.managemnt.AuditLog",
        "org.cern.cms.dbloader.model.condition.Dataset",
        "org.cern.cms.dbloader.model.condition.Run"
    };

    private final static String[] SQGENERATOR_SCHEMA_CORE_IOV_MGMNT = new String[] {
        "org.cern.cms.dbloader.model.iov.Tag",
        "org.cern.cms.dbloader.model.iov.Iov"
    };

    private final static String[] SQGENERATOR_SCHEMA_CONSTRUCT_CORE_SCHEMA_MGMNT = new String[] {
        "org.cern.cms.dbloader.model.construct.KindOfPart",
        "org.cern.cms.dbloader.model.construct.Part",
        "org.cern.cms.dbloader.model.construct.Manufacturer",
        "org.cern.cms.dbloader.model.construct.PartToAttrRltSh"
    };

    private final static String[] SQGENERATOR_SCHEMA_MANAGEMNT_CORE_SCHEMA = new String[] {
        "org.cern.cms.dbloader.model.managemnt.Institution",
        "org.cern.cms.dbloader.model.managemnt.Location"
    };

    private final static String[] SQGENERATOR_SCHEMA_CORE_ATTRIBUTE = new String[] {
        "org.cern.cms.dbloader.model.xml.map.AttrBase",
        "org.cern.cms.dbloader.model.xml.map.AttrCatalog",
        "org.cern.cms.dbloader.model.condition.CondAttrList",
        "org.cern.cms.dbloader.model.construct.PartAttrList",
        "org.cern.cms.dbloader.model.construct.PartRelationship"
    };

    public static void modify(final PropertiesManager props) throws Exception {

        for (final String pc : SQGENERATOR_SCHEMA_CORE_ATTRIBUTE) {
            new EntityModifier(pc) {
                @Override
                protected void modify() throws Exception {
                    addFieldAnnotationAttribute("id", SequenceGenerator.class, "schema", props.getCoreAttributeSchemaName());
                }
            }.process();
        }

        for (final String pc : SQGENERATOR_SCHEMA_CORECONDITION) {
            new EntityModifier(pc) {
                @Override
                protected void modify() throws Exception {
                    addFieldAnnotationAttribute("id", SequenceGenerator.class, "schema", props.getCoreConditionSchemaName());
                    if (pc.equals("org.cern.cms.dbloader.model.condition.Dataset")) {
                        addFieldAnnotationAttribute("iovs", JoinTable.class, "schema", props.getIovCoreSchemaName());
                    }
                }
            }.process();
        }

        for (final String pc : SQGENERATOR_SCHEMA_CORE_IOV_MGMNT) {
            new EntityModifier(pc) {
                @Override
                protected void modify() throws Exception {
                    addFieldAnnotationAttribute("id", SequenceGenerator.class, "schema", props.getIovCoreSchemaName());
                    if (pc.equals("org.cern.cms.dbloader.model.iov.Tag")) {
                        addFieldAnnotationAttribute("iovs", JoinTable.class, "schema", props.getIovCoreSchemaName());
                    }
                }
            }.process();
        }

        for (String pc : SQGENERATOR_SCHEMA_CONSTRUCT_CORE_SCHEMA_MGMNT) {
            new EntityModifier(pc) {
                @Override
                protected void modify() throws Exception {
                    addFieldAnnotationAttribute("id", SequenceGenerator.class, "schema", props.getCoreConstructSchemaName());
                }
            }.process();
        }

        for (String pc : SQGENERATOR_SCHEMA_MANAGEMNT_CORE_SCHEMA) {
            new EntityModifier(pc) {
                @Override
                protected void modify() throws Exception {
                    addFieldAnnotationAttribute("id", SequenceGenerator.class, "schema", props.getCoreManagemntSchemaName());
                }
            }.process();
        }

        new EntityModifier("org.cern.cms.dbloader.model.condition.KindOfCondition") {
            @Override
            protected void modify() throws Exception {
                addFieldAnnotationAttribute("kindsOfParts", JoinTable.class, "schema", props.getCoreConditionSchemaName());
            }
        }.process();

        /*        new EntityModifier("org.cern.cms.dbloader.model.condition.Dataset") {
         @Override
         protected void modify() throws Exception {
         addFieldAnnotationAttribute("iovs", JoinTable.class, "schema", props.getCoreConditionSchemaName());
         }
         }.process();
        
         new EntityModifier("org.cern.cms.dbloader.model.iov.Tag") {
         @Override
         protected void modify() throws Exception {
         addFieldAnnotationAttribute("iovs", JoinTable.class, "schema", props.getIovCoreSchemaName());
         }
         }.process();*/
    }

    private static abstract class EntityModifier {

        private final ClassPool classPool = ClassPool.getDefault();
        private final CtClass cc;
        private final ClassFile cf;
        private final ConstPool cp;

        public EntityModifier(String className) throws NotFoundException {
            this.cc = classPool.getCtClass(className);
            this.cf = cc.getClassFile();
            this.cp = cf.getConstPool();
        }

        protected abstract void modify() throws Exception;

        protected void addFieldAnnotationAttribute(String fieldName, Class<?> annotationType,
                String attributeName, String attributeValue) throws Exception {
            try {
                CtField f = cc.getDeclaredField(fieldName);

                AnnotationsAttribute anAttr = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);

                for (Object o : f.getFieldInfo().getAttributes()) {
                    if (o instanceof AnnotationsAttribute) {
                        AnnotationsAttribute aa = (AnnotationsAttribute) o;
                        for (Annotation a : aa.getAnnotations()) {
                            if (a.getTypeName().equals(annotationType.getName())) {
                                a.addMemberValue(attributeName, new StringMemberValue(attributeValue, cp));
                            }
                            anAttr.addAnnotation(a);
                        }
                    }
                }

                f.getFieldInfo().addAttribute(anAttr);
            } catch (Throwable e) {
                throw new Exception(e);
            }
        }

        public EntityModifier process() throws Exception {
            modify();
            cc.toClass();
            return this;
        }

        @SuppressWarnings("unused")
        public EntityModifier print() {
            PrintWriter pw = new PrintWriter(System.out);
            ClassFilePrinter.print(cf, pw);
            pw.flush();
            return this;
        }

    }

}
