package org.cern.cms.dbloader.app;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.log4j.Log4j;
import org.cern.cms.dbloader.dao.ConfigDao;
import org.cern.cms.dbloader.manager.ResourceFactory;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.xml.Root;

import javax.management.modelmbean.XMLParseException;

/**
 * Created by aisi0860 on 5/24/17.
 */
@Log4j
@Singleton
public class ConfigApp extends AppBase{

    @Inject
    private ResourceFactory rf;

    @Override
    public boolean handleData(SessionManager sm, DataFile file, Root root, AuditLog alog) throws Exception {
        if (!root.getMaps().getVersionAlias().getName().isEmpty()){
            ConfigDao dao = rf.createConfigDao(sm);
            dao.saveVersionAliases(root, alog);

        } else {
            throw new XMLParseException("No VerionAliases defined");
        }

        return true;

    }



}