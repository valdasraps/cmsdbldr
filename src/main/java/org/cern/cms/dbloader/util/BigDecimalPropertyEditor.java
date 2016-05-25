package org.cern.cms.dbloader.util;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;

public class BigDecimalPropertyEditor extends PropertyEditorSupport {

    private BigDecimal value;
    
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        this.value = new BigDecimal(text);
    }

    @Override
    public Object getValue() {
        return this.value;
    }

}