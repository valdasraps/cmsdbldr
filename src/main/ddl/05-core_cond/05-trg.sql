
PROMPT Creating Trigger 'TR_INS_COND_DATA_SETS'
CREATE OR REPLACE TRIGGER TR_INS_COND_DATA_SETS
 BEFORE INSERT
 ON COND_DATA_SETS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN 
   if :new.CONDITION_DATA_SET_ID  is null then     
      SELECT CMS_&det._CORE_COND.COND_DATA_SET_ID_SEQ.NEXTVAL INTO :new.CONDITION_DATA_SET_ID FROM dual;   
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


PROMPT Creating Trigger 'TR_UPD_COND_DATA_SETS'
CREATE OR REPLACE TRIGGER TR_UPD_COND_DATA_SETS
 BEFORE UPDATE
 ON COND_DATA_SETS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN 
   -- protect the original record data
   :new.RECORD_INSERTION_TIME := :OLD.RECORD_INSERTION_TIME;
   :new.RECORD_INSERTION_USER := :OLD.RECORD_INSERTION_USER;
   :new.CONDITION_DATA_SET_ID := :OLD.CONDITION_DATA_SET_ID;
   :new.KIND_OF_CONDITION_ID := :OLD.KIND_OF_CONDITION_ID;
   :new.EXTENSION_TABLE_NAME := :OLD.EXTENSION_TABLE_NAME;

   if :OLD.PART_ID is not null then
     :NEW.PART_ID  := :OLD.PART_ID;
   end if;

   if :OLD.COND_RUN_ID  is not null then
     :NEW.COND_RUN_ID   := :OLD.COND_RUN_ID ;
   end if;

   if :OLD.CHANNEL_MAP_ID  is not null then
     :NEW.CHANNEL_MAP_ID   := :OLD.CHANNEL_MAP_ID ;
   end if;

  
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

CREATE OR REPLACE PROCEDURE DELETE_COND_ATTR_LISTS (P_CONDITION_DATA_SET_ID IN NUMBER, P_RELATIONSHIP_ID IN NUMBER) AS
    PRAGMA AUTONOMOUS_TRANSACTION;
    l_is_multiple_attrs char(1);
BEGIN

select
    nvl(b.IS_MULTIPLE_ATTRS,'F') into l_is_multiple_attrs
from
    CMS_&det._CORE_COND.COND_TO_ATTR_RLTNSHPS a
            join CMS_&det._CORE_ATTRIBUTE.ATTR_CATALOGS b
on a.ATTR_CATALOG_ID = b.ATTR_CATALOG_ID
where
    a.RELATIONSHIP_ID = P_RELATIONSHIP_ID;

if l_is_multiple_attrs = 'F' then
update
    CMS_&det._CORE_COND.COND_ATTR_LISTS a
set
    a.IS_RECORD_DELETED = 'T'
where
    a.CONDITION_DATA_SET_ID = P_CONDITION_DATA_SET_ID and
    a.RELATIONSHIP_ID = P_RELATIONSHIP_ID and
    a.IS_RECORD_DELETED = 'F';

commit;
end if;

END DELETE_COND_ATTR_LISTS;
/
SHOW ERROR


PROMPT Creating Trigger 'TR_INS_COND_KOP_REL'
CREATE OR REPLACE TRIGGER TR_INS_COND_KOP_REL
 BEFORE INSERT
 ON COND_TO_PART_RLTNSHPS
 FOR EACH ROW
BEGIN 
   if :new.RELATIONSHIP_ID  is null then
      SELECT CMS_&det._CORE_ATTRIBUTE.ANY_RLTNSHP_ID_SEQ.NEXTVAL INTO :new.RELATIONSHIP_ID FROM dual;
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


PROMPT Creating Trigger 'TR_UPD_COND_KOP_REL'
CREATE OR REPLACE TRIGGER TR_UPD_COND_KOP_REL
 BEFORE UPDATE
 ON COND_TO_PART_RLTNSHPS
 FOR EACH ROW
