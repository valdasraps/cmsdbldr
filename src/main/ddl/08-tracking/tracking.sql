/*

drop table REQUEST_ITEMS cascade constraints purge;
drop table REQUEST_ITEMS_HST cascade constraints purge;
drop table REQUESTS cascade constraints purge;
drop table REQUESTS_HST cascade constraints purge;
drop table SHIPMENT_ITEMS cascade constraints purge;
drop table SHIPMENT_ITEMS_HST cascade constraints purge;
drop table SHIPMENTS cascade constraints purge;
drop table SHIPMENTS_HST cascade constraints purge;

drop sequence ANY_REQUESTS_HISTORY_ID_SEQ;
drop sequence ANY_REQUESTS_ID_SEQ;
drop sequence ANY_SHIPMENT_ITM_HST_ID_SEQ;
drop sequence ANY_SHIPMENT_ITM_ID_SEQ;
drop sequence ANY_SHIPMENTS_ID_SEQ;
drop sequence ANY_REQUEST_ITM_HISTORY_ID_SEQ;
drop sequence ANY_SHIPMENTS_HISTORY_ID_SEQ;
drop sequence ANY_REQUEST_ITM_ID_SEQ;

*/

-- define det=TST
-- define subdet=TEST

/* REQUESTS */

CREATE TABLE "REQUESTS" (

    "REQ_ID" NUMBER(32,0) NOT NULL ENABLE, 
    "REQ_NAME" VARCHAR2(20 BYTE) NOT NULL ENABLE, 
    "REQ_LOCATION_ID" NUMBER(32,0) NOT NULL ENABLE, 
    "REQ_STATUS" VARCHAR2(20 BYTE) DEFAULT 'OPEN' NOT NULL ENABLE, 
    "COMMENT_DESCRIPTION" VARCHAR2(1024 BYTE), 
    "REQ_DATE" DATE NOT NULL ENABLE, 
    "REQ_PERSON" VARCHAR2(128 BYTE) NOT NULL ENABLE, 
    "RECORD_INSERTION_TIME" TIMESTAMP (6) NOT NULL ENABLE, 
    "RECORD_INSERTION_USER" VARCHAR2(50 BYTE) NOT NULL ENABLE, 
    "RECORD_LASTUPDATE_TIME" TIMESTAMP (6), 
    "RECORD_LASTUPDATE_USER" VARCHAR2(50 BYTE), 

    CONSTRAINT "REQUESTS_PK" PRIMARY KEY ("REQ_ID"),
    CONSTRAINT "REQUESTS_CHK1" CHECK (REQ_STATUS in ('OPEN','CLOSED','CANCELED')) ENABLE,
    CONSTRAINT "REQUESTS_UK1" UNIQUE ("REQ_LOCATION_ID", "REQ_NAME"), 
    CONSTRAINT "REQUESTS_FK1" FOREIGN KEY ("REQ_LOCATION_ID") REFERENCES "CMS_&det._CORE_MANAGEMNT"."LOCATIONS" ("LOCATION_ID") ENABLE

);

CREATE SEQUENCE "ANY_REQUESTS_ID_SEQ"
 INCREMENT BY 20
 START WITH 1000
 NOMAXVALUE
 MINVALUE 1000
 NOCYCLE
 CACHE 20
/

CREATE TABLE "REQUESTS_HST" (

    "HISTORY_RECORD_ID" NUMBER(32,0) NOT NULL ENABLE, 
    "REQ_ID" NUMBER(32,0) NOT NULL ENABLE, 
    "REQ_NAME" VARCHAR2(20 BYTE) NOT NULL ENABLE, 
    "REQ_LOCATION_ID" NUMBER(32,0) NOT NULL ENABLE, 
    "REQ_STATUS" VARCHAR2(20 BYTE) DEFAULT 'OPEN' NOT NULL ENABLE, 
    "COMMENT_DESCRIPTION" VARCHAR2(1024 BYTE), 
    "REQ_DATE" DATE NOT NULL ENABLE, 
    "REQ_PERSON" VARCHAR2(128 BYTE) NOT NULL ENABLE, 
    "RECORD_INSERTION_TIME" TIMESTAMP (6) NOT NULL ENABLE, 
    "RECORD_INSERTION_USER" VARCHAR2(50 BYTE) NOT NULL ENABLE, 
    "RECORD_LASTUPDATE_TIME" TIMESTAMP (6), 
    "RECORD_LASTUPDATE_USER" VARCHAR2(50 BYTE), 

    CONSTRAINT "REQUESTS_HST_PK" PRIMARY KEY ("HISTORY_RECORD_ID")
    
);


