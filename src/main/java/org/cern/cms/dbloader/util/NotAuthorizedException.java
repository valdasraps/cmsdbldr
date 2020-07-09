package org.cern.cms.dbloader.util;

/**
 * Action is not authorized exception.
 * @author valdo
 */
public class NotAuthorizedException extends Exception {
    
    public NotAuthorizedException(String action) {
        super(action);
    }
    
}
