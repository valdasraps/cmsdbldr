CREATE SEQUENCE  "ANY_ASSEMBLY_ID_SEQ"
 INCREMENT BY 20
 START WITH 1000
 NOMAXVALUE
 MINVALUE 1000
 NOCYCLE
 CACHE 20
/

CREATE SEQUENCE  "ANY_ASSEMBLY_HISTORY_ID_SEQ"
 INCREMENT BY 20
 START WITH 1000
 NOMAXVALUE
 MINVALUE 1000
 NOCYCLE
 CACHE 20
/

--------------------------------------------------------
--  Tables
--------------------------------------------------------

CREATE TABLE "ASSEMBLY_ATTRIBUTE_DEFINITIONS" (
    "AAD_ID" NUMBER, 
    "AAD_APD_ID" NUMBER, 
    "AAD_ATTRIBUTE_ID" NUMBER(38,0), 
    "AAD_GET_ACTION" CHAR(1 BYTE),
    "AAD_SET_ACTION" CHAR(1 BYTE),
    "AAD_SET_STATUS" VARCHAR2(30 BYTE)
   );

  CREATE TABLE "ASSEMBLY_DATA" (
    "AED_ID" NUMBER, 
    "AED_ADD_ID" NUMBER, 
    "AED_ASP_ID" NUMBER, 
    "AED_DATA_SET_ID" NUMBER(38,0)
   );

  CREATE TABLE "ASSEMBLY_DATA_DEFINITIONS" (
    "ADD_ID" NUMBER, 
    "ADD_NUMBER" NUMBER, 
    "ADD_KOC_ID" NUMBER(38,0), 
    "ADD_APD_ID" NUMBER, 
    "ADD_NAME" VARCHAR2(30 BYTE), 
    "ADD_DESCR" VARCHAR2(400 BYTE)
   );

  CREATE TABLE "ASSEMBLY_PART_DEFINITIONS" (
    "APD_ID" NUMBER, 
    "APD_NUMBER" NUMBER,
    "APD_APD_ID" NUMBER,
    "APD_ASD_ID" NUMBER, 
    "APD_KOP_ID" NUMBER(38,0), 
    "APD_TYPE" VARCHAR2(30 BYTE),
    "APD_NAME" VARCHAR2(30 BYTE), 
    "APD_DESCR" VARCHAR2(400 BYTE)
   );

  CREATE TABLE "ASSEMBLY_PARTS" (
    "ASP_ID" NUMBER, 
    "ASP_APD_ID" NUMBER, 
    "ASP_ASS_ID" NUMBER, 
    "ASP_PART_ID" NUMBER(38,0)
   );

  CREATE TABLE "ASSEMBLY_PROCESSES" (
    "APR_ID" NUMBER, 
    "APR_NAME" VARCHAR2(30 BYTE), 
    "APR_DESCR" VARCHAR2(400 BYTE), 
    "APR_PRODUCT_KOP_ID" NUMBER
   );

  CREATE TABLE "ASSEMBLY_STEP_DEFINITIONS" (
    "ASD_ID" NUMBER, 
    "ASD_NUMBER" NUMBER, 
    "ASD_APR_ID" NUMBER, 
    "ASD_NAME" VARCHAR2(30 BYTE), 
    "ASD_DESCR" VARCHAR2(400 BYTE)
   );

  CREATE TABLE "ASSEMBLY_STEPS" (
    "ASS_ID" NUMBER, 
    "ASS_ASD_ID" NUMBER, 
    "ASS_LOCATION_ID" NUMBER(38,0), 
    "ASS_STATUS" VARCHAR2(30 BYTE), 
    "ASS_PART_ID" NUMBER, 
    "COMMENT_DESCRIPTION" VARCHAR2(400 BYTE), 
    "RECORD_INSERTION_TIME" TIMESTAMP (6), 
    "RECORD_LASTUPDATE_TIME" TIMESTAMP (6), 
    "RECORD_INSERTION_USER" VARCHAR2(50 BYTE), 
    "RECORD_LASTUPDATE_USER" VARCHAR2(50 BYTE)
   );

  CREATE TABLE "ASSEMBLY_SAME_LOCATIONS" (
    "LOCATION_ID" NUMBER, 
    "LOCATION_ID_SAME" NUMBER
   );

--------------------------------------------------------
--  History tables
--------------------------------------------------------

CREATE TABLE "ASSEMBLY_DATA_HST" (
    "HISTORY_RECORD_ID" NUMBER(32,0) NOT NULL ENABLE,
    "AED_ID" NUMBER, 
    "AED_ADD_ID" NUMBER, 
    "AED_ASP_ID" NUMBER, 
    "AED_DATA_SET_ID" NUMBER(38,0),
    CONSTRAINT "ASSEMBLY_DATA_HST_PK" PRIMARY KEY ("HISTORY_RECORD_ID")
);