CREATE SEQUENCE "ANY_REQUESTS_HISTORY_ID_SEQ"
 INCREMENT BY 20
 START WITH 1000
 NOMAXVALUE
 MINVALUE 1000
 NOCYCLE
 CACHE 20
/

create or replace TRIGGER TR_INS_REQUESTS
    BEFORE INSERT
    ON REQUESTS
    REFERENCING OLD AS OLD NEW AS NEW
    FOR EACH ROW
    BEGIN
        if :new.req_id is null then
            SELECT ANY_REQUESTS_ID_SEQ.NEXTVAL INTO :new.req_id FROM dual;
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

create or replace TRIGGER TR_UPD_REQUESTS
    BEFORE UPDATE
    ON REQUESTS
    REFERENCING OLD AS OLD NEW AS NEW
    FOR EACH ROW
    DECLARE
        seqPkId number(38,0);
        recordChangeTime TIMESTAMP(6) WITH TIME ZONE;
        recordChangeUser VARCHAR2(50 );
    BEGIN
        SELECT ANY_REQUESTS_HISTORY_ID_SEQ.NEXTVAL INTO seqPkId FROM dual;

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

    -- protect the original record data
    :new.RECORD_INSERTION_TIME := :OLD.RECORD_INSERTION_TIME;
    :new.RECORD_INSERTION_USER := :OLD.RECORD_INSERTION_USER;
    :new.REQ_ID :=  :OLD.REQ_ID;

   -- update last change record info, if needed
    :new.RECORD_LASTUPDATE_TIME  := SYSDATE;

   if :new.RECORD_LASTUPDATE_USER is null then
       :new.RECORD_LASTUPDATE_USER := USER;
   end if;

    insert into REQUESTS_HST
    (
        HISTORY_RECORD_ID,
        REQ_ID,
        REQ_NAME,
        REQ_LOCATION_ID,
        REQ_STATUS,
        COMMENT_DESCRIPTION,
        REQ_DATE,
        REQ_PERSON,
        RECORD_INSERTION_TIME,
        RECORD_INSERTION_USER,
        RECORD_LASTUPDATE_TIME,
        RECORD_LASTUPDATE_USER
    )
    values
    (
        seqPkId,
        :OLD.REQ_ID,
        :OLD.REQ_NAME,
        :OLD.REQ_LOCATION_ID,
        :OLD.REQ_STATUS,
        :OLD.COMMENT_DESCRIPTION,
        :OLD.REQ_DATE,
        :OLD.REQ_PERSON,
        :new.RECORD_LASTUPDATE_TIME,
        :new.RECORD_LASTUPDATE_USER,
        recordChangeTime,
        recordChangeUser
    );

   EXCEPTION
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
END ;
/

GRANT SELECT ON REQUESTS TO PUBLIC;
GRANT INSERT, UPDATE ON REQUESTS TO CMS_&det._&subdet._CONSTRUCT_WRITER;
GRANT SELECT, REFERENCES ON REQUESTS TO CMS_&det._&subdet._CONSTRUCT;

/*
REQUEST ITEMS
*/
CREATE TABLE "REQUEST_ITEMS" (

    "RQI_ID" NUMBER(32,0) NOT NULL ENABLE, 
    "RQI_REQ_ID" NUMBER(32,0) NOT NULL ENABLE, 
    "RQI_KIND_OF_PART_ID" NUMBER NOT NULL ENABLE, 
    "RQI_QUANTITY" NUMBER NOT NULL ENABLE,
    "COMMENT_DESCRIPTION" VARCHAR2(1024 BYTE), 
    "RECORD_INSERTION_TIME" TIMESTAMP (6) NOT NULL ENABLE, 
    "RECORD_INSERTION_USER" VARCHAR2(50 BYTE) NOT NULL ENABLE, 
    "RECORD_LASTUPDATE_TIME" TIMESTAMP (6), 
    "RECORD_LASTUPDATE_USER" VARCHAR2(50 BYTE),

    CONSTRAINT "REQUEST_ITEMS_PK" PRIMARY KEY ("RQI_ID"), 
    CONSTRAINT "REQUEST_ITEMS_CHK1" CHECK (RQI_QUANTITY > 0) ENABLE,
    CONSTRAINT "REQUEST_ITEMS_UK1" UNIQUE ("RQI_REQ_ID", "RQI_KIND_OF_PART_ID"),
    CONSTRAINT "REQUEST_ITEMS_FK1" FOREIGN KEY ("RQI_REQ_ID") REFERENCES "REQUESTS" ("REQ_ID") ENABLE, 
    CONSTRAINT "REQUEST_ITEMS_FK2" FOREIGN KEY ("RQI_KIND_OF_PART_ID") REFERENCES "CMS_&det._CORE_CONSTRUCT"."KINDS_OF_PARTS" ("KIND_OF_PART_ID") ENABLE

 );

