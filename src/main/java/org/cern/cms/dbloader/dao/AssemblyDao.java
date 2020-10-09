package org.cern.cms.dbloader.dao;

import org.cern.cms.dbloader.manager.file.DataFile;

import org.cern.cms.dbloader.manager.*;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.model.serial.Root;


import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import lombok.extern.log4j.Log4j;
import org.cern.cms.dbloader.util.OperatorAuth;

@Log4j
public class AssemblyDao extends DaoBase {

    @Inject
    public AssemblyDao(@Assisted SessionManager sm, @Assisted OperatorAuth auth) throws Exception {
        super(sm, auth);
    }

    @Inject
    private DynamicEntityGenerator enGenerator;

    public void saveAssembly(Root root, AuditLog alog, DataFile file) throws Exception {
        
    }

}
