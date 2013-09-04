package org.cern.cms.dbloader.metadata;

import java.lang.reflect.Modifier;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import lombok.Getter;

/**
 * Simple Class builder
 * for javassist
 * @author valdo
 *
 */
public class ClassBuilder {

	private final ClassPool pool;
	
	@Getter
	private final CtClass cc;
	
	@Getter
	private final ClassFile cf;
	
	private final ConstPool cpool;
	private final AnnotationsAttribute attrs;
	
	/**
	 * Constructor
	 * @param className fully qualified class name to build
	 */
	public ClassBuilder(String className) {
	    this.pool = ClassPool.getDefault();
	    this.cc = pool.makeClass(className);
        this.cc.setModifiers(Modifier.PUBLIC);
        this.cf = cc.getClassFile();
        this.cpool = cf.getConstPool();
        this.attrs = new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
	}

	/**
	 * Set super class for the class to build
	 * @param superClass
	 * @return ClassBuilder
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 */
	public ClassBuilder setSuper(Class<?> superClass) throws NotFoundException, CannotCompileException {
		cc.setSuperclass(pool.getCtClass(superClass.getName()));
		return this;
	}

	/**
	 * Set super class for the class to build
	 * @param superClassName fully qualified class name
	 * @return
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 */
	public ClassBuilder setSuper(String superClassName) throws NotFoundException, CannotCompileException {
		cc.setSuperclass(pool.getCtClass(superClassName));
		return this;
	}

	/**
	 * Create new property builder
	 * @param type type of the property
	 * @param name name of the property
	 * @return PropertyBuilder
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	public PropertyBuilder newProperty(Class<?> type, String name) throws CannotCompileException, NotFoundException {
		return new PropertyBuilder(this, type, name);
	}
	
	/**
	 * Create new class-level annotation builder
	 * @param type type of the annotation
	 * @return Annotation builder
	 */
	public AnnBuilder<ClassBuilder> newAnnotation(Class<? extends java.lang.annotation.Annotation> type) {
		return new AnnBuilder<ClassBuilder>(type, this, attrs, this);
	}
	
	/**
	 * Build the class
	 * @return Class just built
	 * @throws CannotCompileException
	 */
	public Class<?> build() throws CannotCompileException {
		this.cf.addAttribute(attrs);
		return cc.toClass();
	}
	
	/**
	 * Single property builder for the class
	 * @author valdo
	 *
	 */
	public class PropertyBuilder {
		
		private final ClassBuilder cb;
		private final CtField f;
		private final CtMethod s;
		private final CtMethod g;
		private final AnnotationsAttribute af = new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
		private final AnnotationsAttribute ag = new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
		private final AnnotationsAttribute as = new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
		
		private PropertyBuilder(ClassBuilder cb, Class<?> type, String name) throws CannotCompileException, NotFoundException {
			this.cb = cb;
	        this.f = new CtField(cb.pool.get(type.getName()), name, cb.cc);
	        this.f.setModifiers(Modifier.PRIVATE);
	        this.s = CtNewMethod.setter("set".concat(firstUpper(name)), f);
	        this.g = CtNewMethod.getter("get".concat(firstUpper(name)), f);
		}
		
		/**
		 * Build the property and attach it to class
		 * @return Class builder
		 * @throws CannotCompileException
		 */
		public ClassBuilder build() throws CannotCompileException {
			cb.cc.addField(f);
			cb.cc.addMethod(g);
			cb.cc.addMethod(s);
			f.getFieldInfo().addAttribute(af);
			s.getMethodInfo().addAttribute(as);
			g.getMethodInfo().addAttribute(ag);
			return cb;
		}
		
		/**
		 * New annotation on the field
		 * @param type type of annotation
		 * @return Annotation builder
		 */
		public AnnBuilder<PropertyBuilder> newFieldAnnotation(Class<? extends java.lang.annotation.Annotation> type) {
			return new AnnBuilder<PropertyBuilder>(type, cb, af, this);
		}
		
		/**
		 * New annotation on the getter method
		 * @param type type of annotation
		 * @return Annotation builder
		 */
		public AnnBuilder<PropertyBuilder> newGetterAnnotation(Class<? extends java.lang.annotation.Annotation> type) {
			return new AnnBuilder<PropertyBuilder>(type, cb, ag, this);
		}
		
		/**
		 * New annotation on the setter method
		 * @param type type of annotation
		 * @return Annotation builder
		 */
		public AnnBuilder<PropertyBuilder> newSetterAnnotation(Class<? extends java.lang.annotation.Annotation> type) {
			return new AnnBuilder<PropertyBuilder>(type, cb, as, this);
		}
		
	}
	
	/**
	 * Annotation builder class
	 * @author valdo
	 *
	 * @param <T>
	 */
	public class AnnBuilder<T> {
		
		private final AnnotationsAttribute attrs;
		private final T parent;
		private final ClassBuilder cb;
		protected final Annotation a;
		
		private AnnBuilder(Class<? extends java.lang.annotation.Annotation> type, ClassBuilder cb, AnnotationsAttribute attrs, T parent) {
			this.parent = parent;
			this.cb = cb;
			this.attrs = attrs;
			this.a = new Annotation(type.getName(), cb.cpool);
		}
		
		/**
		 * Add annotation String attribute member
		 * @param name
		 * @param value
		 * @return Annotation builder
		 */
		public AnnBuilder<T> addMember(String name, String value) {
	        a.addMemberValue(name, new StringMemberValue(value, cb.cpool));
	        return this;
		}
		
		/**
		 * Ad annotation Enum attribute member
		 * @param name
		 * @param value
		 * @return Annotation builder
		 */
		public AnnBuilder<T> addMember(String name, Enum<?> value) {
	        EnumMemberValue v = new EnumMemberValue(cb.cpool);
	        v.setType(value.getClass().getName());
	        v.setValue(value.toString());
	        a.addMemberValue(name, v);
			return this;
		}
	       
		/**
		 *  Build the annotation and attach it to the object
		 * @return Upper-level builder object
		 */
	    public T build() {
	    	this.attrs.addAnnotation(a);
	    	return parent;
	    }
		
	}
	
	public static String firstUpper(String value) {
		return value.substring(0, 1).toUpperCase().concat(value.substring(1));
	}
	
}