CREATE TABLE "ASSEMBLY_PARTS_HST" (
    "HISTORY_RECORD_ID" NUMBER(32,0) NOT NULL ENABLE,
    "ASP_ID" NUMBER, 
    "ASP_APD_ID" NUMBER, 
    "ASP_ASS_ID" NUMBER, 
    "ASP_PART_ID" NUMBER(38,0),
    CONSTRAINT "ASSEMBLY_PARTS_HST_PK" PRIMARY KEY ("HISTORY_RECORD_ID")
);

CREATE TABLE "ASSEMBLY_STEPS_HST" (
    "HISTORY_RECORD_ID" NUMBER(32,0) NOT NULL ENABLE,
    "ASS_ID" NUMBER, 
    "ASS_ASD_ID" NUMBER, 
    "ASS_LOCATION_ID" NUMBER(38,0), 
    "ASS_STATUS" VARCHAR2(30 BYTE), 
    "ASS_PART_ID" NUMBER, 
    "COMMENT_DESCRIPTION" VARCHAR2(400 BYTE), 
    "RECORD_INSERTION_TIME" TIMESTAMP (6), 
    "RECORD_LASTUPDATE_TIME" TIMESTAMP (6), 
    "RECORD_INSERTION_USER" VARCHAR2(50 BYTE), 
    "RECORD_LASTUPDATE_USER" VARCHAR2(50 BYTE),
    CONSTRAINT "ASSEMBLY_STEPS_HST_PK" PRIMARY KEY ("HISTORY_RECORD_ID")
);

--------------------------------------------------------
--  Indexes
--------------------------------------------------------

  CREATE UNIQUE INDEX "AAD_PK" ON "ASSEMBLY_ATTRIBUTE_DEFINITIONS" ("AAD_ID");
  CREATE UNIQUE INDEX "AED_PK" ON "ASSEMBLY_DATA" ("AED_ID");
  CREATE UNIQUE INDEX "AED_UK" ON "ASSEMBLY_DATA" ("AED_ADD_ID", "AED_ASP_ID", "AED_DATA_SET_ID");
  CREATE UNIQUE INDEX "ADD_PK" ON "ASSEMBLY_DATA_DEFINITIONS" ("ADD_ID");
  CREATE UNIQUE INDEX "ADD_ORDER_NUMBER_UK" ON "ASSEMBLY_DATA_DEFINITIONS" ("ADD_NUMBER", "ADD_APD_ID");
  CREATE UNIQUE INDEX "ADD_NAME_UK" ON "ASSEMBLY_DATA_DEFINITIONS" ("ADD_NAME", "ADD_APD_ID") ;
  CREATE UNIQUE INDEX "APD_PK" ON "ASSEMBLY_PART_DEFINITIONS" ("APD_ID") ;
  CREATE UNIQUE INDEX "APD_NUMBER_UK" ON "ASSEMBLY_PART_DEFINITIONS" ("APD_NUMBER", "APD_ASD_ID") ;
  CREATE UNIQUE INDEX "APD_APD_UK" ON "ASSEMBLY_PART_DEFINITIONS" ("APD_APD_ID") ;
  CREATE UNIQUE INDEX "APD_NAME_UK" ON "ASSEMBLY_PART_DEFINITIONS" ("APD_NAME", "APD_ASD_ID") ;
  CREATE UNIQUE INDEX "ASP_PK" ON "ASSEMBLY_PARTS" ("ASP_ID") ;
  CREATE UNIQUE INDEX "ASP_UK" ON "ASSEMBLY_PARTS" ("ASP_APD_ID", "ASP_ASS_ID", "ASP_PART_ID") ;
  CREATE UNIQUE INDEX "APR_PK" ON "ASSEMBLY_PROCESSES" ("APR_ID") ;
  CREATE UNIQUE INDEX "APR_NAME_UK" ON "ASSEMBLY_PROCESSES" ("APR_NAME") ;
  CREATE UNIQUE INDEX "ASSEMBLY_PROCESSES_UK1" ON "ASSEMBLY_PROCESSES" ("APR_PRODUCT_KOP_ID") ;
  CREATE UNIQUE INDEX "ASD_PK" ON "ASSEMBLY_STEP_DEFINITIONS" ("ASD_ID") ;
  CREATE UNIQUE INDEX "ASD_NAME_UK" ON "ASSEMBLY_STEP_DEFINITIONS" ("ASD_NAME", "ASD_APR_ID") ;
  CREATE UNIQUE INDEX "ASD_NUMBER_UK" ON "ASSEMBLY_STEP_DEFINITIONS" ("ASD_NUMBER", "ASD_APR_ID") ;
  CREATE UNIQUE INDEX "ASS_PK" ON "ASSEMBLY_STEPS" ("ASS_ID") ;
  CREATE UNIQUE INDEX "ASS_UK" ON "ASSEMBLY_STEPS" ("ASS_ASD_ID", "ASS_PART_ID") ;

--------------------------------------------------------
--  Triggers
--------------------------------------------------------

CREATE OR REPLACE TRIGGER "AAD_INSERT_TRG" 
    BEFORE INSERT
    ON ASSEMBLY_ATTRIBUTE_DEFINITIONS
    REFERENCING OLD AS OLD NEW AS NEW
    FOR EACH ROW
    BEGIN

        if :new.aad_id is null then
            SELECT ANY_ASSEMBLY_ID_SEQ.NEXTVAL INTO :new.aad_id FROM dual;
        end if;

    EXCEPTION
        WHEN OTHERS THEN
        -- Consider logging the error and then re-raise
        RAISE;
