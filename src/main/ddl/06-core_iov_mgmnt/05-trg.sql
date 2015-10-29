
PROMPT Creating Trigger 'TR_UPD_COND_TAGS'
CREATE OR REPLACE TRIGGER TR_UPD_COND_TAGS
 BEFORE UPDATE
 ON COND_TAGS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN 
   -- protect the original record data
   :new.RECORD_INSERTION_TIME := :OLD.RECORD_INSERTION_TIME;
   :new.RECORD_INSERTION_USER := :OLD.RECORD_INSERTION_USER;
   :new.COND_TAG_ID   := :OLD.COND_TAG_ID;

   EXCEPTION
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
END ;
/
SHOW ERROR


PROMPT Creating Trigger 'TR_INS_COND_TAGS'
CREATE OR REPLACE TRIGGER TR_INS_COND_TAGS
 BEFORE INSERT
 ON COND_TAGS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN 

   if :new.COND_TAG_ID is null then
      SELECT  CNDTAG_ID_SEQ.NEXTVAL INTO :new.COND_TAG_ID FROM dual;
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










PROMPT Creating Trigger 'TR_INS_COND_IOVS'
CREATE OR REPLACE TRIGGER TR_INS_COND_IOVS
 BEFORE INSERT
 ON COND_IOVS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN 

   if :new.COND_IOV_RECORD_ID is null then
      SELECT  CNDIOV_ID_SEQ.NEXTVAL INTO :new.COND_IOV_RECORD_ID FROM dual;
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


PROMPT Creating Trigger 'TR_UPD_COND_IOVS'
CREATE OR REPLACE TRIGGER TR_UPD_COND_IOVS
 BEFORE UPDATE
 ON COND_IOVS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN 
   -- protect the original record data
   :new.RECORD_INSERTION_TIME := :OLD.RECORD_INSERTION_TIME;
   :new.RECORD_INSERTION_USER := :OLD.RECORD_INSERTION_USER;
   :new.COND_IOV_RECORD_ID   := :OLD.COND_IOV_RECORD_ID;

   EXCEPTION
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
END ;
/
SHOW ERROR





