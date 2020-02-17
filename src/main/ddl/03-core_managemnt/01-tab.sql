
PROMPT Creating Table 'USERS'
CREATE TABLE USERS
 (RECORD_ID NUMBER(38,0) NOT NULL
 ,LOGIN_NAME VARCHAR2(64) NOT NULL
 ,INSTITUTION_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) NOT NULL
 ,RECORD_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,RECORD_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 ,PASSWORD VARCHAR2(32)
 ,USERGROUP VARCHAR2(32)
 ,FIRST_NAME VARCHAR2(64)
 ,LAST_NAME VARCHAR2(64)
 ,PHONE VARCHAR2(50)
 ,EMAIL VARCHAR2(200)
 ,FAX VARCHAR2(200)
 ,ZIP_CODE VARCHAR2(50)
 )
 TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'LOCATIONS'
CREATE TABLE LOCATIONS
 (LOCATION_ID NUMBER(38,0) NOT NULL
 ,INSTITUTION_ID NUMBER(38,0)
 ,IS_RECORD_DELETED CHAR(1) NOT NULL
 ,LOCATION_NAME VARCHAR2(40) NOT NULL
 ,RECORD_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,RECORD_INSERTION_USER VARCHAR2(50) NOT NULL
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 )
 TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'LOCATIONS_HST'
CREATE TABLE LOCATIONS_HST
 (HISTORY_RECORD_ID NUMBER(38,0) NOT NULL
 ,LOCATION_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) NOT NULL
 ,LOCATION_NAME VARCHAR2(40) NOT NULL
 ,HISTORY_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,HISTORY_INSERTION_USER VARCHAR2(50) NOT NULL
 ,INSTITUTION_ID NUMBER(38,0)
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 )
 TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'INSTITUTIONS'
CREATE TABLE INSTITUTIONS
 (INSTITUTION_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) NOT NULL
 ,INSTITUTE_CODE NUMBER
 ,RECORD_INSERTION_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_INSERTION_USER VARCHAR2(50)
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,NAME VARCHAR2(256)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 ,TOWN VARCHAR2(128)
 ,COUNTRY VARCHAR2(3)
 )
 TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Table 'INSTITUTIONS_HST'
CREATE TABLE INSTITUTIONS_HST
 (HISTORY_RECORD_ID NUMBER(38,0) NOT NULL
 ,INSTITUTION_ID NUMBER(38,0) NOT NULL
 ,IS_RECORD_DELETED CHAR(1) NOT NULL
 ,HISTORY_INSERTION_TIME TIMESTAMP WITH TIME ZONE NOT NULL
 ,HISTORY_INSERTION_USER VARCHAR2(50) NOT NULL
 ,INSTITUTE_CODE NUMBER
 ,RECORD_LASTUPDATE_TIME TIMESTAMP WITH TIME ZONE
 ,RECORD_LASTUPDATE_USER VARCHAR2(50)
 ,NAME VARCHAR2(256)
 ,COMMENT_DESCRIPTION VARCHAR2(4000)
 ,TOWN VARCHAR2(128)
 ,COUNTRY VARCHAR2(3)
 )
 TABLESPACE CMS_&det._PROCERNIT_DATA
/

