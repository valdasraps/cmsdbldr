package org.cern.cms.dbloader.app;


import lombok.extern.log4j.Log4j;

import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.model.serial.Root;

import com.google.inject.Singleton;
import org.cern.cms.dbloader.dao.TrackingDao;
import org.cern.cms.dbloader.manager.PropertiesManager;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.manager.file.DataFileType;
import org.cern.cms.dbloader.model.construct.ext.Request;
import org.cern.cms.dbloader.model.construct.ext.Shipment;
import org.cern.cms.dbloader.model.managemnt.AuditLog;
import org.cern.cms.dbloader.util.NotAuthorizedException;
import org.cern.cms.dbloader.util.OperatorAuth;

@Log4j
@Singleton
public class TrackingApp extends AppBase {

    @Override
    public void checkPermission(OperatorAuth auth) throws NotAuthorizedException {
        if (!auth.isTrackingPermission()) {
            throw new NotAuthorizedException(PropertiesManager.UserOption.OPERATOR_TRACKING_PERMISSION.name());
        }
    }
    
    @Override
    public void handleData(SessionManager sm, DataFile file, AuditLog alog, OperatorAuth auth) throws Exception {
        Root root = file.getRoot();

        TrackingDao dao = rf.createTrackingDao(sm, auth);
        alog.setKindOfConditionName("[CONSTRUCT]");
        int count = 0;

        if (file.getType() == DataFileType.REQUEST) {
            
            alog.setExtensionTableName("[REQUEST]");
            for (Request request: root.getRequests()) {
                dao.save(request, alog);
                count++;
            }
            
        } else {
            
            if (file.getType() == DataFileType.SHIPMENT) {
                alog.setExtensionTableName("[SHIPMENT]");
                for (Shipment shipment: root.getShipments()) {
                    dao.save(shipment, alog);
                    count++;
                }
            }
            
        }

        alog.setDatasetRecordCount(count);
        
    }

}
