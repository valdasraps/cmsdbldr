-- Create Channels table

CREATE TABLE "CMS_TST_TEST_COND"."TEST_CHANNELS" (	
  "CHANNEL_MAP_ID" NUMBER, 
	"SUBDET" VARCHAR2(20 BYTE), 
	"IETA" NUMBER, 
	"IPHI" NUMBER, 
	"DEPTH" NUMBER
   );

ALTER TABLE "CMS_TST_TEST_COND"."TEST_CHANNELS" ADD CONSTRAINT "TEST_CHANNEL_MAP_PK" PRIMARY KEY ("CHANNEL_MAP_ID");

GRANT SELECT on "CMS_TST_TEST_COND"."TEST_CHANNELS" to "PUBLIC" ;

DECLARE
  CH_ID number;
BEGIN
  FOR IETA IN 1..10 LOOP
    FOR IPHI IN 1..10 LOOP
      FOR DEPTH IN 1..10 LOOP
          
        SELECT CMS_TST_CORE_COND.ANY_COND_RECORD_ID_SEQ.NEXTVAL INTO CH_ID FROM dual;

        INSERT INTO CMS_TST_TEST_COND.TEST_CHANNELS (CHANNEL_MAP_ID, SUBDET, IETA, IPHI, DEPTH) 
        VALUES (CH_ID, 'TEST', IETA, IPHI, DEPTH);
        
      END LOOP;
    END LOOP;
  END LOOP;
END;
/

-- Create Kind of condition

CREATE TABLE "CMS_TST_TEST_COND"."TEST_IV" (	
  "RECORD_ID" NUMBER, 
	"CONDITION_DATA_SET_ID" NUMBER, 
	"VOLTAGE" NUMBER, 
	"CURR" NUMBER, 
	"COMMENT_DESCRIPTION" VARCHAR2(128 BYTE)
   );
   
ALTER TABLE "CMS_TST_TEST_COND"."TEST_IV" ADD CONSTRAINT "TEST_IV_PK" PRIMARY KEY ("RECORD_ID");
   
GRANT SELECT, INSERT on "CMS_TST_TEST_COND"."TEST_IV" to "CMS_TST_PRTTYPE_TEST_WRITER" ;

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

GRANT SELECT, INSERT on "CMS_TST_TEST_COND"."TEST_FILES" to "CMS_TST_PRTTYPE_TEST_WRITER" ;