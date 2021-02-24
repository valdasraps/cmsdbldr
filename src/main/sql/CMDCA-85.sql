ALTER TABLE REQUESTS DROP CONSTRAINT REQUESTS_CHK1;
ALTER TABLE REQUESTS ADD CONSTRAINT REQUESTS_CHK1 CHECK (REQ_STATUS in ('OPEN','CLOSED','CANCELED','FULFILLED')) ENABLE;

ALTER TABLE SHIPMENT_ITEMS ADD (SHI_IS_PAID CHAR DEFAULT 'F' NOT NULL);
ALTER TABLE SHIPMENT_ITEMS ADD CONSTRAINT SHIPMENT_ITEMS_CHK1 CHECK (SHI_IS_PAID in ('F','T')) ENABLE;

ALTER TABLE REQUESTS DROP COLUMN PAID_BY_INSTITUTION;

ALTER TABLE SHIPMENT_ITEMS_HST ADD (SHI_IS_PAID CHAR);
ALTER TABLE REQUESTS_HST DROP COLUMN PAID_BY_INSTITUTION;

create or replace TRIGGER TR_UPD_REQUESTS
    BEFORE UPDATE OR DELETE
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

    if UPDATING then

      -- protect the original record data
      :new.RECORD_INSERTION_TIME := :OLD.RECORD_INSERTION_TIME;
      :new.RECORD_INSERTION_USER := :OLD.RECORD_INSERTION_USER;
      :new.REQ_ID :=  :OLD.REQ_ID;

     -- update last change record info, if needed
      :new.RECORD_LASTUPDATE_TIME  := SYSDATE;

      if :new.RECORD_LASTUPDATE_USER is null then
        :new.RECORD_LASTUPDATE_USER := USER;
      end if;

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

create or replace TRIGGER TR_UPD_SHIPMENT_ITM
    BEFORE UPDATE OR DELETE
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

   if UPDATING then

       -- protect the original record data
       :new.RECORD_INSERTION_TIME := :OLD.RECORD_INSERTION_TIME;
       :new.RECORD_INSERTION_USER := :OLD.RECORD_INSERTION_USER;
       :new.SHI_ID :=  :OLD.SHI_ID;

       -- update last change record info, if needed
           :new.RECORD_LASTUPDATE_TIME  := SYSDATE;

       if :new.RECORD_LASTUPDATE_USER is null then
           :new.RECORD_LASTUPDATE_USER := USER;
       end if;

    end if;

    insert into SHIPMENT_ITEMS_HST
    (
        HISTORY_RECORD_ID,
        SHI_ID,
        SHI_SHP_ID,
        SHI_PART_ID,
        SHI_RQI_ID,
        SHI_IS_PAID,
        COMMENT_DESCRIPTION,
        RECORD_INSERTION_TIME,
        RECORD_INSERTION_USER,
        RECORD_LASTUPDATE_TIME,
        RECORD_LASTUPDATE_USER
    )
    values
    (
        seqPkId,
        :OLD.SHI_ID,
        :OLD.SHI_SHP_ID,
        :OLD.SHI_PART_ID,
        :OLD.SHI_RQI_ID,
        :OLD.SHI_IS_PAID,
        :OLD.COMMENT_DESCRIPTION,
        sysdate,
        :OLD.RECORD_INSERTION_USER,
        recordChangeTime,
        recordChangeUser
    );

   EXCEPTION
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
END;
/