GRANT SELECT ON REQUEST_ITEMS TO PUBLIC;
GRANT INSERT, UPDATE ON REQUEST_ITEMS TO CMS_&det._&subdet._CONSTRUCT_WRITER;
GRANT SELECT, REFERENCES ON REQUEST_ITEMS TO CMS_&det._&subdet._CONSTRUCT;

/*
REQUEST ITEMS HISTORY
*/

CREATE TABLE "REQUEST_ITEMS_HST" (

    "HISTORY_RECORD_ID" NUMBER(32,0) NOT NULL ENABLE, 
    "RQI_ID" NUMBER(32,0) NOT NULL ENABLE, 
    "RQI_REQ_ID" NUMBER(32,0) NOT NULL ENABLE, 
    "RQI_KIND_OF_PART_ID" NUMBER NOT NULL ENABLE, 
    "RQI_QUANTITY" NUMBER NOT NULL ENABLE,
    "COMMENT_DESCRIPTION" VARCHAR2(1024 BYTE), 
    "RECORD_INSERTION_TIME" TIMESTAMP (6) NOT NULL ENABLE, 
    "RECORD_INSERTION_USER" VARCHAR2(50 BYTE) NOT NULL ENABLE, 
    "RECORD_LASTUPDATE_TIME" TIMESTAMP (6), 
    "RECORD_LASTUPDATE_USER" VARCHAR2(50 BYTE),

    CONSTRAINT "REQUEST_ITEMS_HST_PK" PRIMARY KEY ("HISTORY_RECORD_ID")

 );

CREATE SEQUENCE "ANY_REQUEST_ITM_HISTORY_ID_SEQ"
 INCREMENT BY 20
 START WITH 1000
 NOMAXVALUE
 MINVALUE 1000
 NOCYCLE
 CACHE 20
/

CREATE SEQUENCE "ANY_REQUEST_ITM_ID_SEQ"
 INCREMENT BY 20
 START WITH 1000
 NOMAXVALUE
 MINVALUE 1000
 NOCYCLE
 CACHE 20
/

create or replace TRIGGER TR_INS_REQUEST_ITM
    BEFORE INSERT
    ON REQUEST_ITEMS
    REFERENCING OLD AS OLD NEW AS NEW
    FOR EACH ROW
BEGIN

    if :new.rqi_id is null then
        SELECT ANY_REQUEST_ITM_ID_SEQ.NEXTVAL INTO :new.rqi_id FROM dual;
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

create or replace TRIGGER TR_UPD_REQUEST_ITM
    BEFORE UPDATE
    ON REQUEST_ITEMS
    REFERENCING OLD AS OLD NEW AS NEW
    FOR EACH ROW
DECLARE
    seqPkId number(38,0);
    recordChangeTime TIMESTAMP(6) WITH TIME ZONE;
    recordChangeUser VARCHAR2(50 );
