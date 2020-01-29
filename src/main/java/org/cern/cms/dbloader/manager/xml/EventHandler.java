package org.cern.cms.dbloader.manager.xml;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

import org.w3c.dom.Node;

import lombok.extern.log4j.Log4j;

@Log4j
public class EventHandler implements ValidationEventHandler {

    public boolean handleEvent(ValidationEvent ev) {

        // If warning level - log it and continue
        if (ev.getSeverity() == ValidationEvent.WARNING) {
            log.warn(ev.getLinkedException());
            return true;
        }

        // If error about empty element - skip it
        if (ev.getSeverity() == ValidationEvent.ERROR) {
            if (ev.getLinkedException() instanceof NumberFormatException) {
                if (ev.getMessage() != null && ev.getMessage().equals("")) {
                    log.warn(String.format("Empty element found @ %s. Skipping..", getLocation(ev.getLocator())));
                    return true;
                }
            }
        }

        // Error - exit
        return false;

    }

    private static String getLocation(ValidationEventLocator loc) {
        StringBuffer buf = new StringBuffer();
        if (loc != null) {
            Node node = loc.getNode();
            while (node != null) {
                buf.insert(0, "/").insert(0, node.getNodeName());
                node = node.getParentNode();
            }
        }
        buf.append("(")
                .append(loc.getLineNumber())
                .append(",")
                .append(loc.getColumnNumber())
                .append(")");
        return buf.toString();
    }

}
