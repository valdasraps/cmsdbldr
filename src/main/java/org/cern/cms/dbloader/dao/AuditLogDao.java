package org.cern.cms.dbloader.dao;

import java.util.Date;

import org.cern.cms.dbloader.model.managemnt.AuditLog;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import java.io.PrintWriter;
import java.io.StringWriter;
import lombok.Getter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.cern.cms.dbloader.manager.PropertiesManager;
import org.cern.cms.dbloader.manager.ResourceFactory;
import org.cern.cms.dbloader.manager.SessionManager;
import org.cern.cms.dbloader.manager.file.ArchiveFile;
import org.cern.cms.dbloader.manager.file.DataFile;
import org.cern.cms.dbloader.manager.file.FileBase;
import org.cern.cms.dbloader.model.construct.Part;
import org.cern.cms.dbloader.model.managemnt.UploadStatus;
import org.cern.cms.dbloader.util.OperatorAuth;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class AuditLogDao {

    private static final Logger logger = LogManager.getLogger(AuditLogDao.class);
    
    private static final String UNDEFINED_SUBDETECTOR_NAME = "UNDEFINED";

    @Getter
    private final AuditLog log;
    private final OperatorAuth auth;
    private String operatorName;
    
    @Inject
    private ResourceFactory rf;
    
    @Inject
    private PropertiesManager props;
    
    private final long startTime = System.currentTimeMillis();
    
    @Inject
    public AuditLogDao(@Assisted FileBase fb, @Assisted OperatorAuth auth) throws Exception {
        this.log = new AuditLog();
        this.auth = auth;

        if (fb instanceof ArchiveFile) {
            this.log.setArchiveFileName(fb.getFilename());
        }
        
        if (fb instanceof DataFile) {
            this.log.setArchiveFileName(((DataFile) fb).getArchive().getFilename());
            this.operatorName = ((DataFile) fb).getRoot().getOperatorName();
        }

        this.log.setDataFileName(fb.getFilename());        
        this.log.setDataFileChecksum(fb.getMd5());
        this.log.setCreateTimestamp(new Date());

    }
    
    public final void saveProcessing() throws Exception {
        save(UploadStatus.Processing);
    }
    
    public final void saveFailure(Throwable error) throws Exception {
        StringWriter sw = new StringWriter();
        error.printStackTrace(new PrintWriter(sw));
        String str = sw.toString();
        if (str.length() > 4000) {
            str = str.substring(0, 4000);
        }
        log.setUploadLogTrace(str);
        save(UploadStatus.Failure);
    }
    
    public final void saveSuccess() throws Exception {
        save(UploadStatus.Success);
    }

    private void save(UploadStatus status) throws Exception {
        try (SessionManager sm = rf.createSessionManager()) {
            
            this.log.setStatus(status);
            this.log.setUploadTimeSeconds((System.currentTimeMillis() - this.startTime) / 1000);
            this.log.setUploadHostName(props.getHostName());
            this.log.setUploadSoftware(props.getVersion());
            
            if (this.log.getInsertTime() == null) {
                this.log.setInsertTime(new Date());
                if (this.operatorName != null) {
                    this.log.setCreatedByUser(this.operatorName);
                    this.log.setInsertUser(this.operatorName);
                } else {
                    this.log.setCreatedByUser(auth.getOperatorValue());
                    this.log.setInsertUser(props.getOsUser());
                }
                this.log.setSubdetectorName(getSubDetectorName(sm));
            } else {
                if (this.operatorName != null) {
                    this.log.setLastUpdateUser(this.operatorName);
                } else {
                    this.log.setLastUpdateUser(auth.getOperatorValue());
                }
                this.log.setLastUpdateTime(new Date());
            }

            sm.getSession().saveOrUpdate(this.log);

            if (props.isTest()) {
                sm.rollback();
            } else {
                sm.commit();
            }

        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private String getSubDetectorName(SessionManager sm) {
        
        try {
        
            return (String) sm.getSession().createCriteria(Part.class)
                .add(Restrictions.eq("id", props.getRootPartId()))
                .add(Restrictions.eq("deleted", Boolean.FALSE))
                .createCriteria("kindOfPart", "kop")
                .createCriteria("subdetector", "det")
                .setProjection(Projections.property("det.name"))
                .uniqueResult();

        } catch (Exception ex) {
            
            logger.warn("Error while reading subdetector name", ex);
            
            return UNDEFINED_SUBDETECTOR_NAME;
            
        }
        
    }

}
