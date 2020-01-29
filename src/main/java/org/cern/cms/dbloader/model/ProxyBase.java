package org.cern.cms.dbloader.model;

import org.apache.commons.lang.NotImplementedException;

public abstract class ProxyBase<E> {
    
    public <T extends E> T getDelegate(Class<T> clazz) throws Exception {
        throw new NotImplementedException();
    }
    
}