DECLARE

 

    seqPkId number(38);
    recordChangeTime TIMESTAMP(6) WITH TIME ZONE;
    recordChangeUser VARCHAR2(50 );
BEGIN
      SELECT CMS_&det._CORE_ATTRIBUTE.ANY_RLTNSHP_HISTORY_ID_SEQ.NEXTVAL INTO seqPkId FROM dual;

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
   :new.RELATIONSHIP_ID :=  :OLD.RELATIONSHIP_ID;

   -- update last change record info, if needed
       :new.RECORD_LASTUPDATE_TIME  := SYSDATE;

   if :new.RECORD_LASTUPDATE_USER is null then
       :new.RECORD_LASTUPDATE_USER := USER;
   end if;

    insert into CMS_&det._CORE_COND.COND_TO_ATTR_RLTNSHPS_HST
    (
	  HISTORY_RECORD_ID,
	  RELATIONSHIP_ID,
	  IS_RECORD_DELETED,
	  DISPLAY_NAME,
	  HISTORY_INSERTION_TIME,
	  HISTORY_INSERTION_USER,
	  RECORD_LASTUPDATE_TIME,
	  RECORD_LASTUPDATE_USER,
	  COMMENT_DESCRIPTION
    )
    VALUES
    (
        seqPkId,
        :OLD.RELATIONSHIP_ID,
        :OLD.IS_RECORD_DELETED,
        :OLD.DISPLAY_NAME,
        :new.RECORD_LASTUPDATE_TIME,
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




PROMPT Creating Trigger 'TR_INS_COND_TO_ATTR_RLTNSHPS'
CREATE OR REPLACE TRIGGER TR_INS_COND_TO_ATTR_RLTNSHPS
 BEFORE INSERT
 ON COND_TO_ATTR_RLTNSHPS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN 
   if :new.RELATIONSHIP_ID  is null then
      SELECT CMS_&det._CORE_ATTRIBUTE.ANY_RLTNSHP_ID_SEQ.NEXTVAL INTO :new.RELATIONSHIP_ID FROM dual;
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


PROMPT Creating Trigger 'TR_UPD_COND_TO_ATTR_RLTNSHPS'
CREATE OR REPLACE TRIGGER TR_UPD_COND_TO_ATTR_RLTNSHPS
 BEFORE UPDATE
 ON COND_TO_ATTR_RLTNSHPS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
DECLARE

 

    seqPkId number(38);
    recordChangeTime TIMESTAMP(6) WITH TIME ZONE;
    recordChangeUser VARCHAR2(50 );
BEGIN
      SELECT CMS_&det._CORE_ATTRIBUTE.ANY_RLTNSHP_HISTORY_ID_SEQ.NEXTVAL INTO seqPkId FROM dual;

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
   :new.RELATIONSHIP_ID :=  :OLD.RELATIONSHIP_ID;

   -- update last change record info, if needed
       :new.RECORD_LASTUPDATE_TIME  := SYSDATE;

   if :new.RECORD_LASTUPDATE_USER is null then
       :new.RECORD_LASTUPDATE_USER := USER;
   end if;

    insert into CMS_&det._CORE_COND.COND_TO_ATTR_RLTNSHPS_HST
    (
	  HISTORY_RECORD_ID,
	  RELATIONSHIP_ID,
	  IS_RECORD_DELETED,
	  DISPLAY_NAME,
	  HISTORY_INSERTION_TIME,
	  HISTORY_INSERTION_USER,
	  RECORD_LASTUPDATE_TIME,
	  RECORD_LASTUPDATE_USER,
	  COMMENT_DESCRIPTION
    )
    VALUES
    (
        seqPkId,
        :OLD.RELATIONSHIP_ID,
        :OLD.IS_RECORD_DELETED,
        :OLD.DISPLAY_NAME,
        :new.RECORD_LASTUPDATE_TIME,
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






PROMPT Creating Trigger 'TR_UPD_KINDS_OF_CONDITIONS'
CREATE OR REPLACE TRIGGER TR_UPD_KINDS_OF_CONDITIONS
 BEFORE UPDATE
 ON KINDS_OF_CONDITIONS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
DECLARE

    seqPkId number(38);
    recordChangeTime TIMESTAMP(6) WITH TIME ZONE;
    recordChangeUser VARCHAR2(50 );
BEGIN
      SELECT CMS_&det._CORE_ATTRIBUTE.ANY_KINDOFIFC_HISTORY_ID_SEQ.NEXTVAL INTO seqPkId FROM dual;

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
   :new.KIND_OF_CONDITION_ID :=  :OLD.KIND_OF_CONDITION_ID;

   -- update last change record info, if needed
       :new.RECORD_LASTUPDATE_TIME  := SYSDATE;

   if :new.RECORD_LASTUPDATE_USER is null then
       :new.RECORD_LASTUPDATE_USER := USER;
   end if;

    insert into CMS_&det._CORE_COND.KIND_OF_CONDITIONS_HST
    (
	  HISTORY_RECORD_ID,
	  KIND_OF_CONDITION_ID,
	  IS_RECORD_DELETED,
	  NAME,
	  EXTENSION_TABLE_NAME,
	  HISTORY_INSERTION_TIME,
	  HISTORY_INSERTION_USER,
	  RECORD_LASTUPDATE_TIME,
	  RECORD_LASTUPDATE_USER,
	  COMMENT_DESCRIPTION
   )
    VALUES
    (
        seqPkId,
        :OLD.KIND_OF_CONDITION_ID,
        :OLD.IS_RECORD_DELETED,
        :OLD.NAME,
        :OLD.EXTENSION_TABLE_NAME,
        :new.RECORD_LASTUPDATE_TIME,
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


PROMPT Creating Trigger 'TR_INS_KINDS_OF_CONDITIONS'
CREATE OR REPLACE TRIGGER TR_INS_KINDS_OF_CONDITIONS
 BEFORE INSERT
 ON KINDS_OF_CONDITIONS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN 
   if :new.KIND_OF_CONDITION_ID  is null then     
      SELECT CMS_&det._CORE_ATTRIBUTE.ANY_KINDOFIFC_ID_SEQ.NEXTVAL INTO :new.KIND_OF_CONDITION_ID FROM dual;   
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






PROMPT Creating Trigger 'TR_UPD_COND_ATTR_LISTS'
CREATE OR REPLACE TRIGGER TR_UPD_COND_ATTR_LISTS
 BEFORE UPDATE
 ON COND_ATTR_LISTS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN 
   -- protect the original record data
   :new.RECORD_INSERTION_TIME := :OLD.RECORD_INSERTION_TIME;
   :new.RECORD_INSERTION_USER := :OLD.RECORD_INSERTION_USER;
   :new.ATTR_LIST_RECORD_ID   := :OLD.ATTR_LIST_RECORD_ID;

   EXCEPTION
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
END ;
/
SHOW ERROR


PROMPT Creating Trigger 'TR_INS_COND_ATTR_LISTS'
CREATE OR REPLACE TRIGGER TR_INS_COND_ATTR_LISTS
 BEFORE INSERT
 ON COND_ATTR_LISTS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
DECLARE
 
tmpRel NUMBER(38,0);
BEGIN
   tmpRel := 0;

   if :new.ATTR_LIST_RECORD_ID  is null then     
   	SELECT CMS_&det._CORE_ATTRIBUTE.ANY_ATTR_LIST_REC_ID_SEQ.NEXTVAL INTO :new.ATTR_LIST_RECORD_ID FROM dual;  
   end if;
    
  SELECT R.RELATIONSHIP_ID into tmpRel
   FROM CMS_&det._CORE_ATTRIBUTE.ATTR_CATALOGS B 
  	   INNER JOIN CMS_&det._CORE_ATTRIBUTE.ATTR_BASES A 
	   		 ON A.ATTR_CATALOG_ID = B.ATTR_CATALOG_ID and A.IS_RECORD_DELETED = 'F' and B.IS_RECORD_DELETED = 'F' 
       INNER JOIN CMS_&det._CORE_COND.COND_TO_ATTR_RLTNSHPS R 
	   		 ON R.ATTR_CATALOG_ID = B.ATTR_CATALOG_ID and R.IS_RECORD_DELETED = 'F'
       INNER JOIN CMS_&det._CORE_COND.COND_DATA_SETS D 
	   		 ON R.KIND_OF_CONDITION_ID = D.KIND_OF_CONDITION_ID and D.IS_RECORD_DELETED = 'F'
	   WHERE 
	   		 A.ATTRIBUTE_ID = :new.ATTRIBUTE_ID
	   AND D.CONDITION_DATA_SET_ID =  :new.CONDITION_DATA_SET_ID; 


      :NEW.RECORD_INSERTION_TIME := SYSDATE;

   if :new.RECORD_INSERTION_USER is null then
     :NEW.RECORD_INSERTION_USER  := USER;
   end if;
      
   :NEW.RELATIONSHIP_ID :=tmpRel;
   
   EXCEPTION
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
END ;
/
SHOW ERROR






PROMPT Creating Trigger 'TR_INS_CHANNEL_MAPS_BASE'
CREATE OR REPLACE TRIGGER TR_INS_CHANNEL_MAPS_BASE
 BEFORE INSERT
 ON CHANNEL_MAPS_BASE
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN 
   if :new.CHANNEL_MAP_ID  is null then
      SELECT CMS_&det._CORE_COND.ANY_COND_RECORD_ID_SEQ.NEXTVAL INTO :new.CHANNEL_MAP_ID FROM dual;
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






PROMPT Creating Trigger 'TR_INS_COND_RUNS'
CREATE OR REPLACE TRIGGER TR_INS_COND_RUNS
 BEFORE INSERT
 ON COND_RUNS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN 
   if :new.COND_RUN_ID  is null then     
      SELECT CMS_&det._CORE_COND.COND_RUN_ID_SEQ.NEXTVAL INTO :new.COND_RUN_ID FROM dual;   
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


PROMPT Creating Trigger 'TR_UPD_COND_RUNS'
CREATE OR REPLACE TRIGGER TR_UPD_COND_RUNS
 BEFORE UPDATE
 ON COND_RUNS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
DECLARE
 

    seqPkId number(38);
    recordChangeTime TIMESTAMP(6) WITH TIME ZONE;
    recordChangeUser VARCHAR2(50 );
BEGIN
      SELECT CMS_&det._CORE_COND.COND_RUN_HISTORY_ID_SEQ.NEXTVAL INTO seqPkId FROM dual;

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
   :new.COND_RUN_ID :=  :OLD.COND_RUN_ID;

   -- update last change record info, if needed
       :new.RECORD_LASTUPDATE_TIME  := SYSDATE;

   if :new.RECORD_LASTUPDATE_USER is null then
       :new.RECORD_LASTUPDATE_USER := USER;
   end if;

    insert into CMS_&det._CORE_COND.COND_RUNS_HST
    (
	  HISTORY_RECORD_ID,
	  COND_RUN_ID,
	  IS_RECORD_DELETED,
	  RUN_BEGIN_TIMESTAMP,
	  RUN_NAME,
	  HISTORY_INSERTION_TIME,
	  HISTORY_INSERTION_USER,
	  RECORD_LASTUPDATE_TIME,
	  RECORD_LASTUPDATE_USER,
	  INITIATED_BY_USER,
	  RUN_END_TIMESTAMP,
	  COMMENT_DESCRIPTION
    )
    VALUES
    (
        seqPkId,
        :OLD.COND_RUN_ID,
        :OLD.IS_RECORD_DELETED,
        :OLD.RUN_BEGIN_TIMESTAMP,
        :OLD.RUN_NAME,
        :new.RECORD_LASTUPDATE_TIME,
        :new.RECORD_LASTUPDATE_USER,
        recordChangeTime,
        recordChangeUser,
        :OLD.INITIATED_BY_USER,
        :OLD.RUN_END_TIMESTAMP,
        :OLD.COMMENT_DESCRIPTION
    );
   EXCEPTION
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
END ;
/
SHOW ERROR







