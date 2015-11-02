-- PROMPT Creating Table 'CONDITIONS_DATA_AUDITLOG'

CREATE TABLE CONDITIONS_DATA_AUDITLOG
(RECORD_ID NUMBER(38,0) NOT NULL ,
RECORD_INSERTION_TIME  TIMESTAMP WITH TIME ZONE NOT NULL ,
RECORD_INSERTION_USER   VARCHAR2(50) NOT NULL ,
RECORD_LASTUPDATE_TIME  TIMESTAMP WITH TIME ZONE ,
RECORD_LASTUPDATE_USER VARCHAR2(50) ,
COMMENT_DESCRIPTION VARCHAR2(4000) ,
ARCHVE_FILE_NAME VARCHAR2(4000) NOT NULL ,
DATA_FILE_NAME VARCHAR2(4000) NOT NULL ,
DATA_FILE_CHECKSUM VARCHAR2(200)  NOT NULL ,
UPLOAD_STATUS VARCHAR2(50) NOT NULL ,
UPLOAD_HOSTNAME VARCHAR2(200)  NOT NULL ,
UPLOAD_SOFTWARE VARCHAR2(50) NOT NULL ,
UPLOAD_TIME_SECONDS NUMBER(38,0) NOT NULL ,
UPLOAD_LOG_TRACE VARCHAR2(4000) ,
CREATE_TIMESTAMP  TIMESTAMP WITH TIME ZONE ,
CREATED_BY_USER VARCHAR2(50) ,
VERSION VARCHAR2(40) ,
SUBVERSION NUMBER(38,0)  ,
KIND_OF_CONDITION_NAME VARCHAR2(40) ,
EXTENSION_TABLE_NAME VARCHAR2(30) ,
SUBDETECTOR_NAME VARCHAR2(20) ,
RUN_TYPE VARCHAR2(40) ,
RUN_NUMBER NUMBER(38,0)  ,
TAG_NAME VARCHAR2(40) ,
INTERVAL_OF_VALIDITY_BEGIN NUMBER(38,0)  ,
INTERVAL_OF_VALIDITY_END NUMBER(38,0)  ,
DATASET_COUNT NUMBER(38,0)  ,
DATASET_RECORD_COUNT NUMBER(38,0)  ,
DATA_RELATED_TO_LIST VARCHAR2(4000) 
)
 TABLESPACE CMS_&det._PROCERNIT_DATA
/

ALTER TABLE CONDITIONS_DATA_AUDITLOG
 ADD CONSTRAINT CNDDTAUDITLOG_PK
 PRIMARY KEY
 (RECORD_ID)
/

Prompt Index DATACHKSM_I;
--
-- DATACHKSM_I  (Index) 
--
--  Dependencies: 
--   CONDITIONS_DATA_AUDITLOG (Table)
--
CREATE INDEX DATACHKSM_I ON CONDITIONS_DATA_AUDITLOG
(DATA_FILE_CHECKSUM)
LOGGING
TABLESPACE CMS_&det._PROCERNIT_DATA
NOPARALLEL
/


Prompt Index TDFLNAME_I;
--
-- TDFLNAME_I  (Index) 
--
--  Dependencies: 
--   CONDITIONS_DATA_AUDITLOG (Table)
--
CREATE INDEX TDFLNAME_I ON CONDITIONS_DATA_AUDITLOG
(DATA_FILE_NAME)
LOGGING
TABLESPACE CMS_&det._PROCERNIT_DATA
NOPARALLEL
/


Prompt Index RECINSTIME_I;
--
-- RECINSTIME_I  (Index) 
--
--  Dependencies: 
--   CONDITIONS_DATA_AUDITLOG (Table)
--
CREATE INDEX RECINSTIME_I ON CONDITIONS_DATA_AUDITLOG
(SYS_EXTRACT_UTC("RECORD_INSERTION_TIME"))
LOGGING
TABLESPACE CMS_&det._PROCERNIT_DATA
NOPARALLEL
/


Prompt Index CRETETSTMP_I;
--
-- CRETETSTMP_I  (Index) 
--
--  Dependencies: 
--   CONDITIONS_DATA_AUDITLOG (Table)
--
CREATE INDEX CRETETSTMP_I ON CONDITIONS_DATA_AUDITLOG
(SYS_EXTRACT_UTC("CREATE_TIMESTAMP"))
LOGGING
TABLESPACE CMS_&det._PROCERNIT_DATA
NOPARALLEL
/


Prompt Index TAGNAME_I;
--
-- TAGNAME_I  (Index) 
--
--  Dependencies: 
--   CONDITIONS_DATA_AUDITLOG (Table)
--
CREATE INDEX TAGNAME_I ON CONDITIONS_DATA_AUDITLOG
(TAG_NAME)
LOGGING
TABLESPACE CMS_&det._PROCERNIT_DATA
NOPARALLEL
/

Prompt Privs on TABLE CONDITIONS_DATA_AUDITLOG TO CMS_&det._CORE_MANAGEMNT_ADMIN;
GRANT DELETE ON  CONDITIONS_DATA_AUDITLOG TO CMS_&det._CORE_MANAGEMNT_ADMIN
/

Prompt Privs on TABLE CONDITIONS_DATA_AUDITLOG TO CMS_&det._CORE_MANAGEMNT_READER;
GRANT SELECT ON  CONDITIONS_DATA_AUDITLOG TO CMS_&det._CORE_MANAGEMNT_READER
/

Prompt Privs on TABLE CONDITIONS_DATA_AUDITLOG TO CMS_&det._CORE_MANAGEMNT_WRITER;
GRANT INSERT, UPDATE ON  CONDITIONS_DATA_AUDITLOG TO CMS_&det._CORE_MANAGEMNT_WRITER
/