BEGIN
    SELECT ANY_REQUEST_ITM_HISTORY_ID_SEQ.NEXTVAL INTO seqPkId FROM dual;

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


    -- protect the original record data
    :new.RECORD_INSERTION_TIME := :OLD.RECORD_INSERTION_TIME;
    :new.RECORD_INSERTION_USER := :OLD.RECORD_INSERTION_USER;
    :new.RQI_ID :=  :OLD.RQI_ID;

    -- update last change record info, if needed
        :new.RECORD_LASTUPDATE_TIME  := SYSDATE;

    if :new.RECORD_LASTUPDATE_USER is null then
        :new.RECORD_LASTUPDATE_USER := USER;
    end if;

    insert into REQUEST_ITEMS_HST
    (
        HISTORY_RECORD_ID,
        RQI_ID,
        RQI_REQ_ID,
        RQI_KIND_OF_PART_ID,
        RQI_QUANTITY,
        COMMENT_DESCRIPTION,
        RECORD_INSERTION_TIME,
        RECORD_INSERTION_USER,
        RECORD_LASTUPDATE_TIME,
        RECORD_LASTUPDATE_USER
    )
    values
    (
            seqPkId    ,
        :OLD.RQI_ID,
        :OLD.RQI_REQ_ID,
        :OLD.RQI_KIND_OF_PART_ID,
        :OLD.RQI_QUANTITY,
        :OLD.COMMENT_DESCRIPTION,
        :new.RECORD_LASTUPDATE_TIME ,
        :new.RECORD_LASTUPDATE_USER,
        recordChangeTime,
        recordChangeUser
    );

    EXCEPTION
        WHEN OTHERS THEN
        -- Consider logging the error and then re-raise
        RAISE;
END ;
/

/*
SHIPMENTS
*/
CREATE TABLE "SHIPMENTS" (

    "SHP_ID" NUMBER(32,0) NOT NULL ENABLE, 
    "SHP_COMPANY_NAME" VARCHAR2(128 BYTE) NOT NULL ENABLE, 
    "SHP_TRACKING_NUMBER" VARCHAR2(128 BYTE) NOT NULL ENABLE, 
    "SHP_FROM_LOCATION_ID" NUMBER(32,0) NOT NULL ENABLE, 
    "SHP_TO_LOCATION_ID" NUMBER(32,0) NOT NULL ENABLE, 
    "SHP_STATUS" VARCHAR2(20 BYTE) DEFAULT 'PACKAGING' NOT NULL ENABLE, 
    "COMMENT_DESCRIPTION" VARCHAR2(1024 BYTE), 
    "SHP_DATE" DATE NOT NULL ENABLE, 
    "SHP_PERSON" VARCHAR2(128 BYTE) NOT NULL ENABLE, 
    "RECORD_INSERTION_TIME" TIMESTAMP (6) NOT NULL ENABLE, 
    "RECORD_INSERTION_USER" VARCHAR2(50 BYTE) NOT NULL ENABLE, 
    "RECORD_LASTUPDATE_TIME" TIMESTAMP (6), 
    "RECORD_LASTUPDATE_USER" VARCHAR2(50 BYTE), 

    CONSTRAINT "SHIPMENTS_PK" PRIMARY KEY ("SHP_ID"), 
    CONSTRAINT "SHIPMENTS_CHK1" CHECK (SHP_STATUS in ('PACKAGING', 'SHIPPED','RECEIVED','CANCELED')) ENABLE, 
    CONSTRAINT "SHIPMENTS_UK1" UNIQUE ("SHP_TRACKING_NUMBER"), 
    CONSTRAINT "SHIPMENTS_FK1" FOREIGN KEY ("SHP_FROM_LOCATION_ID") REFERENCES "CMS_&det._CORE_MANAGEMNT"."LOCATIONS" ("LOCATION_ID") ENABLE, 
    CONSTRAINT "SHIPMENTS_FK2" FOREIGN KEY ("SHP_TO_LOCATION_ID") REFERENCES "CMS_&det._CORE_MANAGEMNT"."LOCATIONS" ("LOCATION_ID") ENABLE

);

CREATE SEQUENCE "ANY_SHIPMENTS_ID_SEQ"
 INCREMENT BY 20
 START WITH 1000
 NOMAXVALUE
 MINVALUE 1000
 NOCYCLE
 CACHE 20
/

GRANT SELECT ON SHIPMENTS TO PUBLIC;
GRANT INSERT, UPDATE ON SHIPMENTS TO CMS_&det._&subdet._CONSTRUCT_WRITER;
GRANT SELECT, REFERENCES ON SHIPMENTS TO CMS_&det._&subdet._CONSTRUCT;

/*
SHIPMENTS HISTORY
*/

