package org.cern.cms.dbloader.manager.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.cern.cms.dbloader.model.condition.CondBase;

public class CondBaseProxyAdapter<T extends CondBase> extends XmlAdapter<Object, T> {

    private final JAXBContext jaxb;
    private final Class<T> clazz;
    
    public CondBaseProxyAdapter(JAXBContext jaxb) {
        this(jaxb, null);
    }

    public CondBaseProxyAdapter(JAXBContext jaxb, Class<T> clazz) {
        this.jaxb = jaxb;
        this.clazz = clazz;
    }
        
    private ClassAdapter<T> getAdapter(Class<T> clazz) {
        return new ClassAdapter<>(this.jaxb, clazz, "DATA");
    }
    
    @Override
    public Object marshal(T o) throws Exception {
        if (clazz != null) {
            return getAdapter(clazz).marshal(o);
        } else {
            return null;
        }
    }

    @Override
    public T unmarshal(Object o) throws Exception {
        return (T) new CondBase() {

            private final Object obj = o;

            @Override
            public <P extends CondBase> P getDelegate(Class<P> clazz_) throws Exception {
                return (P) getAdapter((Class<T>) clazz_).unmarshal(obj);
            }

        };
        
    }
    
}
