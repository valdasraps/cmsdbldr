
PROMPT Creating Table 'KIND_OF_CONDITIONS_HST'
CREATE TABLE KIND_OF_CONDITIONS_HST
 (HISTORY_RECORD_ID NUMBER(38,0) NOT NULL
 ,KIND_OF_CONDITION_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) NOT NULL
 ,NAME VARCHAR2(40) NOT NULL
 ,EXTENSION_TABLE_NAME VARCHAR2(30) NOT NULL
 ,HISTORY_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,HISTORY_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 )
 TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'COND_DATA_SETS'
CREATE TABLE COND_DATA_SETS
 (CONDITION_DATA_SET_ID NUMBER(38,0) NOT NULL
 ,PART_ID NUMBER(38,0)
 ,COND_RUN_ID NUMBER(38,0) NOT NULL
 ,CHANNEL_MAP_ID NUMBER(38,0)
 ,KIND_OF_CONDITION_ID NUMBER(38,0) NOT NULL
 ,AGGREGATED_COND_DATA_SET_ID NUMBER(38,0)
 ,IS_RECORD_DELETED CHAR(1) NOT NULL
 ,EXTENSION_TABLE_NAME VARCHAR2(30) NOT NULL
 ,RECORD_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,RECORD_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_DEL_FLAG_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_DEL_FLAG_USER VARCHAR2(50)
 ,DATA_FILE_NAME VARCHAR2(4000)
 ,IMAGE_FILE_NAME VARCHAR2(4000)
 ,VERSION VARCHAR2(40)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 ,CREATE_TIMESTAMP TIMESTAMP (6) WITH TIME ZONE
 ,CREATED_BY_USER VARCHAR2(50 BYTE)
 ,SUBVERSION NUMBER(38,0)
 ,NUMBER_OF_EVENTS_IN_SET NUMBER(38,0)
 ,SET_NUMBER NUMBER(38,0)
 ,SET_BEGIN_TIMESTAMP TIMESTAMP (6) WITH TIME ZONE
 ,SET_END_TIMESTAMP TIMESTAMP (6) WITH TIME ZONE
 ,SET_STATUS NUMBER(38,0)
 )
 TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'COND_TO_ATTR_RLTNSHPS_HST'
CREATE TABLE COND_TO_ATTR_RLTNSHPS_HST
 (HISTORY_RECORD_ID NUMBER(38,0) NOT NULL
 ,RELATIONSHIP_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) NOT NULL
 ,DISPLAY_NAME VARCHAR2(80) NOT NULL
 ,HISTORY_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,HISTORY_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 )
 TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'COND_TO_PART_RLTNSHPS'
CREATE TABLE COND_TO_PART_RLTNSHPS
 (RELATIONSHIP_ID NUMBER(38,0) NOT NULL
 ,KIND_OF_PART_ID NUMBER(38,0) NOT NULL
 ,KIND_OF_CONDITION_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) NOT NULL
 ,DISPLAY_NAME VARCHAR2(80) NOT NULL
 ,RECORD_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,RECORD_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 )
 TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'COND_TO_ATTR_RLTNSHPS'
CREATE TABLE COND_TO_ATTR_RLTNSHPS
 (RELATIONSHIP_ID NUMBER(38,0) NOT NULL
 ,KIND_OF_CONDITION_ID NUMBER(38,0) NOT NULL
 ,ATTR_CATALOG_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) NOT NULL
 ,DISPLAY_NAME VARCHAR2(80) NOT NULL
 ,RECORD_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,RECORD_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 )
 TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'KINDS_OF_CONDITIONS'
CREATE TABLE KINDS_OF_CONDITIONS
 (KIND_OF_CONDITION_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) NOT NULL
 ,NAME VARCHAR2(40) NOT NULL
 ,EXTENSION_TABLE_NAME VARCHAR2(30) NOT NULL
 ,RECORD_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,RECORD_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 ,CATEGORY_NAME VARCHAR2(50)
 )
 TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'COND_ATTR_LISTS'
CREATE TABLE COND_ATTR_LISTS
 (ATTR_LIST_RECORD_ID NUMBER(38,0) NOT NULL
 ,RELATIONSHIP_ID NUMBER(38,0) NOT NULL
 ,CONDITION_DATA_SET_ID NUMBER(38,0) NOT NULL
 ,ATTRIBUTE_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) NOT NULL
 ,RECORD_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,RECORD_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_DEL_FLAG_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_DEL_FLAG_USER VARCHAR2(50)
 )
 TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'CHANNEL_MAPS_BASE'
CREATE TABLE CHANNEL_MAPS_BASE
 (CHANNEL_MAP_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) NOT NULL
 ,EXTENSION_TABLE_NAME VARCHAR2(30) NOT NULL
 ,RECORD_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,RECORD_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_DEL_FLAG_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_DEL_FLAG_USER VARCHAR2(50)
 )
 TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'COND_TO_PART_RLTNSHPS_HST'
CREATE TABLE COND_TO_PART_RLTNSHPS_HST
 (HISTORY_RECORD_ID NUMBER(38,0) NOT NULL
 ,RELATIONSHIP_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) NOT NULL
 ,DISPLAY_NAME VARCHAR2(80) NOT NULL
 ,HISTORY_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,HISTORY_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 )
 TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'COND_RUNS'
CREATE TABLE COND_RUNS
 (COND_RUN_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) NOT NULL
 ,RUN_BEGIN_TIMESTAMP DATE NOT NULL
 ,RUN_NAME VARCHAR2(255)
 ,RECORD_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,RECORD_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,RUN_END_TIMESTAMP DATE
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 ,INITIATED_BY_USER VARCHAR2(80)
 ,LOCATION VARCHAR2(40)
 ,RUN_TYPE VARCHAR2(120)
 ,RUN_NUMBER NUMBER(38)
 )
 TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'COND_RUNS_HST'
CREATE TABLE COND_RUNS_HST
 (HISTORY_RECORD_ID NUMBER(38,0) NOT NULL
 ,COND_RUN_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) NOT NULL
 ,RUN_BEGIN_TIMESTAMP DATE NOT NULL
 ,RUN_NAME VARCHAR2(255)
 ,HISTORY_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,HISTORY_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,RUN_END_TIMESTAMP DATE
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 ,INITIATED_BY_USER VARCHAR2(80)
 )
 TABLESPACE CMS_&det._PROCERNIT_DATA
/

