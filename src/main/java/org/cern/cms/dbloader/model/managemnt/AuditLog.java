package org.cern.cms.dbloader.model.managemnt;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.cern.cms.dbloader.manager.xml.DateAdapter;
import org.cern.cms.dbloader.model.EntityBase;

@Entity
@Table(name = "CONDITIONS_DATA_AUDITLOG")
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false, of = {"id"})
public class AuditLog extends EntityBase {

    @Id
    @Column(name = "RECORD_ID")
    @GeneratedValue(generator = "ANY_COND_RECORD_ID_SEQ", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "ANY_COND_RECORD_ID_SEQ", sequenceName = "ANY_COND_RECORD_ID_SEQ", allocationSize = 20)
    private BigInteger id;

    @Basic
    @Column(name = "ARCHVE_FILE_NAME", nullable = false)
    private String archiveFileName;

    @Basic
    @Column(name = "DATA_FILE_NAME", nullable = false)
    private String dataFileName;

    @Basic
    @Column(name = "DATA_FILE_CHECKSUM", nullable = false)
    private String dataFileChecksum;

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "UPLOAD_STATUS", nullable = false)
    private UploadStatus status = UploadStatus.Processing;

    @Basic
    @Column(name = "UPLOAD_HOSTNAME", nullable = false)
    private String uploadHostName;

    @Basic
    @Column(name = "UPLOAD_SOFTWARE", nullable = false)
    private String uploadSoftware;

    @Transient
    private Long instanceCreateTime = System.currentTimeMillis();

    @Basic
    @Column(name = "UPLOAD_TIME_SECONDS", nullable = false)
    private Long uploadTimeSeconds;

    @Basic
    @Column(name = "UPLOAD_LOG_TRACE")
    private String uploadLogTrace;

    @Basic
    @Column(name = "CREATE_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date createTimestamp;

    @Basic
    @Column(name = "CREATED_BY_USER")
    private String createdByUser;

    @Basic
    @Column(name = "VERSION")
    private String version;

    @Basic
    @Column(name = "SUBVERSION")
    private Integer subversion;

    @Basic
    @Column(name = "KIND_OF_CONDITION_NAME")
    private String kindOfConditionName;

    @Basic
    @Column(name = "EXTENSION_TABLE_NAME")
    private String extensionTableName;

    @Basic
    @Column(name = "SUBDETECTOR_NAME")
    private String subdetectorName;

    @Basic
    @Column(name = "RUN_TYPE")
    private String runType;

    @Basic
    @Column(name = "RUN_NUMBER")
    private BigInteger runNumber;

    @Basic
    @Column(name = "TAG_NAME")
    private String tagName;

    @Basic
    @Column(name = "INTERVAL_OF_VALIDITY_BEGIN")
    private BigInteger intervalOfValidityBegin;

    @Basic
    @Column(name = "INTERVAL_OF_VALIDITY_END")
    private BigInteger intervalOfValidityEnd;

    @Basic
    @Column(name = "DATASET_COUNT")
    private Integer datasetCount;

    @Basic
    @Column(name = "DATASET_RECORD_COUNT")
    private Integer datasetRecordCount;

    @Basic
    @Column(name = "DATA_RELATED_TO_LIST")
    private String dataRelatedToList;

}
