
PROMPT Creating Table 'ATTR_CATALOGS_HST'
CREATE TABLE ATTR_CATALOGS_HST
 (HISTORY_RECORD_ID NUMBER(38,0) NOT NULL
 ,ATTR_CATALOG_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) DEFAULT 'F' NOT NULL
 ,DISPLAY_NAME VARCHAR2(40) NOT NULL
 ,HISTORY_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,HISTORY_INSERTION_USER VARCHAR2(50) NOT NULL
 ,IS_UNIQUE_WITHIN_PARENT CHAR(1) DEFAULT 'F'
 ,IS_MULTIPLE_ATTRS CHAR(1) DEFAULT 'F'
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 )
/

PROMPT Creating Table 'EXT_POSITION_SCHEMAS'
CREATE TABLE EXT_POSITION_SCHEMAS
 (ATTRIBUTE_ID NUMBER(38,0) NOT NULL
 ,NAME VARCHAR2(40) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) NOT NULL
 ,RECORD_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,RECORD_INSERTION_USER VARCHAR2(50) NOT NULL
 ,ALIAS_A VARCHAR2(40)
 ,ALIAS_B VARCHAR2(40)
 ,ALIAS_C VARCHAR2(40)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 )
/

PROMPT Creating Table 'POSITION_SCHEMAS'
CREATE TABLE POSITION_SCHEMAS
 (ATTRIBUTE_ID NUMBER(38,0) NOT NULL
 ,NAME VARCHAR2(40) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) NOT NULL
 ,RECORD_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,RECORD_INSERTION_USER VARCHAR2(50) NOT NULL
 )
/

PROMPT Creating Table 'ATTR_CATALOGS'
CREATE TABLE ATTR_CATALOGS
 (ATTR_CATALOG_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) DEFAULT 'F' NOT NULL
 ,DISPLAY_NAME VARCHAR2(40) NOT NULL
 ,RECORD_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,RECORD_INSERTION_USER VARCHAR2(50) NOT NULL
 ,IS_UNIQUE_WITHIN_PARENT CHAR(1) DEFAULT 'F'
 ,IS_MULTIPLE_ATTRS CHAR(1) DEFAULT 'F'
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 )
/

PROMPT Creating Table 'COND_ALGORITHMS'
CREATE TABLE COND_ALGORITHMS
 (ATTRIBUTE_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) NOT NULL
 ,NAME VARCHAR2(40) NOT NULL
 ,RECORD_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,RECORD_INSERTION_USER VARCHAR2(50) NOT NULL
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 ,SOFTWARE_RELEASE_VERSION VARCHAR2(50)
 )
/

PROMPT Creating Table 'ATTR_BASES'
CREATE TABLE ATTR_BASES
 (ATTRIBUTE_ID NUMBER(38,0) NOT NULL
 ,ATTR_CATALOG_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) DEFAULT 'F' NOT NULL
 ,EXTENSION_TABLE_NAME VARCHAR2(30) NOT NULL
 ,RECORD_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,RECORD_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_DEL_FLAG_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_DEL_FLAG_USER VARCHAR2(50)
 )
/

PROMPT Creating Table 'MODES_STAGES'
CREATE TABLE MODES_STAGES
 (ATTRIBUTE_ID NUMBER(38,0) NOT NULL
 ,NAME VARCHAR2(40) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) NOT NULL
 ,RECORD_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,RECORD_INSERTION_USER VARCHAR2(50) NOT NULL
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 )
/

