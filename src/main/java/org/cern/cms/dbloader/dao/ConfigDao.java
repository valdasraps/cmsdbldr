package org.cern.cms.dbloader.dao;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import lombok.extern.log4j.Log4j;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.model.config.VersionAlias;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.xml.Root;

/**
 * Created by aisi0860 on 5/30/17.
 */

@Log4j
public class ConfigDao extends DaoBase{

    @Inject
    public ConfigDao(@Assisted SessionManager sm) throws Exception {
        super(sm);
    }

    public void saveVersionAliases(Root root, AuditLog alog) {
        if (root.getMaps().getVersionAlias() != null) {
//            VersionAlias vAl = root.getMaps().getVersionAlias();
//            session.save(vAl);
        }



    }
}