END;
/

CREATE OR REPLACE TRIGGER "AED_INSERT_TRG" 
    BEFORE INSERT
    ON ASSEMBLY_DATA
    REFERENCING OLD AS OLD NEW AS NEW
    FOR EACH ROW
    BEGIN

        if :new.aed_id is null then
            SELECT ANY_ASSEMBLY_ID_SEQ.NEXTVAL INTO :new.aed_id FROM dual;
        end if;

    EXCEPTION
        WHEN OTHERS THEN
        -- Consider logging the error and then re-raise
        RAISE;
END;
/

CREATE OR REPLACE TRIGGER "ADD_INSERT_TRG" 
    BEFORE INSERT
    ON assembly_data_definitions
    REFERENCING OLD AS OLD NEW AS NEW
    FOR EACH ROW
    BEGIN

        if :new.add_id is null then
            SELECT ANY_ASSEMBLY_ID_SEQ.NEXTVAL INTO :new.add_id FROM dual;
        end if;

    EXCEPTION
        WHEN OTHERS THEN
        -- Consider logging the error and then re-raise
        RAISE;
END;
/

CREATE OR REPLACE TRIGGER "APD_INSERT_TRG" 
    BEFORE INSERT
    ON ASSEMBLY_PART_DEFINITIONS
    REFERENCING OLD AS OLD NEW AS NEW
    FOR EACH ROW
    BEGIN

        if :new.apd_id is null then
            SELECT ANY_ASSEMBLY_ID_SEQ.NEXTVAL INTO :new.apd_id FROM dual;
        end if;

    EXCEPTION
        WHEN OTHERS THEN
        -- Consider logging the error and then re-raise
        RAISE;
END;
/

CREATE OR REPLACE TRIGGER "ASP_INSERT_TRG" 
    BEFORE INSERT
    ON assembly_parts
    REFERENCING OLD AS OLD NEW AS NEW
    FOR EACH ROW
    BEGIN

        if :new.asp_id is null then
            SELECT ANY_ASSEMBLY_ID_SEQ.NEXTVAL INTO :new.asp_id FROM dual;
        end if;

    EXCEPTION
        WHEN OTHERS THEN
        -- Consider logging the error and then re-raise
        RAISE;
END;
/

CREATE OR REPLACE TRIGGER "APR_INSERT_TRG" 
    BEFORE INSERT
    ON assembly_processes
    REFERENCING OLD AS OLD NEW AS NEW
    FOR EACH ROW
    BEGIN

        if :new.apr_id is null then
            SELECT ANY_ASSEMBLY_ID_SEQ.NEXTVAL INTO :new.apr_id FROM dual;
        end if;

    EXCEPTION
        WHEN OTHERS THEN
        -- Consider logging the error and then re-raise
        RAISE;
END;
/

CREATE OR REPLACE TRIGGER "ASD_INSERT_TRG" 
    BEFORE INSERT
    ON assembly_step_definitions
    REFERENCING OLD AS OLD NEW AS NEW
    FOR EACH ROW
    BEGIN

        if :new.asd_id is null then
            SELECT ANY_ASSEMBLY_ID_SEQ.NEXTVAL INTO :new.asd_id FROM dual;
        end if;

    EXCEPTION
        WHEN OTHERS THEN
        -- Consider logging the error and then re-raise
        RAISE;
END;
/

CREATE OR REPLACE TRIGGER "ASS_INSERT_TRG" 
    BEFORE INSERT
    ON assembly_steps
    REFERENCING OLD AS OLD NEW AS NEW
    FOR EACH ROW
    BEGIN

        if :new.ass_id is null then
            SELECT ANY_ASSEMBLY_ID_SEQ.NEXTVAL INTO :new.ass_id FROM dual;
        end if;

    EXCEPTION
        WHEN OTHERS THEN
        -- Consider logging the error and then re-raise
        RAISE;
END;
/

create or replace TRIGGER "ASS_INSERT_TRG"
    BEFORE INSERT
    ON assembly_steps
    REFERENCING OLD AS OLD NEW AS NEW
    FOR EACH ROW
    BEGIN

        if :new.ass_id is null then
            SELECT ANY_ASSEMBLY_ID_SEQ.NEXTVAL INTO :new.ass_id FROM dual;
        end if;
        :new.RECORD_INSERTION_TIME := systimestamp;
        :new.RECORD_LASTUPDATE_TIME := systimestamp;

    EXCEPTION
        WHEN OTHERS THEN
        -- Consider logging the error and then re-raise
        RAISE;
END;
/

--------------------------------------------------------
-- History triggers
--------------------------------------------------------

