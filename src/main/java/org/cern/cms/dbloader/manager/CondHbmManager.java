package org.cern.cms.dbloader.manager;

import org.cern.cms.dbloader.metadata.ChannelEntityHandler;
import org.cern.cms.dbloader.metadata.CondEntityHandler;

import com.google.inject.Inject;
import org.cern.cms.dbloader.PropertiesManager;

public class CondHbmManager extends HbmManager {

    @Inject
    public CondHbmManager(PropertiesManager props, CondManager condm) throws Exception {
        super(props);
        for (CondEntityHandler eh : condm.getConditionHandlers()) {
            addEntityClass(eh.getEntityClass().getC());
        }
        for (ChannelEntityHandler eh : condm.getChannelHandlers()) {
            addEntityClass(eh.getEntityClass().getC());
        }
    }

}
