
PROMPT Creating Table 'PARTS_HST'
CREATE TABLE PARTS_HST
 (HISTORY_RECORD_ID NUMBER(38,0) NOT NULL
 ,PART_ID NUMBER(38,0) NOT NULL
 ,KIND_OF_PART_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) DEFAULT 'F' NOT NULL
 ,HISTORY_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,HISTORY_INSERTION_USER VARCHAR2(50) NOT NULL
 ,LOCATION_ID NUMBER(38,0)
 ,MANUFACTURER_ID NUMBER(38,0)
 ,BARCODE VARCHAR2(40)
 ,SERIAL_NUMBER VARCHAR2(40)
 ,VERSION VARCHAR2(40)
 ,NAME_LABEL VARCHAR2(40)
 ,INSTALLED_DATE TIMESTAMP WITH TIME ZONE
 ,REMOVED_DATE TIMESTAMP WITH TIME ZONE
 ,INSTALLED_BY_USER VARCHAR2(50)
 ,REMOVED_BY_USER VARCHAR2(50)
 ,EXTENSION_TABLE_NAME VARCHAR2(30)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 )
 PCTFREE 10
 STORAGE
 (
   INITIAL 10M
   NEXT 10M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'KINDS_OF_PARTS'
CREATE TABLE KINDS_OF_PARTS
 (KIND_OF_PART_ID NUMBER(38,0) NOT NULL
 ,SUBDETECTOR_ID NUMBER(38,0) NOT NULL
 ,MANUFACTURER_ID NUMBER(38,0)
 ,IS_IMAGINARY_PART CHAR(1) NOT NULL
 ,DISPLAY_NAME VARCHAR2(40) NOT NULL
 ,IS_DETECTOR_PART CHAR(1) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) DEFAULT 'F' NOT NULL
 ,EXTENSION_TABLE_NAME VARCHAR2(30) NOT NULL
 ,RECORD_INSERTION_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_INSERTION_USER VARCHAR2(50)
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 ,LPNAME VARCHAR2(24)
 )
 PCTFREE 10
 STORAGE
 (
   INITIAL 10M
   NEXT 10M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'PHYSICAL_PARTS_TREE_HST'
CREATE TABLE PHYSICAL_PARTS_TREE_HST
 (HISTORY_RECORD_ID NUMBER(38,0) NOT NULL
 ,PART_ID NUMBER(38,0) NOT NULL
 ,PART_PARENT_ID NUMBER(38,0) NOT NULL
 ,RELATIONSHIP_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) DEFAULT 'F' NOT NULL
 ,HISTORY_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,HISTORY_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,DDD_PART_NAME VARCHAR2(32)
 )
 PCTFREE 10
 STORAGE
 (
   INITIAL 10M
   NEXT 10M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'SIGNAL_CONNECTIONS'
CREATE TABLE SIGNAL_CONNECTIONS
 (CONNNECTION_ID NUMBER(38) NOT NULL
 ,CABLE_PART_ID NUMBER(38,0) NOT NULL
 ,PART_BEGIN_SIDE_ID NUMBER(38,0) NOT NULL
 ,CONNECTOR_BEGIN_SIDE_ID NUMBER(38,0) NOT NULL
 ,CONNECTOR_END_SIDE_ID NUMBER(38,0) NOT NULL
 ,PART_END_SIDE_ID NUMBER(38,0) NOT NULL
 ,SIGNAL_CONNECTION_TYPE_ID NUMBER NOT NULL
 ,IS_RECORD_DELETED CHAR(1) DEFAULT 'F' NOT NULL
 ,RECORD_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,RECORD_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_DEL_FLAG_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_DEL_FLAG_USER VARCHAR2(50)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 )
 PCTFREE 10
 STORAGE
 (
   INITIAL 10M
   NEXT 10M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

COMMENT ON COLUMN SIGNAL_CONNECTIONS.CONNNECTION_ID IS 'generated the sequence number as primary key'
/

COMMENT ON COLUMN SIGNAL_CONNECTIONS.CONNECTOR_BEGIN_SIDE_ID IS 'generated the sequence number as primary key'
/

COMMENT ON COLUMN SIGNAL_CONNECTIONS.CONNECTOR_END_SIDE_ID IS 'generated the sequence number as primary key'
/

PROMPT Creating Table 'KINDS_OF_PARTS_HST'
CREATE TABLE KINDS_OF_PARTS_HST
 (HISTORY_RECORD_ID NUMBER(38,0) NOT NULL
 ,KIND_OF_PART_ID NUMBER(38,0) NOT NULL
 ,SUBDETECTOR_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) DEFAULT 'F' NOT NULL
 ,DISPLAY_NAME VARCHAR2(40) NOT NULL
 ,IS_IMAGINARY_PART CHAR(1) NOT NULL
 ,IS_DETECTOR_PART CHAR(1) NOT NULL
 ,EXTENSION_TABLE_NAME VARCHAR2(30) NOT NULL
 ,HISTORY_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,HISTORY_INSERTION_USER VARCHAR2(50) NOT NULL
 ,LPNAME VARCHAR2(24)
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 ,MANUFACTURER_ID NUMBER(38,0)
 )
 PCTFREE 10
 STORAGE
 (
   INITIAL 10M
   NEXT 10M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'PART_TO_ATTR_RLTNSHPS_HST'
CREATE TABLE PART_TO_ATTR_RLTNSHPS_HST
 (HISTORY_RECORD_ID NUMBER(38,0) NOT NULL
 ,RELATIONSHIP_ID NUMBER(38,0) NOT NULL
 ,KIND_OF_PART_ID NUMBER(38,0) NOT NULL
 ,ATTR_CATALOG_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) DEFAULT 'F' NOT NULL
 ,DISPLAY_NAME VARCHAR2(80) NOT NULL
 ,HISTORY_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,HISTORY_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 )
 PCTFREE 10
 STORAGE
 (
   INITIAL 10M
   NEXT 10M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'SUBDETECTORS'
CREATE TABLE SUBDETECTORS
 (SUBDETECTOR_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) DEFAULT 'F' NOT NULL
 ,RECORD_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,RECORD_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 ,SUBDETECTOR_NAME VARCHAR2(40)
 )
 PCTFREE 10
 STORAGE
 (
   INITIAL 10M
   NEXT 10M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'CONNECTORS'
CREATE TABLE CONNECTORS
 (CONNECTOR_ID NUMBER(38,0) NOT NULL
 ,PART_ID NUMBER(38,0) NOT NULL
 ,IS_INPUT_SIGNAL_DIRECTION CHAR(1) NOT NULL
 ,CONNECTOR_LABEL VARCHAR2(10) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) DEFAULT 'F' NOT NULL
 ,IS_MALE_SIDE CHAR(1) NOT NULL
 ,RECORD_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,RECORD_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 )
 PCTFREE 10
 STORAGE
 (
   INITIAL 10M
   NEXT 10M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

COMMENT ON COLUMN CONNECTORS.CONNECTOR_ID IS 'generated the sequence number as primary key'
/

PROMPT Creating Table 'PART_TO_PART_RLTNSHPS'
CREATE TABLE PART_TO_PART_RLTNSHPS
 (RELATIONSHIP_ID NUMBER(38,0) NOT NULL
 ,KIND_OF_PART_ID NUMBER(38,0) NOT NULL
 ,KIND_OF_PART_ID_CHILD NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) DEFAULT 'F' NOT NULL
 ,PRIORITY_NUMBER NUMBER(3) NOT NULL
 ,DISPLAY_NAME VARCHAR2(80) NOT NULL
 ,RECORD_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,RECORD_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 )
 PCTFREE 10
 STORAGE
 (
   INITIAL 10M
   NEXT 10M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'MANUFACTURERS_HST'
CREATE TABLE MANUFACTURERS_HST
 (HISTORY_RECORD_ID NUMBER(38,0) NOT NULL
 ,MANUFACTURER_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) DEFAULT 'F' NOT NULL
 ,MANUFACTURER_NAME VARCHAR2(40) NOT NULL
 ,HISTORY_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,HISTORY_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 )
 PCTFREE 10
 STORAGE
 (
   INITIAL 10M
   NEXT 10M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'PART_ATTR_LISTS'
CREATE TABLE PART_ATTR_LISTS
 (ATTR_LIST_RECORD_ID NUMBER(38,0) NOT NULL
 ,PART_ID NUMBER(38,0) NOT NULL
 ,ATTRIBUTE_ID NUMBER(38,0) NOT NULL
 ,RELATIONSHIP_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) DEFAULT 'F' NOT NULL
 ,RECORD_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,RECORD_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_DEL_FLAG_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_DEL_FLAG_USER VARCHAR2(50)
 )
 PCTFREE 10
 STORAGE
 (
   INITIAL 10M
   NEXT 10M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'SIGNAL_CONNECTION_TYPES'
CREATE TABLE SIGNAL_CONNECTION_TYPES
 (SIGNAL_CONNECTION_TYPE_ID NUMBER NOT NULL
 ,KIND_OF_PART_END_SIDE_ID NUMBER(38,0) NOT NULL
 ,KIND_OF_PART_BEGIN_SIDE_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) DEFAULT 'F' NOT NULL
 ,RECORD_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,RECORD_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 )
 PCTFREE 10
 STORAGE
 (
   INITIAL 10M
   NEXT 10M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'PART_TO_ATTR_RLTNSHPS'
CREATE TABLE PART_TO_ATTR_RLTNSHPS
 (RELATIONSHIP_ID NUMBER(38,0) NOT NULL
 ,ATTR_CATALOG_ID NUMBER(38,0) NOT NULL
 ,KIND_OF_PART_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) DEFAULT 'F' NOT NULL
 ,DISPLAY_NAME VARCHAR2(80) NOT NULL
 ,RECORD_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,RECORD_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 )
 PCTFREE 10
 STORAGE
 (
   INITIAL 10M
   NEXT 10M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'PARTS'
CREATE TABLE PARTS
 (PART_ID NUMBER(38,0) NOT NULL
 ,KIND_OF_PART_ID NUMBER(38,0) NOT NULL
 ,LOCATION_ID NUMBER(38,0)
 ,MANUFACTURER_ID NUMBER(38,0)
 ,IS_RECORD_DELETED CHAR(1) DEFAULT 'F' NOT NULL
 ,RECORD_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,RECORD_INSERTION_USER VARCHAR2(50) NOT NULL
 ,BARCODE VARCHAR2(40)
 ,SERIAL_NUMBER VARCHAR2(40)
 ,VERSION VARCHAR2(40)
 ,NAME_LABEL VARCHAR2(40)
 ,INSTALLED_DATE TIMESTAMP WITH TIME ZONE
 ,REMOVED_DATE TIMESTAMP WITH TIME ZONE
 ,INSTALLED_BY_USER VARCHAR2(50)
 ,REMOVED_BY_USER VARCHAR2(50)
 ,EXTENSION_TABLE_NAME VARCHAR2(30)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 )
 PCTFREE 10
 STORAGE
 (
   INITIAL 10M
   NEXT 10M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'SIGNAL_CONNECTION_TYPES_HST'
CREATE TABLE SIGNAL_CONNECTION_TYPES_HST
 (HISTORY_RECORD_ID NUMBER NOT NULL
 ,SIGNAL_CONNECTION_TYPE_ID NUMBER NOT NULL
 ,KIND_OF_PART_END_SIDE_ID NUMBER(38,0) NOT NULL
 ,KIND_OF_PART_BEGIN_SIDE_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) DEFAULT 'F' NOT NULL
 ,HISTORY_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,HISTORY_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 )
 PCTFREE 10
 STORAGE
 (
   INITIAL 10M
   NEXT 10M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'SUBDETECTORS_HST'
CREATE TABLE SUBDETECTORS_HST
 (HISTORY_RECORD_ID NUMBER(38,0) NOT NULL
 ,SUBDETECTOR_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) DEFAULT 'F' NOT NULL
 ,HISTORY_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,HISTORY_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 ,SUBDETECTOR_NAME VARCHAR2(40)
 )
 PCTFREE 10
 STORAGE
 (
   INITIAL 10M
   NEXT 10M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'PHYSICAL_PARTS_TREE'
CREATE TABLE PHYSICAL_PARTS_TREE
 (PART_ID NUMBER(38,0) NOT NULL
 ,RELATIONSHIP_ID NUMBER(38,0) NOT NULL
 ,PART_PARENT_ID NUMBER(38,0)
 ,IS_RECORD_DELETED CHAR(1) DEFAULT 'F' NOT NULL
 ,RECORD_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,RECORD_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,DDD_PART_NAME VARCHAR2(32)
 )
 PCTFREE 10
 STORAGE
 (
   INITIAL 10M
   NEXT 10M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'CONNECTORS_HST'
CREATE TABLE CONNECTORS_HST
 (HISTORY_RECORD_ID NUMBER(38,0) NOT NULL
 ,CONNECTOR_ID NUMBER(38,0) NOT NULL
 ,PART_ID NUMBER(38,0) NOT NULL
 ,IS_INPUT_SIGNAL_DIRECTION CHAR(1) NOT NULL
 ,CONNECTOR_LABEL VARCHAR2(10) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) DEFAULT 'F' NOT NULL
 ,IS_MALE_SIDE CHAR(1) NOT NULL
 ,HISTORY_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,HISTORY_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 )
 PCTFREE 10
 STORAGE
 (
   INITIAL 10M
   NEXT 10M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

COMMENT ON COLUMN CONNECTORS_HST.HISTORY_RECORD_ID IS 'generated the sequence number as primary key'
/

COMMENT ON COLUMN CONNECTORS_HST.CONNECTOR_ID IS 'generated the sequence number as primary key'
/

PROMPT Creating Table 'MANUFACTURERS'
CREATE TABLE MANUFACTURERS
 (MANUFACTURER_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) DEFAULT 'F' NOT NULL
 ,MANUFACTURER_NAME VARCHAR2(40) NOT NULL
 ,RECORD_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,RECORD_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 )
 PCTFREE 10
 STORAGE
 (
   INITIAL 10M
   NEXT 10M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'LOGICALPARTTYPES'
CREATE TABLE LOGICALPARTTYPES
 (LPNAME VARCHAR2(24) NOT NULL
 )
 PCTFREE 10
 STORAGE
 (
   INITIAL 10M
   NEXT 10M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'PART_TO_PART_RLTNSHPS_HST'
CREATE TABLE PART_TO_PART_RLTNSHPS_HST
 (HISTORY_RECORD_ID NUMBER(38,0) NOT NULL
 ,RELATIONSHIP_ID NUMBER(38,0) NOT NULL
 ,KIND_OF_PART_ID NUMBER(38,0) NOT NULL
 ,KIND_OF_PART_ID_CHILD NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) DEFAULT 'F' NOT NULL
 ,PRIORITY_NUMBER NUMBER(3) NOT NULL
 ,DISPLAY_NAME VARCHAR2(80) NOT NULL
 ,HISTORY_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,HISTORY_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 )
 PCTFREE 10
 STORAGE
 (
   INITIAL 10M
   NEXT 10M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