CREATE TABLE "SHIPMENTS_HST" (

    "HISTORY_RECORD_ID" NUMBER(32,0) NOT NULL ENABLE, 
    "SHP_ID" NUMBER(32,0) NOT NULL ENABLE, 
    "SHP_COMPANY_NAME" VARCHAR2(128 BYTE) NOT NULL ENABLE, 
    "SHP_TRACKING_NUMBER" VARCHAR2(128 BYTE) NOT NULL ENABLE, 
    "SHP_FROM_LOCATION_ID" NUMBER(32,0) NOT NULL ENABLE, 
    "SHP_TO_LOCATION_ID" NUMBER(32,0) NOT NULL ENABLE, 
    "SHP_STATUS" VARCHAR2(20 BYTE) DEFAULT 'PACKAGING' NOT NULL ENABLE, 
    "COMMENT_DESCRIPTION" VARCHAR2(1024 BYTE), 
    "SHP_DATE" DATE NOT NULL ENABLE, 
    "SHP_PERSON" VARCHAR2(128 BYTE) NOT NULL ENABLE, 
    "RECORD_INSERTION_TIME" TIMESTAMP (6) NOT NULL ENABLE, 
    "RECORD_INSERTION_USER" VARCHAR2(50 BYTE) NOT NULL ENABLE, 
    "RECORD_LASTUPDATE_TIME" TIMESTAMP (6), 
    "RECORD_LASTUPDATE_USER" VARCHAR2(50 BYTE), 

    CONSTRAINT "SHIPMENTS_HST_PK" PRIMARY KEY ("HISTORY_RECORD_ID")

);

CREATE SEQUENCE "ANY_SHIPMENTS_HISTORY_ID_SEQ"
 INCREMENT BY 20
 START WITH 1000
 NOMAXVALUE
 MINVALUE 1000
 NOCYCLE
 CACHE 20
/

create or replace TRIGGER TR_INS_SHIPMENTS
    BEFORE INSERT
    ON SHIPMENTS
    REFERENCING OLD AS OLD NEW AS NEW
    FOR EACH ROW
BEGIN

    if :new.shp_id is null then
        SELECT ANY_SHIPMENTS_ID_SEQ.NEXTVAL INTO :new.shp_id FROM dual;
    end if;

        :NEW.RECORD_INSERTION_TIME := SYSDATE;

    if :new.RECORD_INSERTION_USER is null then
        :NEW.RECORD_INSERTION_USER  := USER;
    end if;

    EXCEPTION
        WHEN OTHERS THEN
        -- Consider logging the error and then re-raise
        RAISE;
END ;
/

create or replace TRIGGER TR_UPD_SHIPMENTS
    BEFORE UPDATE
    ON SHIPMENTS
    REFERENCING OLD AS OLD NEW AS NEW
    FOR EACH ROW
    DECLARE
    seqPkId number(38,0);
    recordChangeTime TIMESTAMP(6) WITH TIME ZONE;
    recordChangeUser VARCHAR2(50 );
BEGIN
    SELECT ANY_SHIPMENTS_HISTORY_ID_SEQ.NEXTVAL INTO seqPkId FROM dual;

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


    -- protect the original record data
    :new.RECORD_INSERTION_TIME := :OLD.RECORD_INSERTION_TIME;
    :new.RECORD_INSERTION_USER := :OLD.RECORD_INSERTION_USER;
    :new.SHP_ID :=  :OLD.SHP_ID;

    -- update last change record info, if needed
        :new.RECORD_LASTUPDATE_TIME  := SYSDATE;

    if :new.RECORD_LASTUPDATE_USER is null then
        :new.RECORD_LASTUPDATE_USER := USER;
    end if;

    insert into SHIPMENTS_HST
    (
        HISTORY_RECORD_ID,
        SHP_ID,
        SHP_COMPANY_NAME,
        SHP_TRACKING_NUMBER,
        SHP_FROM_LOCATION_ID,
        SHP_TO_LOCATION_ID,
        SHP_STATUS,
        COMMENT_DESCRIPTION,
        SHP_DATE,
        SHP_PERSON,
        RECORD_INSERTION_TIME,
        RECORD_INSERTION_USER,
        RECORD_LASTUPDATE_TIME,
        RECORD_LASTUPDATE_USER
    )
    values
    (
            seqPkId    ,
        :OLD.SHP_ID,
        :OLD.SHP_COMPANY_NAME,
        :OLD.SHP_TRACKING_NUMBER,
        :OLD.SHP_FROM_LOCATION_ID,
        :OLD.SHP_TO_LOCATION_ID,
        :OLD.SHP_STATUS,
        :OLD.COMMENT_DESCRIPTION,
        :OLD.SHP_DATE,
        :OLD.SHP_PERSON,
        :new.RECORD_LASTUPDATE_TIME ,
        :new.RECORD_LASTUPDATE_USER,
        recordChangeTime,
        recordChangeUser
    );



    EXCEPTION
        WHEN OTHERS THEN
        -- Consider logging the error and then re-raise
        RAISE;
