-- Create Channels table

CREATE TABLE "CMS_TST_TEST_COND"."TEST_CHANNELS" (	
  "CHANNEL_MAP_ID" NUMBER, 
	"SUBDET" VARCHAR2(20 BYTE), 
	"IETA" NUMBER, 
	"IPHI" NUMBER, 
	"DEPTH" NUMBER
   );

ALTER TABLE "CMS_TST_TEST_COND"."TEST_CHANNELS" ADD CONSTRAINT "TEST_CHANNEL_MAP_PK" PRIMARY KEY ("CHANNEL_MAP_ID");
ALTER TABLE "CMS_TST_TEST_COND"."TEST_CHANNELS" ADD CONSTRAINT TEST_CHANNELS_FK1 FOREIGN KEY (CHANNEL_MAP_ID) REFERENCES CMS_TST_CORE_COND.CHANNEL_MAPS_BASE (CHANNEL_MAP_ID) ENABLE;
ALTER TABLE "CMS_TST_TEST_COND"."TEST_CHANNELS" ADD CONSTRAINT TEST_CHANNELS_UK1 UNIQUE (SUBDET, IETA, IPHI, DEPTH) ENABLE;

GRANT SELECT on "CMS_TST_TEST_COND"."TEST_CHANNELS" to "PUBLIC" ;
GRANT SELECT, INSERT on "CMS_TST_TEST_COND"."TEST_CHANNELS" to "CMS_TST_PRTTYPE_TEST_WRITER" ;

-- Create Kind of condition

CREATE TABLE "CMS_TST_TEST_COND"."TEST_IV" (	
  "RECORD_ID" NUMBER, 
	"CONDITION_DATA_SET_ID" NUMBER, 
	"VOLTAGE" NUMBER, 
	"CURR" NUMBER, 
	"COMMENT_DESCRIPTION" VARCHAR2(128 BYTE)
   );
   
ALTER TABLE "CMS_TST_TEST_COND"."TEST_IV" ADD CONSTRAINT "TEST_IV_PK" PRIMARY KEY ("RECORD_ID");
ALTER TABLE "CMS_TST_TEST_COND"."TEST_IV" ADD CONSTRAINT TEST_IV_FK1 FOREIGN KEY (CONDITION_DATA_SET_ID) REFERENCES CMS_TST_CORE_COND.COND_DATA_SETS (CONDITION_DATA_SET_ID) ENABLE;
   
GRANT SELECT, INSERT on "CMS_TST_TEST_COND"."TEST_IV" to "CMS_TST_PRTTYPE_TEST_WRITER" ;
-- GRANT SELECT, INSERT on "CMS_TST_TEST_COND"."TEST_CHANNELS" to "CMS_TST_CORE_COND" ;
-- Create Kind of condition with LOBs

CREATE TABLE "CMS_TST_TEST_COND"."TEST_FILES" (	
  "RECORD_ID" NUMBER NOT NULL ENABLE, 
	"CONDITION_DATA_SET_ID" NUMBER NOT NULL ENABLE, 
	"TEST_TEXT_FILE" VARCHAR2(256 BYTE), 
	"TEST_MEDIA_FILE" VARCHAR2(256 BYTE), 
	"TEST_TEXT_CLOB" CLOB, 
	"TEST_MEDIA_BLOB" BLOB, 
	"TEST_COMMENT" VARCHAR2(20 BYTE), 
	 CONSTRAINT "TEST_FILES_PK" PRIMARY KEY ("RECORD_ID")
); 

ALTER TABLE "CMS_TST_TEST_COND"."TEST_FILES" ADD CONSTRAINT TEST_FILES_FK1 FOREIGN KEY (CONDITION_DATA_SET_ID) REFERENCES CMS_TST_CORE_COND.COND_DATA_SETS (CONDITION_DATA_SET_ID) ENABLE;

GRANT SELECT, INSERT on "CMS_TST_TEST_COND"."TEST_FILES" to "CMS_TST_PRTTYPE_TEST_WRITER" ;

CREATE TABLE "CMS_TST_TEST_COND"."TEST_COORDINATES" (	
  "CHANNEL_MAP_ID" NUMBER, 
	"X" NUMBER, 
	"Y" NUMBER, 
	"Z" NUMBER,
	"NAME" VARCHAR2(20 BYTE)
   );

ALTER TABLE "CMS_TST_TEST_COND"."TEST_COORDINATES" ADD CONSTRAINT "TEST_COORD_MAP_PK" PRIMARY KEY ("CHANNEL_MAP_ID");
ALTER TABLE "CMS_TST_TEST_COND"."TEST_COORDINATES" ADD CONSTRAINT TEST_COORDINATES_FK1 FOREIGN KEY (CHANNEL_MAP_ID) REFERENCES CMS_TST_CORE_COND.CHANNEL_MAPS_BASE (CHANNEL_MAP_ID) ENABLE;
ALTER TABLE "CMS_TST_TEST_COND"."TEST_COORDINATES" ADD CONSTRAINT TEST_COORDINATES_UK1 UNIQUE (X, Y, Z) ENABLE;

GRANT SELECT on "CMS_TST_TEST_COND"."TEST_COORDINATES" to "PUBLIC" ;
GRANT SELECT, INSERT on "CMS_TST_TEST_COND"."TEST_COORDINATES" to "CMS_TST_PRTTYPE_TEST_WRITER" ;
