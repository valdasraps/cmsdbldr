CREATE TABLE "CMS_&det._&subdet._CONSTRUCT"."PART_DETAILS"(
    "ID" NUMBER(32,0) NOT NULL ENABLE,
	"PART_ID" NUMBER(32,0) NOT NULL ENABLE,
	"BLOB_TYPE_BLOB" BLOB,
	"BLOB_TYPE_FILE" VARCHAR2 (256 BYTE),
	"DATE_TYPE" DATE,
	"CLOB_TYPE_CLOB" CLOB,
	"NUMBER_TYPE" NUMBER(32,0),
  "CLOB_TYPE_FILE" VARCHAR2 (256 BYTE),
	"VARCHAR_TYPE" VARCHAR2 (60 BYTE),
	"RECORD_INSERTION_TIME" TIMESTAMP (6) NOT NULL ENABLE,
	"RECORD_INSERTION_USER" VARCHAR2(40 BYTE) NOT NULL ENABLE,
	"RECORD_LASTUPDATE_TIME" TIMESTAMP (6),
	"RECORD_LASTUPDATE_USER" VARCHAR2(50 BYTE),


    CONSTRAINT "PART_DETAILS_UK1" UNIQUE ("PART_ID"),
    CONSTRAINT "PART_DETAILS_PK" PRIMARY KEY ("ID"),
    CONSTRAINT "PART_DETAILS_FK1" FOREIGN KEY ("PART_ID") REFERENCES "CMS_&det._CORE_CONSTRUCT"."PARTS" ("PART_ID") ENABLE
);

--Add sequence for PART_DETAILS table

CREATE SEQUENCE "ANY_PARTS_DETAILS_ID_SEQ"
 INCREMENT BY 20
 START WITH 1000
 NOMAXVALUE
 MINVALUE 1000
 NOCYCLE
 CACHE 20
/

--Create PART_DETAILS_HST Table

CREATE TABLE "CMS_&det._&subdet._CONSTRUCT"."PART_DETAILS_HST"(
    "HISTORY_RECORD_ID" NUMBER(32,0) NOT NULL ENABLE,
	"PARTS_DETAILS_ID" NUMBER(32,0) NOT NULL ENABLE,
	"PART_ID" NUMBER(32,0) NOT NULL ENABLE,
	"BLOB_TYPE_BLOB" BLOB,
	"BLOB_TYPE_FILE" VARCHAR2 (256 BYTE),
	"CLOB_TYPE_CLOB" CLOB,
  "CLOB_TYPE_FILE" VARCHAR2 (256 BYTE),
	"NUMBER_TYPE" NUMBER(32,0),
	"DATE_TYPE" DATE,
	"VARCHAR_TYPE" VARCHAR2 (60 BYTE),
	"RECORD_INSERTION_TIME" TIMESTAMP (6) NOT NULL ENABLE,
	"RECORD_INSERTION_USER" VARCHAR2(40 BYTE) NOT NULL ENABLE,
	"RECORD_LASTUPDATE_TIME" TIMESTAMP (6),
	"RECORD_LASTUPDATE_USER" VARCHAR2(40 BYTE),

  CONSTRAINT "PART_DETAILS_HST_PK" PRIMARY KEY ("HISTORY_RECORD_ID"),
  CONSTRAINT "PART_DETAILS_HST_FK" FOREIGN KEY ("PARTS_DETAILS_ID") REFERENCES "CMS_&det._&subdet._CONSTRUCT"."PART_DETAILS" ("ID") ENABLE


);

--Add sequence for PART_DETAILS_HST table

CREATE SEQUENCE "ANY_PARTS_DETAILS_HST_ID_SEQ"
 INCREMENT BY 20
 START WITH 1000
 NOMAXVALUE
 MINVALUE 1000
 NOCYCLE
 CACHE 20
/


-- Add TRIGGER to insert entry data into PART_DETAILS_HST table

