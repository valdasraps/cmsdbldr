package org.cern.cms.dbloader.manager.xml;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateAdapter extends XmlAdapter<String, Date> {

    private static final SimpleDateFormat DATE_FORMAT
            = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public String marshal(Date v) throws Exception {
        if (v == null) {
            return null;
        }
        return DATE_FORMAT.format(v);
    }

    @Override
    public Date unmarshal(String v) throws ParseException, NullPointerException {
        if (v == null || v.trim().isEmpty()) {
            return null;
        }
//    	if (!v.contains("T")) {
//    		String temp = v.replaceAll(" ", "T").trim();
//    		return dateFormat.parse(temp);
//    	}
        Date date = DATE_FORMAT.parse(v, new ParsePosition(0));
        if (date == null) {
            date = DATE_FORMAT.parse(v.concat(" 00:00:00.0"));
        }
        return date;
    }

}
