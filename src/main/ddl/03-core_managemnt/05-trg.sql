
PROMPT Creating Trigger 'TR_INS_USERS'
CREATE OR REPLACE TRIGGER TR_INS_USERS
 BEFORE INSERT
 ON USERS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN 

   if :new.RECORD_ID is null then
      SELECT ANY_USER_ID_SEQ.NEXTVAL INTO :new.RECORD_ID FROM dual;
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
SHOW ERROR




PROMPT Creating Trigger 'TR_INS_LOCATIONS'
CREATE OR REPLACE TRIGGER TR_INS_LOCATIONS
 BEFORE INSERT
 ON LOCATIONS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN 

   if :new.LOCATION_ID is null then
      SELECT ANY_LOCATION_ID_SEQ.NEXTVAL INTO :new.LOCATION_ID FROM dual;
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
SHOW ERROR


PROMPT Creating Trigger 'TR_UPD_LOCATIONS'
CREATE OR REPLACE TRIGGER TR_UPD_LOCATIONS
 BEFORE UPDATE
 ON LOCATIONS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
DECLARE

    seqPkId number(38,0);
    recordChangeTime TIMESTAMP(6) WITH TIME ZONE;
    recordChangeUser VARCHAR2(50 );
BEGIN
      SELECT ANY_LOCATION_HISTORY_ID_SEQ.NEXTVAL INTO seqPkId FROM dual;

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
   :new.LOCATION_ID :=  :OLD.LOCATION_ID;

   -- update last change record info, if needed
       :new.RECORD_LASTUPDATE_TIME  := SYSDATE;

   if :new.RECORD_LASTUPDATE_USER is null then
       :new.RECORD_LASTUPDATE_USER := USER;
   end if;

    insert into CMS_&det._CORE_MANAGEMNT.LOCATIONS_HST
    (
        HISTORY_RECORD_ID,
        LOCATION_ID,
		INSTITUTION_ID,
        IS_RECORD_DELETED,
        LOCATION_NAME,
        HISTORY_INSERTION_TIME,
        HISTORY_INSERTION_USER,
        RECORD_LASTUPDATE_TIME,
        RECORD_LASTUPDATE_USER,
        COMMENT_DESCRIPTION
    )
    values
    (
            seqPkId,
        :OLD.LOCATION_ID,
        :OLD.INSTITUTION_ID,
        :OLD.IS_RECORD_DELETED,
        :OLD.LOCATION_NAME,
        :new.RECORD_LASTUPDATE_TIME ,
        :new.RECORD_LASTUPDATE_USER,
        recordChangeTime,
        recordChangeUser,
        :OLD.COMMENT_DESCRIPTION
    );



   EXCEPTION
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
END ;
/
SHOW ERROR






PROMPT Creating Trigger 'TR_INS_INSTITUTIONS'
CREATE OR REPLACE TRIGGER TR_INS_INSTITUTIONS
 BEFORE INSERT
 ON INSTITUTIONS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN 

   if :new.INSTITUTION_ID is null then
      SELECT ANY_INSTITUTION_ID_SEQ.NEXTVAL INTO :new.INSTITUTION_ID FROM dual;
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
SHOW ERROR


PROMPT Creating Trigger 'TR_UPD_INSTITUTIONS'
CREATE OR REPLACE TRIGGER TR_UPD_INSTITUTIONS
 BEFORE UPDATE
 ON INSTITUTIONS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
DECLARE

    seqPkId number(38,0);
    recordChangeTime TIMESTAMP(6) WITH TIME ZONE;
    recordChangeUser VARCHAR2(50 );
BEGIN
      SELECT ANY_INSTITUTION_HISTORY_ID_SEQ.NEXTVAL INTO seqPkId FROM dual;

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
   :new.INSTITUTION_ID :=  :OLD.INSTITUTION_ID;

   -- update last change record info, if needed
       :new.RECORD_LASTUPDATE_TIME  := SYSDATE;

   if :new.RECORD_LASTUPDATE_USER is null then
       :new.RECORD_LASTUPDATE_USER := USER;
   end if;

    insert into CMS_&det._CORE_MANAGEMNT.INSTITUTIONS_HST
    (
       HISTORY_RECORD_ID,
       INSTITUTION_ID,
	   IS_RECORD_DELETED,
       INSTITUTE_CODE,
       NAME,
       TOWN,
       COUNTRY,
       HISTORY_INSERTION_TIME,
       HISTORY_INSERTION_USER,
       RECORD_LASTUPDATE_TIME,
       RECORD_LASTUPDATE_USER,
       COMMENT_DESCRIPTION
    )
    values
    (
            seqPkId,
        :OLD.INSTITUTION_ID,
        :OLD.IS_RECORD_DELETED,
        :OLD.INSTITUTE_CODE,
        :OLD.NAME,
        :OLD.TOWN,
        :OLD.COUNTRY,
        :new.RECORD_LASTUPDATE_TIME ,
        :new.RECORD_LASTUPDATE_USER,
        recordChangeTime,
        recordChangeUser,
        :OLD.COMMENT_DESCRIPTION
    );



   EXCEPTION
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
END ;
/
SHOW ERROR







