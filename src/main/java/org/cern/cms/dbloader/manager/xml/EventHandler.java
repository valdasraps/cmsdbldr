package org.cern.cms.dbloader.manager.xml;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import lombok.extern.log4j.Log4j;

@Log4j
public class EventHandler implements ValidationEventHandler {

	public boolean handleEvent(ValidationEvent ev) {
		
		// If warning level - log it and continue
		if (ev.getSeverity() == ValidationEvent.WARNING) {
			log.warn(ev.getLinkedException());
			return true;
		}
		
		// Error - exit
		return false;
		
	}
	
}

