package org.cern.cms.dbloader.rest.service;

import com.google.common.collect.Collections2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.ws.rs.core.HttpHeaders;
import org.cern.cms.dbloader.util.OperatorAuth;

/**
 * Authorization service.
 * @author valdo
 */
public class AuthService {
    
    private final static String USERNAME_HEADER = "ADFS-LOGIN";
    private final static String FULLNAME_HEADER = "ADFS-FULLNAME";
    private final static String EGROUP_HEADER = "ADFS-GROUP";
    private final static String ANY_KEY = "*";
    
    public enum Operator {
        
        CONDITION("operators-condition"),
        CONSTRUCT("operators-construct"),
        TRACKING("operators-tracking");
        
        private final String keyValue;
        
        Operator(String keyValue) {
            this.keyValue = keyValue;
        }
        
        public String getKeyValue() {
            return keyValue;
        }
        
    }
    
    private final Map<Operator, List<String>> alist = new HashMap<>();
    
    public AuthService(Properties props) {
        for (Operator op: Operator.values()) {
            alist.put(op, Arrays.asList(props.getProperty(op.getKeyValue(), "").toUpperCase().split(",")));
        }
    }
    
    public String getUsername(HttpHeaders headers) {
        List<String> values = headers.getRequestHeader(USERNAME_HEADER);
        return values != null && values.size() > 0 ? values.get(0) : null;
    }

    public String getFullname(HttpHeaders headers) {
        List<String> values = headers.getRequestHeader(FULLNAME_HEADER);
        return values != null && values.size() > 0 ? values.get(0) : null;
    }

    public List<String> getGroups(HttpHeaders headers) {
        List<String> values = headers.getRequestHeader(EGROUP_HEADER);
        return values != null && values.size() > 0 ? new ArrayList<>(Arrays.asList(values.get(0).split(";"))) : new ArrayList<>();
    }
    
    public boolean isOperator(Operator op, HttpHeaders headers) {
        String username = getUsername(headers);
        if (username != null) {
            List<String> list = alist.get(op);
            if (list.contains(ANY_KEY)) { 
                return true;
            } else {
                List<String> groups = getGroups(headers);
                groups.add(username);
                return !Collections.disjoint(alist.get(op), upperCaseCollection(groups));
            }
        } else {
            return false;
        }
    }
 
    private static Collection<String> upperCaseCollection(Collection<String> coll) {
        return Collections2.transform(coll, String::toUpperCase);
    }
    
    public OperatorAuth getOperatorAuth(HttpHeaders headers) {
        return new OperatorAuth(
                getUsername(headers), 
                getFullname(headers), 
                isOperator(Operator.CONSTRUCT, headers), 
                isOperator(Operator.CONDITION, headers), 
                isOperator(Operator.TRACKING, headers));
    }
    
}
