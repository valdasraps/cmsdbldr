package org.cern.cms.dbloader.util;

@SuppressWarnings("serial")
public class PropertiesException extends Exception {

	public PropertiesException(String message, Object... params) {
		super(String.format(message, params));
	}
	
}
