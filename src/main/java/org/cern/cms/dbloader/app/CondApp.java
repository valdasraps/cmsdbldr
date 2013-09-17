package org.cern.cms.dbloader.app;

import java.io.File;

import javax.management.modelmbean.XMLParseException;

import lombok.extern.log4j.Log4j;

import org.cern.cms.dbloader.dao.CondDao;
import org.cern.cms.dbloader.manager.CondManager;
import org.cern.cms.dbloader.manager.CondXmlManager;
import org.cern.cms.dbloader.manager.HbmManager;
import org.cern.cms.dbloader.manager.HelpPrinter;
import org.cern.cms.dbloader.manager.PropertiesManager;
import org.cern.cms.dbloader.manager.ResourceFactory;
import org.cern.cms.dbloader.metadata.ChannelEntityHandler;
import org.cern.cms.dbloader.metadata.CondEntityHandler;
import org.cern.cms.dbloader.metadata.EntityHandler;
import org.cern.cms.dbloader.model.condition.ChannelBase;
import org.cern.cms.dbloader.model.condition.CondBase;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.xml.Header;
import org.cern.cms.dbloader.model.xml.Root;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Log4j
@Singleton
public class CondApp extends AppBase {

	@Inject
	private PropertiesManager props;
	
	@Inject
	private CondManager condm;

	@Inject
	private ResourceFactory rf;
	
	public boolean handleInfo() throws Exception {
		
		if (props.isConditionsList()) {
			HelpPrinter.outputConditionList(System.out, condm);
			return true;
		}
		
		if (props.isChannelsList()) {
			HelpPrinter.outputChannelList(System.out, condm);
			return true;
		}
		
		if (props.isConditionDesc()) {
			String condName = props.getConditionDesc();
			EntityHandler<CondBase> tm = condm.getConditionHandler(condName);
			if (tm == null) {
				throw new IllegalArgumentException(String.format("%s condition not found!", condName));
			}
			HelpPrinter.outputConditionDesc(System.out, condName, tm);
			return true;
		}
		
		if (props.isChannelDesc()) {
			String chanName = props.getChannelDesc();
			EntityHandler<ChannelBase> tm = condm.getChannelHandler(chanName);
			if (tm == null) {
				throw new IllegalArgumentException(String.format("%s channel not found!", chanName));
			}
			HelpPrinter.outputConditionDesc(System.out, chanName, tm);
			return true;
		}

		if (props.isConditionClass()) {
			String condName = props.getConditionClass();
			EntityHandler<CondBase> tm = condm.getConditionHandler(condName);
			if (tm == null) {
				throw new IllegalArgumentException(String.format("%s condition not found!", condName));
			}
			tm.getEntityClass().outputClassFile(System.out);
			return true;
		}

		if (props.isChannelClass()) {
			String chName = props.getChannelClass();
			EntityHandler<ChannelBase> tm = condm.getChannelHandler(chName);
			if (tm == null) {
				throw new IllegalArgumentException(String.format("%s channel not found!", chName));
			}
			tm.getEntityClass().outputClassFile(System.out);
			return true;
		}
		
		if (props.isConditionXml()) {
			String condName = props.getConditionXml();
			CondEntityHandler ceh = condm.getConditionHandler(condName);
			if (ceh == null) {
				throw new IllegalArgumentException(String.format("%s condition not found!", condName));
			}
			CondXmlManager xmlm = new CondXmlManager(ceh, null);
			xmlm.printExample();
			
			return true;
		}

		return false;
		
	}

	@Override
	public boolean handleData(File file, HbmManager hbm, Root root, AuditLog auditlog) throws Exception {
		if (root.getHeader() != null && root.getDatasets() != null) {
			
			Header h = root.getHeader();
			
			if (root.getDatasets().isEmpty()) {
				throw new XMLParseException("No datasets defined!");
			}
			
			if (h.getKindOfCondition() == null) {
				throw new XMLParseException("No Kind of Conition defined!");
			}
			
			if (h.getKindOfCondition().getName() == null) {
				throw new XMLParseException("No Kind of Condition name defined!");
			}

			CondEntityHandler condeh = condm.getConditionHandler(h.getKindOfCondition().getName());
			if (condeh == null) {
				throw new XMLParseException(String.format("Kind of Condition not resolved: %s", h.getKindOfCondition()));
			}
			
			ChannelEntityHandler chaneh = null;
			if (h.getHint() != null) {
				if (h.getHint().getChannelMap() != null) {
					chaneh = condm.getChannelHandler(h.getHint().getChannelMap());
					if (chaneh == null) {
						throw new XMLParseException(String.format("Channel Map not resolved: %s", h.getHint()));
					}
					
				}
			}
			
			// So far - success!
			log.info(String.format("%s with %d datasets and %s channel map to be processed", condeh.getName(), root.getDatasets().size(), chaneh));

			CondXmlManager xmlm = new CondXmlManager(condeh, chaneh);
			root = (Root) xmlm.unmarshal(file);
			
			// Try to load the file!
			CondDao dao = rf.createCondDao(hbm);
			dao.saveCondition(root, auditlog);
				
			return true;
		}
		return false;
	}
	
}