create or replace TRIGGER "AED_UPDATE_DELETE_TRG"
    BEFORE UPDATE OR DELETE
    ON ASSEMBLY_DATA
    REFERENCING OLD AS OLD NEW AS NEW
    FOR EACH ROW
    DECLARE
        l_hst_id number;
    BEGIN

        SELECT ANY_ASSEMBLY_HISTORY_ID_SEQ.NEXTVAL INTO l_hst_id FROM dual;
    
        insert into ASSEMBLY_DATA_HST
        (
            HISTORY_RECORD_ID,
            AED_ID,
            AED_ADD_ID,
            AED_ASP_ID,
            AED_DATA_SET_ID
        ) values (
            l_hst_id,
            :OLD.AED_ID,
            :OLD.AED_ADD_ID,
            :OLD.AED_ASP_ID,
            :OLD.AED_DATA_SET_ID
        );

    EXCEPTION
        WHEN OTHERS THEN
        -- Consider logging the error and then re-raise
        RAISE;
END;
/

create or replace TRIGGER "ASP_UPDATE_DELETE_TRG"
    BEFORE UPDATE OR DELETE
    ON ASSEMBLY_PARTS
    REFERENCING OLD AS OLD NEW AS NEW
    FOR EACH ROW
    DECLARE
        l_hst_id number;
    BEGIN

        SELECT ANY_ASSEMBLY_HISTORY_ID_SEQ.NEXTVAL INTO l_hst_id FROM dual;
    
        insert into ASSEMBLY_PARTS_HST
        (
            HISTORY_RECORD_ID,
            ASP_ID,
            ASP_APD_ID,
            ASP_ASS_ID,
            ASP_PART_ID
        ) values (
            l_hst_id,
            :OLD.ASP_ID,
            :OLD.ASP_APD_ID,
            :OLD.ASP_ASS_ID,
            :OLD.ASP_PART_ID
        );

    EXCEPTION
        WHEN OTHERS THEN
        -- Consider logging the error and then re-raise
        RAISE;
END;
/

create or replace TRIGGER "ASS_UPDATE_DELETE_TRG"
    BEFORE UPDATE OR DELETE
    ON assembly_steps
    REFERENCING OLD AS OLD NEW AS NEW
    FOR EACH ROW
    DECLARE
        l_hst_id number;
    BEGIN

        if UPDATING then
            :new.RECORD_LASTUPDATE_TIME := systimestamp;
            :new.RECORD_INSERTION_TIME := :old.RECORD_INSERTION_TIME;
            :new.RECORD_INSERTION_USER := :old.RECORD_INSERTION_USER;
        end if;
        
        SELECT ANY_ASSEMBLY_HISTORY_ID_SEQ.NEXTVAL INTO l_hst_id FROM dual;
    
        insert into assembly_steps_hst
        (
            HISTORY_RECORD_ID,
            ASS_ID,
            ASS_ASD_ID,
            ASS_LOCATION_ID,
            ASS_STATUS,
            ASS_PART_ID,
            COMMENT_DESCRIPTION,
            RECORD_INSERTION_TIME,
            RECORD_LASTUPDATE_TIME,
            RECORD_INSERTION_USER,
            RECORD_LASTUPDATE_USER
        )
        values
        (
            l_hst_id,
            :OLD.ASS_ID,
            :OLD.ASS_ASD_ID,
            :OLD.ASS_LOCATION_ID,
            :OLD.ASS_STATUS,
            :OLD.ASS_PART_ID,
            :OLD.COMMENT_DESCRIPTION,
            :OLD.RECORD_INSERTION_TIME,
            :OLD.RECORD_LASTUPDATE_TIME,
            :OLD.RECORD_INSERTION_USER,
            :OLD.RECORD_LASTUPDATE_USER
        );

    EXCEPTION
        WHEN OTHERS THEN
        -- Consider logging the error and then re-raise
        RAISE;
END;
/

