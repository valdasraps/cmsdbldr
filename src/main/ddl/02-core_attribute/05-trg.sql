
PROMPT Creating Trigger 'TR_INS_EXT_POS_SCHMS'
CREATE OR REPLACE TRIGGER TR_INS_EXT_POS_SCHMS
 BEFORE INSERT
 ON EXT_POSITION_SCHEMAS
 FOR EACH ROW
BEGIN 
   if :new.ATTRIBUTE_ID  is null then 
      SELECT CMS_&det._CORE_ATTRIBUTE.PREDEF_ATTR_ID_SEQ.NEXTVAL INTO :new.ATTRIBUTE_ID  FROM dual; 
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




PROMPT Creating Trigger 'TR_UPD_POSITION_SCHEMAS'
CREATE OR REPLACE TRIGGER TR_UPD_POSITION_SCHEMAS
 BEFORE UPDATE
 ON POSITION_SCHEMAS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN 
   -- protect the original record data
   :new.RECORD_INSERTION_TIME := :OLD.RECORD_INSERTION_TIME;
   :new.RECORD_INSERTION_USER := :OLD.RECORD_INSERTION_USER;
   :new.ATTRIBUTE_ID :=  :OLD.ATTRIBUTE_ID;

   EXCEPTION
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
END ;
/
SHOW ERROR


PROMPT Creating Trigger 'TR_INS_POSITION_SCHEMAS'
CREATE OR REPLACE TRIGGER TR_INS_POSITION_SCHEMAS
 BEFORE INSERT
 ON POSITION_SCHEMAS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN 
   if :new.ATTRIBUTE_ID  is null then     
      SELECT CMS_&det._CORE_ATTRIBUTE.PREDEF_ATTR_ID_SEQ.NEXTVAL INTO :new.ATTRIBUTE_ID  FROM dual;
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




PROMPT Creating Trigger 'TR_INS_ATTR_CATALOGS'
CREATE OR REPLACE TRIGGER TR_INS_ATTR_CATALOGS
 BEFORE INSERT
 ON ATTR_CATALOGS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN 

   if :new.ATTR_CATALOG_ID  is null then     
      SELECT CMS_&det._CORE_ATTRIBUTE.ATTR_CATALOG_ID_SEQ.NEXTVAL INTO :new.ATTR_CATALOG_ID  FROM dual;
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


PROMPT Creating Trigger 'TR_UPD_ATTR_CATALOGS'
CREATE OR REPLACE TRIGGER TR_UPD_ATTR_CATALOGS
 BEFORE UPDATE
 ON ATTR_CATALOGS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
DECLARE


    seqPkId number(38);
    recordChangeTime TIMESTAMP(6) WITH TIME ZONE;
    recordChangeUser VARCHAR2(50 );