END ;
/

/*
SHIPMENTS ITEMS
*/
CREATE TABLE "SHIPMENT_ITEMS" (

    "SHI_ID" NUMBER(32,0) NOT NULL ENABLE, 
    "SHI_SHP_ID" NUMBER NOT NULL ENABLE,
    "SHI_PART_ID" NUMBER NOT NULL ENABLE, 
    "SHI_RQI_ID" NUMBER(32,0),
    "COMMENT_DESCRIPTION" VARCHAR2(1024 BYTE), 
    "RECORD_INSERTION_TIME" TIMESTAMP (6) NOT NULL ENABLE, 
    "RECORD_INSERTION_USER" VARCHAR2(50 BYTE) NOT NULL ENABLE, 
    "RECORD_LASTUPDATE_TIME" TIMESTAMP (6), 
    "RECORD_LASTUPDATE_USER" VARCHAR2(50 BYTE), 

    CONSTRAINT "SHIPMENT_ITEMS_PK" PRIMARY KEY ("SHI_ID"),
    CONSTRAINT "SHIPMENT_ITEMS_UK1" UNIQUE ("SHI_SHP_ID", "SHI_PART_ID"),
    CONSTRAINT "SHIPMENT_ITEMS_FK1" FOREIGN KEY ("SHI_PART_ID") REFERENCES "CMS_&det._CORE_CONSTRUCT"."PARTS" ("PART_ID") ENABLE, 
    CONSTRAINT "SHIPMENT_ITEMS_FK2" FOREIGN KEY ("SHI_SHP_ID")  REFERENCES "SHIPMENTS" ("SHP_ID") ENABLE, 
    CONSTRAINT "SHIPMENT_ITEMS_FK3" FOREIGN KEY ("SHI_RQI_ID")  REFERENCES "REQUEST_ITEMS" ("RQI_ID") ENABLE

);

GRANT SELECT ON SHIPMENT_ITEMS TO PUBLIC;
GRANT INSERT, UPDATE ON SHIPMENT_ITEMS TO CMS_&det._&subdet._CONSTRUCT_WRITER;
GRANT SELECT, REFERENCES ON SHIPMENT_ITEMS TO CMS_&det._&subdet._CONSTRUCT;

/*
SHIPMENTS ITEMS HISTORY
*/
CREATE TABLE "SHIPMENT_ITEMS_HST" (

    "HISTORY_RECORD_ID" NUMBER(32,0) NOT NULL ENABLE, 
    "SHI_ID" NUMBER(32,0) NOT NULL ENABLE, 
    "SHI_SHP_ID" NUMBER NOT NULL ENABLE,
    "SHI_PART_ID" NUMBER NOT NULL ENABLE, 
    "SHI_RQI_ID" NUMBER(32,0),
    "COMMENT_DESCRIPTION" VARCHAR2(1024 BYTE), 
    "RECORD_INSERTION_TIME" TIMESTAMP (6) NOT NULL ENABLE, 
    "RECORD_INSERTION_USER" VARCHAR2(50 BYTE) NOT NULL ENABLE, 
    "RECORD_LASTUPDATE_TIME" TIMESTAMP (6), 
    "RECORD_LASTUPDATE_USER" VARCHAR2(50 BYTE), 

    CONSTRAINT "SHIPMENT_ITEMS_HST_PK" PRIMARY KEY ("HISTORY_RECORD_ID")

);

CREATE SEQUENCE  "ANY_SHIPMENT_ITM_ID_SEQ"
 INCREMENT BY 20
 START WITH 1000
 NOMAXVALUE
 MINVALUE 1000
 NOCYCLE
 CACHE 20
/

