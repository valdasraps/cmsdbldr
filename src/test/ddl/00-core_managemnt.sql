INSERT INTO CMS_&det._CORE_MANAGEMNT.INSTITUTIONS (INSTITUTION_ID,IS_RECORD_DELETED,INSTITUTE_CODE,NAME)
SELECT -1,'F',123,'Perrugia' from dual;

INSERT INTO CMS_&det._CORE_MANAGEMNT.INSTITUTIONS (INSTITUTION_ID,IS_RECORD_DELETED,INSTITUTE_CODE,NAME)
SELECT -2,'F',124,'Catania' from dual;

INSERT INTO CMS_&det._CORE_MANAGEMNT.LOCATIONS (LOCATION_ID,IS_RECORD_DELETED,INSTITUTION_ID,LOCATION_NAME)
SELECT -1,'F',INSTITUTION_ID,'Perrugia' from CMS_&det._CORE_MANAGEMNT.INSTITUTIONS where INSTITUTE_CODE = 123;

INSERT INTO CMS_&det._CORE_MANAGEMNT.LOCATIONS (LOCATION_ID,IS_RECORD_DELETED,INSTITUTION_ID,LOCATION_NAME)
SELECT -2,'F',INSTITUTION_ID,'Catania' from CMS_&det._CORE_MANAGEMNT.INSTITUTIONS where INSTITUTE_CODE = 124;
