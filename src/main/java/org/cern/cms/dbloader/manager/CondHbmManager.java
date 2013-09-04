package org.cern.cms.dbloader.manager;

import org.cern.cms.dbloader.metadata.ChannelEntityHandler;
import org.cern.cms.dbloader.metadata.CondEntityHandler;

public class CondHbmManager extends HbmManager {

	public CondHbmManager(final PropertiesManager props, final CondManager condm) throws Exception  {
		super(props);
		for (CondEntityHandler eh: condm.getConditionHandlers()) {
			addEntityClass(eh.getEntityClass().getC());
		}
		for (ChannelEntityHandler eh: condm.getChannelHandlers()) {
			addEntityClass(eh.getEntityClass().getC());
		}
	}
	
}
