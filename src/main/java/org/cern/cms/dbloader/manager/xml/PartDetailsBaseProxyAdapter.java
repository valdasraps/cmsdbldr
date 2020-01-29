package org.cern.cms.dbloader.manager.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.cern.cms.dbloader.model.construct.PartDetailsBase;

public class PartDetailsBaseProxyAdapter<T extends PartDetailsBase> extends XmlAdapter<Object, T> {

    private final JAXBContext jaxb;
    private final Class<T> clazz;
    
    public PartDetailsBaseProxyAdapter(JAXBContext jaxb) {
        this(jaxb, null);
    }

    public PartDetailsBaseProxyAdapter(JAXBContext jaxb, Class<T> clazz) {
        this.jaxb = jaxb;
        this.clazz = clazz;
    }
        
    private ClassAdapter<T> getAdapter(Class<T> clazz) {
        return new ClassAdapter<>(this.jaxb, clazz, "PART_EXTENSION");
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
        return (T) new PartDetailsBase() {

            private final Object obj = o;

            @Override
            public <P extends PartDetailsBase> P getDelegate(Class<P> clazz_) throws Exception {
                return (P) getAdapter((Class<T>) clazz_).unmarshal(obj);
            }

        };
        
    }
    
}