create or replace TRIGGER TR_INS_PART_DETAILS
    BEFORE INSERT
    ON PART_DETAILS
    REFERENCING OLD AS OLD NEW AS NEW
    FOR EACH ROW
    BEGIN
        if :new.id is null then
            SELECT ANY_PARTS_DETAILS_ID_SEQ.NEXTVAL INTO :new.id FROM dual;
        end if;

      :NEW.RECORD_INSERTION_TIME := SYSDATE;

    if :new.RECORD_INSERTION_USER is null then
        :NEW.RECORD_INSERTION_USER  := USER;
    end if;

    EXCEPTION
        WHEN OTHERS THEN
        -- Consider logging the error and then re-raise
        RAISE;
END;
/

-- Add TRIGGER to insert entry data into PART_DETAILS_HST table when entry in PART_DETAILS table was updated

create or replace TRIGGER TR_UPD_PART_DETAILS
    BEFORE UPDATE OR DELETE
    ON PART_DETAILS
    REFERENCING OLD AS OLD NEW AS NEW
    FOR EACH ROW
    DECLARE
        seqPkId number(38,0);
        recordChangeTime TIMESTAMP(6) WITH TIME ZONE;
        recordChangeUser VARCHAR2(50 );
    BEGIN
        SELECT ANY_PARTS_DETAILS_HST_ID_SEQ.NEXTVAL INTO seqPkId FROM dual;

        if :old.RECORD_LASTUPDATE_TIME is null then
            recordChangeTime:= :OLD.RECORD_INSERTION_TIME;
        else
            recordChangeTime:= :old.RECORD_LASTUPDATE_TIME;
    end if;

    if :old.RECORD_LASTUPDATE_USER is null then
        recordChangeUser:= :OLD.RECORD_INSERTION_USER;
    else
        recordChangeUser:= :old.RECORD_LASTUPDATE_USER;
    end if;

    if UPDATING then

      -- protect the original record data
      :new.RECORD_INSERTION_TIME := :OLD.RECORD_INSERTION_TIME;
      :new.RECORD_INSERTION_USER := :OLD.RECORD_INSERTION_USER;
      :new.ID:=  :OLD.ID;

     -- update last change record info, if needed
      :new.RECORD_LASTUPDATE_TIME  := SYSDATE;

      if :new.RECORD_LASTUPDATE_USER is null then
        :new.RECORD_LASTUPDATE_USER := USER;
      end if;

    end if;

    insert into PART_DETAILS_HST
    (
        HISTORY_RECORD_ID,
        PARTS_DETAILS_ID,
        PART_ID,
	      BLOB_TYPE_BLOB,
	      BLOB_TYPE_FILE,
	      CLOB_TYPE_CLOB,
	      CLOB_TYPE_FILE,
	      NUMBER_TYPE,
	      DATE_TYPE,
        VARCHAR_TYPE,
        RECORD_INSERTION_TIME,
        RECORD_INSERTION_USER,
        RECORD_LASTUPDATE_TIME,
        RECORD_LASTUPDATE_USER
    )
    values
    (
        seqPkId,
        :OLD.ID,
        :OLD.PART_ID,
        :OLD.BLOB_TYPE_BLOB,
        :OLD.BLOB_TYPE_FILE,
        :OLD.CLOB_TYPE_CLOB,
        :OLD.CLOB_TYPE_FILE,
	      :OLD.NUMBER_TYPE,
	      :OLD.DATE_TYPE,
        :OLD.VARCHAR_TYPE,
        sysdate,
        :OLD.RECORD_INSERTION_USER,
        recordChangeTime,
        recordChangeUser
    );

   EXCEPTION
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
END ;
/

-- Add Grants to see PART_DETAILS table
GRANT SELECT ON PART_DETAILS TO PUBLIC;
GRANT INSERT, UPDATE ON PART_DETAILS TO CMS_&det._&subdet._CONSTRUCT_WRITER;
GRANT SELECT, REFERENCES ON PART_DETAILS TO CMS_&det._&subdet._CONSTRUCT;

-- Add Grants to see PART_DETAILS_HST table
GRANT SELECT ON PART_DETAILS_HST TO PUBLIC;


GRANT SELECT ON ANY_PARTS_DETAILS_ID_SEQ TO CMS_&det._&subdet._CONSTRUCT_WRITER;
GRANT SELECT ON ANY_PARTS_DETAILS_HST_ID_SEQ TO CMS_&det._&subdet._CONSTRUCT_WRITER;


