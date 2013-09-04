package org.cern.cms.dbloader;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.management.modelmbean.XMLParseException;

import lombok.extern.log4j.Log4j;

import org.apache.log4j.Logger;
import org.cern.cms.dbloader.app.CondApp;
import org.cern.cms.dbloader.dao.AuditDao;
import org.cern.cms.dbloader.manager.CondHbmManager;
import org.cern.cms.dbloader.manager.CondManager;
import org.cern.cms.dbloader.manager.EntityModificationManager;
import org.cern.cms.dbloader.manager.FilesManager;
import org.cern.cms.dbloader.manager.FilesManager.DataFile;
import org.cern.cms.dbloader.manager.HbmManager;
import org.cern.cms.dbloader.manager.PropertiesManager;
import org.cern.cms.dbloader.manager.XmlManager;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.managemnt.UploadStatus;
import org.cern.cms.dbloader.model.xml.Root;
import org.cern.cms.dbloader.util.PropertiesException;
import org.slf4j.bridge.SLF4JBridgeHandler;

@Log4j
public class DbLoader {
	
	public static void main(String[] args) {
		
        // Install SLF4J logger to direct all java.util.logging messages to log4j
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
		
		try {
			
			PropertiesManager props = new PropertiesManager(args);
			
			if (props.printVersion()) {
				return;
			}
			
			if (props.printHelp()) {
				return;
			}
			
			Logger.getRootLogger().setLevel(props.getLogLevel());
			
			/**
			 * Modify individual classes. This routine must be run before 
			 * the class loader tries to access the to-be-modified classes!
			 */
			EntityModificationManager.modify(props);

			if (props.isSchema()) {
				XmlManager xmlm = new XmlManager();
				xmlm.generateSchema(props.getSchemaParent());
				return;
			}
			
			CondManager condm = new CondManager(props);
			CondApp condApp = new CondApp(props, condm);
			
			if (condApp.handleInfo()) {
				return;
			}
			
			if (props.getArgs().isEmpty()) {
				throw new IllegalArgumentException("No input files provided");
			}
			
			try (HbmManager hbm = new CondHbmManager(props, condm)) {
				
				AuditDao auditDao = new AuditDao(props, hbm);
				for (DataFile df: FilesManager.getFiles(props)) {
					
					AuditLog alog = new AuditLog();					
					try {
						
						alog.setArchiveFileName(df.getArchive().getName());
						alog.setDataFileName(df.getData().getName());
						alog.setDataFileChecksum(df.getMd5());
						auditDao.saveAuditRecord(alog);
						
						log.info(String.format("Processing %s", df));
						XmlManager xmlm = new XmlManager();
						Root root = xmlm.unmarshal(df.getData());
						
						if (log.isDebugEnabled()) {
							log.debug(root);
						}

						boolean loaded = condApp.handleData(df.getData(), hbm, root, alog);

						if (loaded) {
							alog.setStatus(UploadStatus.Success);
						} else {
							throw new XMLParseException("XML file not recognized by handlers");
						}
						
					} catch (Exception ex) {
						
						StringWriter sw = new StringWriter();
						ex.printStackTrace(new PrintWriter(sw));
						alog.setUploadLogTrace(sw.toString());
						alog.setStatus(UploadStatus.Failure);
						
						log.error(ex.getMessage(), ex);
						
					}
					
					auditDao.saveAuditRecord(alog);
					
				}

			}
			
		} catch (PropertiesException ex) {
			System.err.println("ERROR: " + ex.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}

	}
		
}