BEGIN
      SELECT CMS_&det._CORE_ATTRIBUTE.ATTR_CATALOG_HISTORY_ID_SEQ.NEXTVAL INTO seqPkId FROM dual;

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
   :new.ATTR_CATALOG_ID :=  :OLD.ATTR_CATALOG_ID;

   -- update last change record info, if needed
       :new.RECORD_LASTUPDATE_TIME  := SYSDATE;

   if :new.RECORD_LASTUPDATE_USER is null then
       :new.RECORD_LASTUPDATE_USER := USER;
   end if;

    insert into CMS_&det._CORE_ATTRIBUTE.ATTR_CATALOGS_HST
    (
	HISTORY_RECORD_ID,
	ATTR_CATALOG_ID,
	IS_RECORD_DELETED,
	DISPLAY_NAME,
	HISTORY_INSERTION_TIME,
	HISTORY_INSERTION_USER,
	IS_UNIQUE_WITHIN_PARENT,
	RECORD_LASTUPDATE_TIME,
	RECORD_LASTUPDATE_USER,
	COMMENT_DESCRIPTION
    )
    VALUES
    (
        seqPkId,
        :OLD.ATTR_CATALOG_ID,
        :OLD.IS_RECORD_DELETED,
        :OLD.DISPLAY_NAME,
        :new.RECORD_LASTUPDATE_TIME ,
        :new.RECORD_LASTUPDATE_USER,
       :new.IS_UNIQUE_WITHIN_PARENT,
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




PROMPT Creating Trigger 'TR_UPD_COND_ALGORITHMS'
CREATE OR REPLACE TRIGGER TR_UPD_COND_ALGORITHMS
 BEFORE UPDATE
 ON COND_ALGORITHMS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN 
   -- protect the original record data
   :new.RECORD_INSERTION_TIME := :OLD.RECORD_INSERTION_TIME;
   :new.RECORD_INSERTION_USER := :OLD.RECORD_INSERTION_USER;
   :new.ATTRIBUTE_ID :=  :OLD.ATTRIBUTE_ID;

   EXCEPTION
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
END ;
/
SHOW ERROR


PROMPT Creating Trigger 'TR_INS_COND_ALGORITHMS'
CREATE OR REPLACE TRIGGER TR_INS_COND_ALGORITHMS
 BEFORE INSERT
 ON COND_ALGORITHMS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN 
   if :new.ATTRIBUTE_ID  is null then     
      SELECT CMS_&det._CORE_ATTRIBUTE.PREDEF_ATTR_ID_SEQ.NEXTVAL INTO :new.ATTRIBUTE_ID  FROM dual;
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




PROMPT Creating Trigger 'TR_UPD_ATTR_BASES'
CREATE OR REPLACE TRIGGER TR_UPD_ATTR_BASES
 BEFORE UPDATE
 ON ATTR_BASES
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN 
   -- protect the original record data
   :new.RECORD_INSERTION_TIME := :OLD.RECORD_INSERTION_TIME;
   :new.RECORD_INSERTION_USER := :OLD.RECORD_INSERTION_USER;
   :new.ATTRIBUTE_ID :=  :OLD.ATTRIBUTE_ID;

   -- update last change record info, if needed
       :new.RECORD_DEL_FLAG_TIME  := SYSDATE;

   if :new.RECORD_DEL_FLAG_USER is null then
       :new.RECORD_DEL_FLAG_USER := USER;
   end if;

   EXCEPTION
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
END ;
/
SHOW ERROR


PROMPT Creating Trigger 'TR_INS_ATTR_BASES'
CREATE OR REPLACE TRIGGER TR_INS_ATTR_BASES
 BEFORE INSERT
 ON ATTR_BASES
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN 

   if :new.ATTRIBUTE_ID  is null then     
      SELECT CMS_&det._CORE_ATTRIBUTE.PREDEF_ATTR_ID_SEQ.NEXTVAL INTO :new.ATTRIBUTE_ID  FROM dual;
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




PROMPT Creating Trigger 'TR_INS_MODES_STAGES'
CREATE OR REPLACE TRIGGER TR_INS_MODES_STAGES
 BEFORE INSERT
 ON MODES_STAGES
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN 
   if :new.ATTRIBUTE_ID  is null then     
      SELECT CMS_&det._CORE_ATTRIBUTE.PREDEF_ATTR_ID_SEQ.NEXTVAL INTO :new.ATTRIBUTE_ID  FROM dual;
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


PROMPT Creating Trigger 'TR_UPD_MODES_STAGES'
CREATE OR REPLACE TRIGGER TR_UPD_MODES_STAGES
 BEFORE UPDATE
 ON MODES_STAGES
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN 
   -- protect the original record data
   :new.RECORD_INSERTION_TIME := :OLD.RECORD_INSERTION_TIME;
   :new.RECORD_INSERTION_USER := :OLD.RECORD_INSERTION_USER;
   :new.ATTRIBUTE_ID :=  :OLD.ATTRIBUTE_ID;

   EXCEPTION
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
END ;
/
SHOW ERROR