--------------------------------------------------------
--  Constraints
--------------------------------------------------------

  ALTER TABLE "ASSEMBLY_ATTRIBUTE_DEFINITIONS" ADD CONSTRAINT "AAD_GET_ACTION_CK" CHECK ( 
    aad_get_action is null or aad_get_action IN ('T','F') 
  ) ENABLE;
  ALTER TABLE "ASSEMBLY_ATTRIBUTE_DEFINITIONS" ADD CONSTRAINT "AAD_SET_ACTION_CK" CHECK 
    ((AAD_SET_STATUS in ('IN_PROGRESS','COMPLETED','FAILED','CANCELED') and AAD_SET_ACTION IN ('T','F')) or 
     (AAD_SET_STATUS is null and AAD_SET_ACTION in null)) ENABLE;
  ALTER TABLE ASSEMBLY_ATTRIBUTE_DEFINITIONS ADD CONSTRAINT AAD_PK PRIMARY KEY (  AAD_ID ) ENABLE;
  ALTER TABLE ASSEMBLY_ATTRIBUTE_DEFINITIONS ADD CONSTRAINT AAD_UK UNIQUE (  AAD_APD_ID, AAD_ATTRIBUTE_ID, AAD_SET_STATUS ) ENABLE;
  ALTER TABLE ASSEMBLY_ATTRIBUTE_DEFINITIONS  MODIFY (AAD_ID NOT NULL);
  ALTER TABLE "ASSEMBLY_ATTRIBUTE_DEFINITIONS" MODIFY ("AAD_APD_ID" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_ATTRIBUTE_DEFINITIONS" MODIFY ("AAD_ATTRIBUTE_ID" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_DATA" ADD CONSTRAINT "AED_PK" PRIMARY KEY ("AED_ID");
  ALTER TABLE "ASSEMBLY_DATA" ADD CONSTRAINT "AED_UK" UNIQUE ("AED_ADD_ID", "AED_ASP_ID", "AED_DATA_SET_ID");
  ALTER TABLE "ASSEMBLY_DATA" MODIFY ("AED_ID" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_DATA" MODIFY ("AED_ADD_ID" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_DATA" MODIFY ("AED_ASP_ID" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_DATA" MODIFY ("AED_DATA_SET_ID" NOT NULL ENABLE);

  ALTER TABLE "ASSEMBLY_DATA_DEFINITIONS" ADD CONSTRAINT "ADD_NAME_UK" UNIQUE ("ADD_NAME", "ADD_APD_ID");
  ALTER TABLE "ASSEMBLY_DATA_DEFINITIONS" ADD CONSTRAINT "ADD_ORDER_NUMBER_UK" UNIQUE ("ADD_NUMBER", "ADD_APD_ID");
  ALTER TABLE "ASSEMBLY_DATA_DEFINITIONS" ADD CONSTRAINT "ADD_PK" PRIMARY KEY ("ADD_ID");
  ALTER TABLE "ASSEMBLY_DATA_DEFINITIONS" MODIFY ("ADD_ID" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_DATA_DEFINITIONS" MODIFY ("ADD_NUMBER" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_DATA_DEFINITIONS" MODIFY ("ADD_KOC_ID" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_DATA_DEFINITIONS" MODIFY ("ADD_APD_ID" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_DATA_DEFINITIONS" MODIFY ("ADD_NAME" NOT NULL ENABLE);

  ALTER TABLE "ASSEMBLY_PART_DEFINITIONS" ADD CONSTRAINT "APD_CK" CHECK (( apd_type IN (
        'COMPONENT',
        'PRODUCT',
        'JIG'
    )
    AND apd_apd_id is null )
  OR ( apd_type  IN (
        'COMPONENT',
        'PRODUCT'
    ) AND apd_apd_id is not null )) ENABLE;
  ALTER TABLE "ASSEMBLY_PART_DEFINITIONS" ADD CONSTRAINT "APD_NAME_UK" UNIQUE ("APD_NAME", "APD_ASD_ID");
  ALTER TABLE "ASSEMBLY_PART_DEFINITIONS" ADD CONSTRAINT "APD_ORDER_NUMBER_UK" UNIQUE ("APD_NUMBER", "APD_ASD_ID");
  ALTER TABLE "ASSEMBLY_PART_DEFINITIONS" ADD CONSTRAINT "APD_PK" PRIMARY KEY ("APD_ID");
  ALTER TABLE "ASSEMBLY_PART_DEFINITIONS" MODIFY ("APD_ID" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_PART_DEFINITIONS" MODIFY ("APD_NUMBER" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_PART_DEFINITIONS" MODIFY ("APD_ASD_ID" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_PART_DEFINITIONS" MODIFY ("APD_KOP_ID" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_PART_DEFINITIONS" MODIFY ("APD_TYPE" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_PART_DEFINITIONS" MODIFY ("APD_NAME" NOT NULL ENABLE);

  ALTER TABLE "ASSEMBLY_PARTS" ADD CONSTRAINT "ASP_PK" PRIMARY KEY ("ASP_ID");
  ALTER TABLE "ASSEMBLY_PARTS" ADD CONSTRAINT "ASP_UK" UNIQUE ("ASP_APD_ID", "ASP_ASS_ID", "ASP_PART_ID");
  ALTER TABLE "ASSEMBLY_PARTS" MODIFY ("ASP_ID" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_PARTS" MODIFY ("ASP_APD_ID" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_PARTS" MODIFY ("ASP_ASS_ID" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_PARTS" MODIFY ("ASP_PART_ID" NOT NULL ENABLE);

  ALTER TABLE "ASSEMBLY_PROCESSES" ADD CONSTRAINT "APR_KOP_UK" UNIQUE ("APR_PRODUCT_KOP_ID");
  ALTER TABLE "ASSEMBLY_PROCESSES" ADD CONSTRAINT "APR_NAME_UK" UNIQUE ("APR_NAME");
  ALTER TABLE "ASSEMBLY_PROCESSES" ADD CONSTRAINT "APR_PK" PRIMARY KEY ("APR_ID");
  ALTER TABLE "ASSEMBLY_PROCESSES" MODIFY ("APR_ID" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_PROCESSES" MODIFY ("APR_NAME" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_PROCESSES" MODIFY ("APR_DESCR" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_PROCESSES" MODIFY ("APR_PRODUCT_KOP_ID" NOT NULL ENABLE);

  ALTER TABLE "ASSEMBLY_STEP_DEFINITIONS" ADD CONSTRAINT "ASD_NAME_UK" UNIQUE ("ASD_NAME", "ASD_APR_ID");
  ALTER TABLE "ASSEMBLY_STEP_DEFINITIONS" ADD CONSTRAINT "ASD_NUMBER_UK" UNIQUE ("ASD_NUMBER", "ASD_APR_ID");
  ALTER TABLE "ASSEMBLY_STEP_DEFINITIONS" ADD CONSTRAINT "ASD_PK" PRIMARY KEY ("ASD_ID");
  ALTER TABLE "ASSEMBLY_STEP_DEFINITIONS" MODIFY ("ASD_ID" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_STEP_DEFINITIONS" MODIFY ("ASD_NUMBER" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_STEP_DEFINITIONS" MODIFY ("ASD_APR_ID" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_STEP_DEFINITIONS" MODIFY ("ASD_NAME" NOT NULL ENABLE);

  ALTER TABLE "ASSEMBLY_STEPS" ADD CONSTRAINT "ASS_PK" PRIMARY KEY ("ASS_ID");
  ALTER TABLE "ASSEMBLY_STEPS" ADD CONSTRAINT "ASS_UK" UNIQUE ("ASS_ASD_ID", "ASS_PART_ID");
  ALTER TABLE "ASSEMBLY_STEPS" MODIFY ("ASS_ID" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_STEPS" MODIFY ("ASS_ASD_ID" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_STEPS" MODIFY ("ASS_LOCATION_ID" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_STEPS" MODIFY ("ASS_STATUS" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_STEPS" MODIFY ("COMMENT_DESCRIPTION" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_STEPS" MODIFY ("RECORD_INSERTION_TIME" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_STEPS" MODIFY ("RECORD_LASTUPDATE_TIME" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_STEPS" MODIFY ("ASS_PART_ID" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_STEPS" MODIFY ("RECORD_INSERTION_USER" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_STEPS" MODIFY ("RECORD_LASTUPDATE_USER" NOT NULL ENABLE);
  ALTER TABLE ASSEMBLY_STEPS ADD CONSTRAINT ASS_STATUS_CHK CHECK (ASS_STATUS in ('IN_PROGRESS','COMPLETED','FAILED','CANCELED')) ENABLE;

  ALTER TABLE "ASSEMBLY_ATTRIBUTE_DEFINITIONS" ADD CONSTRAINT "AAD_APD_FK" FOREIGN KEY ("AAD_APD_ID")
	  REFERENCES "ASSEMBLY_PART_DEFINITIONS" ("APD_ID") ENABLE;
  ALTER TABLE "ASSEMBLY_ATTRIBUTE_DEFINITIONS" ADD CONSTRAINT "AAD_ATTRIBUTE_FK" FOREIGN KEY ("AAD_ATTRIBUTE_ID")
	  REFERENCES "CMS_&det._CORE_ATTRIBUTE"."ATTR_BASES" ("ATTRIBUTE_ID") ENABLE;

  ALTER TABLE "ASSEMBLY_DATA" ADD CONSTRAINT "AED_ADD_FK" FOREIGN KEY ("AED_ADD_ID")
	  REFERENCES "ASSEMBLY_DATA_DEFINITIONS" ("ADD_ID") ENABLE;
  ALTER TABLE "ASSEMBLY_DATA" ADD CONSTRAINT "AED_ASP_FK" FOREIGN KEY ("AED_ASP_ID")
	  REFERENCES "ASSEMBLY_PARTS" ("ASP_ID") ENABLE;
  ALTER TABLE "ASSEMBLY_DATA" ADD CONSTRAINT "AED_DATA_SET_FK" FOREIGN KEY ("AED_DATA_SET_ID")
	  REFERENCES "CMS_&det._CORE_COND"."COND_DATA_SETS" ("CONDITION_DATA_SET_ID") ENABLE;

  ALTER TABLE "ASSEMBLY_DATA_DEFINITIONS" ADD CONSTRAINT "ADD_APD_FK" FOREIGN KEY ("ADD_APD_ID")
	  REFERENCES "ASSEMBLY_PART_DEFINITIONS" ("APD_ID") ENABLE;
  ALTER TABLE "ASSEMBLY_DATA_DEFINITIONS" ADD CONSTRAINT "ADD_KOC_FK" FOREIGN KEY ("ADD_KOC_ID")
	  REFERENCES "CMS_&det._CORE_COND"."KINDS_OF_CONDITIONS" ("KIND_OF_CONDITION_ID") ENABLE;

  ALTER TABLE "ASSEMBLY_PART_DEFINITIONS" ADD CONSTRAINT "APD_ASD_FK" FOREIGN KEY ("APD_ASD_ID")
	  REFERENCES "ASSEMBLY_STEP_DEFINITIONS" ("ASD_ID") ENABLE;
  ALTER TABLE "ASSEMBLY_PART_DEFINITIONS" ADD CONSTRAINT "APD_KOP_FK" FOREIGN KEY ("APD_KOP_ID")
	  REFERENCES "CMS_&det._CORE_CONSTRUCT"."KINDS_OF_PARTS" ("KIND_OF_PART_ID") ENABLE;
  ALTER TABLE "ASSEMBLY_PART_DEFINITIONS" ADD CONSTRAINT "APD_APD_FK" FOREIGN KEY ("APD_APD_ID")
	  REFERENCES "ASSEMBLY_PART_DEFINITIONS" ("APD_ID") ENABLE;

  ALTER TABLE "ASSEMBLY_PARTS" ADD CONSTRAINT "ASP_APD_FK" FOREIGN KEY ("ASP_APD_ID")
	  REFERENCES "ASSEMBLY_PART_DEFINITIONS" ("APD_ID") ENABLE;
  ALTER TABLE "ASSEMBLY_PARTS" ADD CONSTRAINT "ASP_ASS_FK" FOREIGN KEY ("ASP_ASS_ID")
	  REFERENCES "ASSEMBLY_STEPS" ("ASS_ID") ENABLE;
  ALTER TABLE "ASSEMBLY_PARTS" ADD CONSTRAINT "ASP_PARTS_FK" FOREIGN KEY ("ASP_PART_ID")
	  REFERENCES "CMS_&det._CORE_CONSTRUCT"."PARTS" ("PART_ID") ENABLE;

  ALTER TABLE "ASSEMBLY_PROCESSES" ADD CONSTRAINT "APR_KOP_FK" FOREIGN KEY ("APR_PRODUCT_KOP_ID")
	  REFERENCES "CMS_&det._CORE_CONSTRUCT"."KINDS_OF_PARTS" ("KIND_OF_PART_ID") ENABLE;

  ALTER TABLE "ASSEMBLY_STEP_DEFINITIONS" ADD CONSTRAINT "ASD_APR_FK" FOREIGN KEY ("ASD_APR_ID")
	  REFERENCES "ASSEMBLY_PROCESSES" ("APR_ID") ENABLE;

  ALTER TABLE "ASSEMBLY_STEPS" ADD CONSTRAINT "ASS_ASD_FK" FOREIGN KEY ("ASS_ASD_ID")
	  REFERENCES "ASSEMBLY_STEP_DEFINITIONS" ("ASD_ID") ENABLE;
  ALTER TABLE "ASSEMBLY_STEPS" ADD CONSTRAINT "ASS_KOP_FK" FOREIGN KEY ("ASS_PART_ID")
	  REFERENCES "CMS_&det._CORE_CONSTRUCT"."PARTS" ("PART_ID") ENABLE;
  ALTER TABLE "ASSEMBLY_STEPS" ADD CONSTRAINT "ASS_LOCATIONS_FK" FOREIGN KEY ("ASS_LOCATION_ID")
	  REFERENCES "CMS_&det._CORE_MANAGEMNT"."LOCATIONS" ("LOCATION_ID") ENABLE;

  ALTER TABLE "ASSEMBLY_SAME_LOCATIONS" ADD CONSTRAINT "ASSEMBLY_LOC_PK" PRIMARY KEY ("LOCATION_ID","LOCATION_ID_SAME");
  ALTER TABLE "ASSEMBLY_SAME_LOCATIONS" MODIFY ("LOCATION_ID" NOT NULL ENABLE);
  ALTER TABLE "ASSEMBLY_SAME_LOCATIONS" MODIFY ("LOCATION_ID_SAME" NOT NULL ENABLE);

  ALTER TABLE "ASSEMBLY_SAME_LOCATIONS" ADD CONSTRAINT "ASSEMBLY_LOC_FK" FOREIGN KEY ("LOCATION_ID")
	  REFERENCES "CMS_&det._CORE_MANAGEMNT"."LOCATIONS" ("LOCATION_ID") ENABLE;
  ALTER TABLE "ASSEMBLY_SAME_LOCATIONS" ADD CONSTRAINT "ASSEMBLY_LOC_SAME_FK" FOREIGN KEY ("LOCATION_ID_SAME")
	  REFERENCES "CMS_&det._CORE_MANAGEMNT"."LOCATIONS" ("LOCATION_ID") ENABLE;

--------------------------------------------------------
--  Views
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "ASSEMBLY_PART_CURRENT_STEPS" ("PART_ID", "APR_ID", "ASD_ID", "ASD_NUMBER", "ASS_ID") AS 
  select 
    a.PART_ID,
    a.apr_id,
    sd.asd_id,
    a.curr_step_number as asd_number,
    s.ass_id
from (
    select 
        p.PART_ID,
        pr.apr_id,
        CASE WHEN pr.apr_id is null THEN 
            null 
        ELSE 
            nvl(ls.last_closed, 0) + 1
        END as curr_step_number
    from 
        CMS_&det._CORE_CONSTRUCT.PARTS p 
        left join CMS_&det._&subdet._CONSTRUCT.ASSEMBLY_PROCESSES pr
            on p.kind_of_part_id = pr.apr_product_kop_id
        left join (
            select 
                s.ASS_PART_ID,
                sd.asd_apr_id,
                max(sd.asd_number) last_closed
            from 
                CMS_&det._&subdet._CONSTRUCT.ASSEMBLY_STEP_DEFINITIONS sd
                left join CMS_&det._&subdet._CONSTRUCT.ASSEMBLY_STEPS s 
                    on sd.asd_id = s.ass_asd_id
            where
                s.ASS_STATUS <> 'IN_PROGRESS'
            group by 
                s.ASS_PART_ID,
                sd.asd_apr_id) ls
            on ls.ASS_PART_ID = p.PART_ID and ls.asd_apr_id = pr.apr_id) a
    left join CMS_&det._&subdet._CONSTRUCT.ASSEMBLY_STEP_DEFINITIONS sd
        on sd.asd_apr_id = a.apr_id and sd.asd_number = a.curr_step_number
    left join CMS_&det._&subdet._CONSTRUCT.ASSEMBLY_STEPS s 
        on sd.asd_id = s.ass_asd_id and a.part_id = s.ass_part_id;


CREATE OR REPLACE FORCE VIEW "ASSEMBLY_LOCATIONS"  AS
SELECT LOCATION_ID, LOCATION_ID_SAME FROM ASSEMBLY_SAME_LOCATIONS 
union 
SELECT LOCATION_ID_SAME, LOCATION_ID FROM ASSEMBLY_SAME_LOCATIONS 
union 
SELECT LOCATION_ID, LOCATION_ID FROM CMS_&det._CORE_MANAGEMNT.LOCATIONS;

--------------------------------------------------------
--  Grants
--------------------------------------------------------

GRANT SELECT ON ANY_ASSEMBLY_ID_SEQ TO CMS_&det._&subdet._CONSTRUCT_WRITER;

GRANT SELECT ON ASSEMBLY_ATTRIBUTE_DEFINITIONS TO PUBLIC;
GRANT INSERT, UPDATE ON ASSEMBLY_ATTRIBUTE_DEFINITIONS TO CMS_&det._&subdet._CONSTRUCT_WRITER;
GRANT SELECT, REFERENCES ON ASSEMBLY_ATTRIBUTE_DEFINITIONS TO CMS_&det._&subdet._CONSTRUCT;

GRANT SELECT ON ASSEMBLY_DATA TO PUBLIC;
GRANT INSERT, UPDATE ON ASSEMBLY_DATA TO CMS_&det._&subdet._CONSTRUCT_WRITER;
GRANT SELECT, REFERENCES ON ASSEMBLY_DATA TO CMS_&det._&subdet._CONSTRUCT;

GRANT SELECT ON ASSEMBLY_DATA_DEFINITIONS TO PUBLIC;
GRANT INSERT, UPDATE ON ASSEMBLY_DATA_DEFINITIONS TO CMS_&det._&subdet._CONSTRUCT_WRITER;
GRANT SELECT, REFERENCES ON ASSEMBLY_DATA_DEFINITIONS TO CMS_&det._&subdet._CONSTRUCT;

GRANT SELECT ON ASSEMBLY_PART_DEFINITIONS TO PUBLIC;
GRANT INSERT, UPDATE ON ASSEMBLY_PART_DEFINITIONS TO CMS_&det._&subdet._CONSTRUCT_WRITER;
GRANT SELECT, REFERENCES ON ASSEMBLY_PART_DEFINITIONS TO CMS_&det._&subdet._CONSTRUCT;

GRANT SELECT ON ASSEMBLY_PARTS TO PUBLIC;
GRANT INSERT, UPDATE ON ASSEMBLY_PARTS TO CMS_&det._&subdet._CONSTRUCT_WRITER;
GRANT SELECT, REFERENCES ON ASSEMBLY_PARTS TO CMS_&det._&subdet._CONSTRUCT;

GRANT SELECT ON ASSEMBLY_PROCESSES TO PUBLIC;
GRANT INSERT, UPDATE ON ASSEMBLY_PROCESSES TO CMS_&det._&subdet._CONSTRUCT_WRITER;
GRANT SELECT, REFERENCES ON ASSEMBLY_PROCESSES TO CMS_&det._&subdet._CONSTRUCT;

GRANT SELECT ON ASSEMBLY_STEP_DEFINITIONS TO PUBLIC;
GRANT INSERT, UPDATE ON ASSEMBLY_STEP_DEFINITIONS TO CMS_&det._&subdet._CONSTRUCT_WRITER;
GRANT SELECT, REFERENCES ON ASSEMBLY_STEP_DEFINITIONS TO CMS_&det._&subdet._CONSTRUCT;

GRANT SELECT ON ASSEMBLY_STEPS TO PUBLIC;
GRANT INSERT, UPDATE ON ASSEMBLY_STEPS TO CMS_&det._&subdet._CONSTRUCT_WRITER;
GRANT SELECT, REFERENCES ON ASSEMBLY_STEPS TO CMS_&det._&subdet._CONSTRUCT;

grant SELECT on "ASSEMBLY_PART_CURRENT_STEPS" to "PUBLIC" ;

GRANT SELECT ON ASSEMBLY_SAME_LOCATIONS TO PUBLIC;
GRANT SELECT ON ASSEMBLY_LOCATIONS TO PUBLIC;