CREATE SEQUENCE  "ANY_SHIPMENT_ITM_HST_ID_SEQ"
 INCREMENT BY 20
 START WITH 1000
 NOMAXVALUE
 MINVALUE 1000
 NOCYCLE
 CACHE 20
/

create or replace TRIGGER TR_INS_SHIPMENT_ITM
    BEFORE INSERT
    ON SHIPMENT_ITEMS
    REFERENCING OLD AS OLD NEW AS NEW
    FOR EACH ROW
BEGIN

    if :new.shi_id is null then
    SELECT ANY_SHIPMENT_ITM_ID_SEQ.NEXTVAL INTO :new.shi_id FROM dual;
    end if;

        :NEW.RECORD_INSERTION_TIME := SYSDATE;

    if :new.RECORD_INSERTION_USER is null then
        :NEW.RECORD_INSERTION_USER  := USER;
    end if;

    EXCEPTION
        WHEN OTHERS THEN
        -- Consider logging the error and then re-raise
        RAISE;
END ;
/

create or replace TRIGGER TR_UPD_SHIPMENT_ITM
    BEFORE UPDATE
    ON SHIPMENT_ITEMS
    REFERENCING OLD AS OLD NEW AS NEW
    FOR EACH ROW
DECLARE

    seqPkId number(38,0);
    recordChangeTime TIMESTAMP(6) WITH TIME ZONE;
    recordChangeUser VARCHAR2(50 );
BEGIN
      SELECT ANY_SHIPMENT_ITM_HST_ID_SEQ.NEXTVAL INTO seqPkId FROM dual;

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


   -- protect the original record data
   :new.RECORD_INSERTION_TIME := :OLD.RECORD_INSERTION_TIME;
   :new.RECORD_INSERTION_USER := :OLD.RECORD_INSERTION_USER;
   :new.SHI_ID :=  :OLD.SHI_ID;

   -- update last change record info, if needed
       :new.RECORD_LASTUPDATE_TIME  := SYSDATE;

   if :new.RECORD_LASTUPDATE_USER is null then
       :new.RECORD_LASTUPDATE_USER := USER;
   end if;

    insert into SHIPMENT_ITEMS_HST
    (
        HISTORY_RECORD_ID,
        SHI_ID,
        SHI_SHP_ID,
        SHI_PART_ID,
        SHI_RQI_ID,
        COMMENT_DESCRIPTION,
        RECORD_INSERTION_TIME,
        RECORD_INSERTION_USER,
        RECORD_LASTUPDATE_TIME,
        RECORD_LASTUPDATE_USER
    )
    values
    (
            seqPkId    ,
        :OLD.SHI_ID,
        :OLD.SHI_SHP_ID,
        :OLD.SHI_PART_ID,
        :OLD.SHI_RQI_ID,
        :OLD.COMMENT_DESCRIPTION,
        :new.RECORD_LASTUPDATE_TIME ,
        :new.RECORD_LASTUPDATE_USER,
        recordChangeTime,
        recordChangeUser
    );

   EXCEPTION
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
END;
/

GRANT SELECT ON ANY_REQUEST_ITM_HISTORY_ID_SEQ TO CMS_&det._&subdet._CONSTRUCT_WRITER;
GRANT SELECT ON ANY_REQUEST_ITM_ID_SEQ TO CMS_&det._&subdet._CONSTRUCT_WRITER;
GRANT SELECT ON ANY_REQUESTS_HISTORY_ID_SEQ TO CMS_&det._&subdet._CONSTRUCT_WRITER;
GRANT SELECT ON ANY_REQUESTS_ID_SEQ TO CMS_&det._&subdet._CONSTRUCT_WRITER;
GRANT SELECT ON ANY_SHIPMENT_ITM_HST_ID_SEQ TO CMS_&det._&subdet._CONSTRUCT_WRITER;
GRANT SELECT ON ANY_SHIPMENT_ITM_ID_SEQ TO CMS_&det._&subdet._CONSTRUCT_WRITER;
GRANT SELECT ON ANY_SHIPMENTS_HISTORY_ID_SEQ TO CMS_&det._&subdet._CONSTRUCT_WRITER;
GRANT SELECT ON ANY_SHIPMENTS_ID_SEQ TO CMS_&det._&subdet._CONSTRUCT_WRITER;