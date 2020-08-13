
PROMPT Creating Trigger 'TR_INS_KINDS_OF_PARTS'
CREATE OR REPLACE TRIGGER TR_INS_KINDS_OF_PARTS
 BEFORE INSERT
 ON KINDS_OF_PARTS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN

   if :new.KIND_OF_PART_ID is null then     
      SELECT CMS_&det._CORE_ATTRIBUTE.ANY_KINDOFIFC_ID_SEQ.NEXTVAL INTO :new.KIND_OF_PART_ID FROM dual;
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


PROMPT Creating Trigger 'TR_UPD_KINDS_OF_PARTS'
CREATE OR REPLACE TRIGGER TR_UPD_KINDS_OF_PARTS
 BEFORE UPDATE
 ON KINDS_OF_PARTS
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
   :new.KIND_OF_PART_ID :=  :OLD.KIND_OF_PART_ID;

   -- update last change record info, if needed
       :new.RECORD_LASTUPDATE_TIME  := SYSDATE;

   if :new.RECORD_LASTUPDATE_USER is null then
       :new.RECORD_LASTUPDATE_USER := USER;
   end if;

    insert into CMS_&det._CORE_CONSTRUCT.KINDS_OF_PARTS_HST
    (
        HISTORY_RECORD_ID,
        KIND_OF_PART_ID,
        LPNAME,
        DISPLAY_NAME,
        IS_IMAGINARY_PART,
        SUBDETECTOR_ID,
        IS_RECORD_DELETED,
        IS_DETECTOR_PART,
        EXTENSION_TABLE_NAME,
        MANUFACTURER_ID,
        HISTORY_INSERTION_TIME,
        HISTORY_INSERTION_USER,
        RECORD_LASTUPDATE_TIME,
        RECORD_LASTUPDATE_USER,
        COMMENT_DESCRIPTION
    )
    VALUES
    (
        seqPkId,
        :OLD.KIND_OF_PART_ID,
        :OLD.LPNAME,
        :OLD.DISPLAY_NAME,
        :OLD.IS_IMAGINARY_PART,
        :OLD.SUBDETECTOR_ID,
        :OLD.IS_RECORD_DELETED,
        :OLD.IS_DETECTOR_PART,
        :OLD.EXTENSION_TABLE_NAME,
       :OLD.MANUFACTURER_ID,
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






PROMPT Creating Trigger 'TR_INS_SIGNAL_CONNECTIONS'
CREATE OR REPLACE TRIGGER TR_INS_SIGNAL_CONNECTIONS
 BEFORE INSERT
 ON SIGNAL_CONNECTIONS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN

   if :new.CONNNECTION_ID is null then     
      SELECT ANY_SIGNAL_CONNECTION_ID_SEQ.NEXTVAL INTO :new.CONNNECTION_ID FROM dual;
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








PROMPT Creating Trigger 'TR_UPD_SUBDETECTORS'
CREATE OR REPLACE TRIGGER TR_UPD_SUBDETECTORS
 BEFORE UPDATE
 ON SUBDETECTORS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
DECLARE



    seqPkId number(38);
    recordChangeTime TIMESTAMP(6) WITH TIME ZONE;
    recordChangeUser VARCHAR2(50 );
BEGIN
      SELECT ANY_SUBDETCTOR_HISTORY_ID_SEQ.NEXTVAL INTO seqPkId FROM dual;

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
   :new.SUBDETECTOR_ID :=  :OLD.SUBDETECTOR_ID;

   -- update last change record info, if needed
       :new.RECORD_LASTUPDATE_TIME  := SYSDATE;

   if :new.RECORD_LASTUPDATE_USER is null then
       :new.RECORD_LASTUPDATE_USER := USER;
   end if;

       insert into  CMS_&det._CORE_CONSTRUCT.SUBDETECTORS_HST
    (
        HISTORY_RECORD_ID,
        SUBDETECTOR_ID,
        IS_RECORD_DELETED,
        HISTORY_INSERTION_TIME,
        HISTORY_INSERTION_USER,
        RECORD_LASTUPDATE_TIME,
        RECORD_LASTUPDATE_USER,
        COMMENT_DESCRIPTION,
        SUBDETECTOR_NAME
    )
    values
    (
            seqPkId,
        :OLD.SUBDETECTOR_ID,
        :OLD.IS_RECORD_DELETED,
        :new.RECORD_LASTUPDATE_TIME ,
        :new.RECORD_LASTUPDATE_USER,
        recordChangeTime,
        recordChangeUser,
        :OLD.COMMENT_DESCRIPTION,
        :OLD.SUBDETECTOR_NAME
    );



   EXCEPTION
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
END ;
/
SHOW ERROR


PROMPT Creating Trigger 'TR_INS_SUBDETECTORS'
CREATE OR REPLACE TRIGGER TR_INS_SUBDETECTORS
 BEFORE INSERT
 ON SUBDETECTORS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN

   if :new.SUBDETECTOR_ID is null then     
      SELECT ANY_SUBDETCTOR_ID_SEQ.NEXTVAL INTO :new.SUBDETECTOR_ID FROM dual;
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






PROMPT Creating Trigger 'TR_INS_CONNECTORS'
CREATE OR REPLACE TRIGGER TR_INS_CONNECTORS
 BEFORE INSERT
 ON CONNECTORS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN

   if :new.CONNECTOR_ID is null then     
      SELECT ANY_CONNECTOR_ID_SEQ.NEXTVAL INTO :new.CONNECTOR_ID FROM dual;
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


PROMPT Creating Trigger 'TR_UPD_CONNECTORS'
CREATE OR REPLACE TRIGGER TR_UPD_CONNECTORS
 BEFORE UPDATE
 ON CONNECTORS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
DECLARE
 
    seqPkId number(38);
    recordChangeTime TIMESTAMP(6) WITH TIME ZONE;
    recordChangeUser VARCHAR2(50 );
BEGIN
      SELECT ANY_CONNECTOR_HISTORY_ID_SEQ.NEXTVAL INTO seqPkId FROM dual;

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
   :new.CONNECTOR_ID :=  :OLD.CONNECTOR_ID;

   -- update last change record info, if needed
       :new.RECORD_LASTUPDATE_TIME  := SYSDATE;

   if :new.RECORD_LASTUPDATE_USER is null then
       :new.RECORD_LASTUPDATE_USER := USER;
   end if;


    insert into CMS_&det._CORE_CONSTRUCT.CONNECTORS_HST
    (
        HISTORY_RECORD_ID,
        CONNECTOR_ID,
        PART_ID,
        IS_INPUT_SIGNAL_DIRECTION,
        CONNECTOR_LABEL,
        IS_RECORD_DELETED,
        IS_MALE_SIDE,
        HISTORY_INSERTION_TIME,
        HISTORY_INSERTION_USER,
        RECORD_LASTUPDATE_TIME,
        RECORD_LASTUPDATE_USER,
        COMMENT_DESCRIPTION
    )
    VALUES
    (
        seqPkId,
        :OLD.CONNECTOR_ID,
        :OLD.PART_ID,
        :OLD.IS_INPUT_SIGNAL_DIRECTION,
        :OLD.CONNECTOR_LABEL,
        :OLD.IS_RECORD_DELETED,
        :OLD.IS_MALE_SIDE,
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






PROMPT Creating Trigger 'TR_INS_PART_TO_PART_RLTNSHPS'
CREATE OR REPLACE TRIGGER TR_INS_PART_TO_PART_RLTNSHPS
 BEFORE INSERT
 ON PART_TO_PART_RLTNSHPS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN

   if :new.RELATIONSHIP_ID is null then     
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


PROMPT Creating Trigger 'TR_UPD_PART_TO_PART_RLTNSHPS'
CREATE OR REPLACE TRIGGER TR_UPD_PART_TO_PART_RLTNSHPS
 BEFORE UPDATE
 ON PART_TO_PART_RLTNSHPS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
DECLARE

    seqPkId number(38,0);
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

    insert into CMS_&det._CORE_CONSTRUCT.PART_TO_PART_RLTNSHPS_HST
    (
	HISTORY_RECORD_ID,
	RELATIONSHIP_ID,
	IS_RECORD_DELETED,
	KIND_OF_PART_ID,
	KIND_OF_PART_ID_CHILD,
	PRIORITY_NUMBER,
	DISPLAY_NAME,
	HISTORY_INSERTION_TIME,
	HISTORY_INSERTION_USER,
	RECORD_LASTUPDATE_TIME,
	RECORD_LASTUPDATE_USER,
	COMMENT_DESCRIPTION
    )
    values
    (
            seqPkId,
	:OLD.RELATIONSHIP_ID,
	:OLD.IS_RECORD_DELETED,
	:OLD.KIND_OF_PART_ID,
	:OLD.KIND_OF_PART_ID_CHILD,
	:OLD.PRIORITY_NUMBER,
	:OLD.DISPLAY_NAME,
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






PROMPT Creating Trigger 'TR_INS_PART_ATTR_LISTS'
CREATE OR REPLACE TRIGGER TR_INS_PART_ATTR_LISTS
 BEFORE INSERT
 ON PART_ATTR_LISTS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
DECLARE


tmpVar NUMBER(38,0);
tmpRel NUMBER(38,0);
BEGIN
   tmpVar := 0;
   tmpRel :=0;
   
  SELECT CMS_&det._CORE_ATTRIBUTE.ANY_ATTR_LIST_REC_ID_SEQ.NEXTVAL INTO tmpVar FROM dual;

  SELECT max(R.RELATIONSHIP_ID) into tmpRel 
	FROM CMS_&det._CORE_ATTRIBUTE.ATTR_CATALOGS B
  		INNER JOIN CMS_&det._CORE_ATTRIBUTE.ATTR_BASES A
	   		ON A.ATTR_CATALOG_ID = B.ATTR_CATALOG_ID and 
			       A.IS_RECORD_DELETED = 'F' and B.IS_RECORD_DELETED = 'F'
		INNER JOIN CMS_&det._CORE_CONSTRUCT.PART_TO_ATTR_RLTNSHPS  R
	   		 ON R.ATTR_CATALOG_ID = B.ATTR_CATALOG_ID and R.IS_RECORD_DELETED = 'F'
		INNER JOIN CMS_&det._CORE_CONSTRUCT.PARTS D
	   		 ON R.KIND_OF_PART_ID = D.KIND_OF_PART_ID and D.IS_RECORD_DELETED = 'F'
	   WHERE
	   	A.ATTRIBUTE_ID = :new.ATTRIBUTE_ID  AND D.PART_ID =  :new.PART_ID;
	   	   	   
  update  CMS_&det._CORE_CONSTRUCT.PART_ATTR_LISTS   
	set IS_RECORD_DELETED = 'T' 
	   	 where PART_ID = :new.PART_ID and RELATIONSHIP_ID = tmpRel  
		               and IS_RECORD_DELETED = 'F';
	   
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




PROMPT Creating Trigger 'TR_UPD_SIGNAL_CONN_TYPES'
CREATE OR REPLACE TRIGGER TR_UPD_SIGNAL_CONN_TYPES
 BEFORE UPDATE
 ON SIGNAL_CONNECTION_TYPES
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
DECLARE



    seqPkId number(38,0);
    recordChangeTime TIMESTAMP(6) WITH TIME ZONE;
    recordChangeUser VARCHAR2(50 );
BEGIN
      SELECT ANY_SIGCONTYPE_HISTORY_ID_SEQ.NEXTVAL INTO seqPkId FROM dual;

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
   :new.SIGNAL_CONNECTION_TYPE_ID :=  :OLD.SIGNAL_CONNECTION_TYPE_ID;

   -- update last change record info, if needed
       :new.RECORD_LASTUPDATE_TIME  := SYSDATE;

   if :new.RECORD_LASTUPDATE_USER is null then
       :new.RECORD_LASTUPDATE_USER := USER;
   end if;

       insert into  CMS_&det._CORE_CONSTRUCT.SIGNAL_CONNECTION_TYPES_HST
    (
        HISTORY_RECORD_ID,
        SIGNAL_CONNECTION_TYPE_ID,
        IS_RECORD_DELETED,
        KIND_OF_PART_BEGIN_SIDE_ID,
        KIND_OF_PART_END_SIDE_ID,
        HISTORY_INSERTION_TIME,
        HISTORY_INSERTION_USER,
        RECORD_LASTUPDATE_TIME,
        RECORD_LASTUPDATE_USER,
        COMMENT_DESCRIPTION
    )
    values
    (
                seqPkId,
        :OLD.SIGNAL_CONNECTION_TYPE_ID,
        :OLD.IS_RECORD_DELETED,
        :OLD.KIND_OF_PART_BEGIN_SIDE_ID,
        :OLD.KIND_OF_PART_END_SIDE_ID,
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


PROMPT Creating Trigger 'TR_INS_SIGNAL_CONN_TYPES'
CREATE OR REPLACE TRIGGER TR_INS_SIGNAL_CONN_TYPES
 BEFORE INSERT
 ON SIGNAL_CONNECTION_TYPES
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN

   if :new.SIGNAL_CONNECTION_TYPE_ID is null then     
      SELECT ANY_SIGCONTYPE_ID_SEQ.NEXTVAL INTO :new.SIGNAL_CONNECTION_TYPE_ID FROM dual;
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




PROMPT Creating Trigger 'TR_INS_PART_TO_ATTR_RLTNSHPS'
CREATE OR REPLACE TRIGGER TR_INS_PART_TO_ATTR_RLTNSHPS
 BEFORE INSERT
 ON PART_TO_ATTR_RLTNSHPS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN

   if :new.RELATIONSHIP_ID is null then     
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


PROMPT Creating Trigger 'TR_UPD_PART_TO_ATTR_RLTNSHPS'
CREATE OR REPLACE TRIGGER TR_UPD_PART_TO_ATTR_RLTNSHPS
 BEFORE UPDATE
 ON PART_TO_ATTR_RLTNSHPS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
DECLARE
 
    seqPkId number(38,0);
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

    insert into CMS_&det._CORE_CONSTRUCT.PART_TO_ATTR_RLTNSHPS_HST
    (
      HISTORY_RECORD_ID,
      RELATIONSHIP_ID,
      ATTR_CATALOG_ID,
      IS_RECORD_DELETED,
      KIND_OF_PART_ID,
      DISPLAY_NAME,
      HISTORY_INSERTION_TIME,
      HISTORY_INSERTION_USER,
      RECORD_LASTUPDATE_TIME,
      RECORD_LASTUPDATE_USER,
      COMMENT_DESCRIPTION
    )
    values
    (
            seqPkId,
      :OLD.RELATIONSHIP_ID    ,
      :OLD.ATTR_CATALOG_ID  ,
      :OLD.IS_RECORD_DELETED,
      :OLD.KIND_OF_PART_ID ,
      :OLD.DISPLAY_NAME   ,
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






PROMPT Creating Trigger 'TR_UPD_PARTS'
CREATE OR REPLACE TRIGGER TR_UPD_PARTS
 BEFORE UPDATE
 ON PARTS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
DECLARE



    seqPkId number(38,0);
    recordChangeTime TIMESTAMP(6) WITH TIME ZONE;
    recordChangeUser VARCHAR2(50 );
BEGIN
      SELECT ANY_PHYSCLPART_HISTORY_ID_SEQ.NEXTVAL INTO seqPkId FROM dual;

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
   :new.PART_ID :=  :OLD.PART_ID;

   -- update last change record info, if needed
       :new.RECORD_LASTUPDATE_TIME  := SYSDATE;

   if :new.RECORD_LASTUPDATE_USER is null then
       :new.RECORD_LASTUPDATE_USER := USER;
   end if;

    insert into CMS_&det._CORE_CONSTRUCT.PARTS_HST
    (
        HISTORY_RECORD_ID,
        PART_ID,
        LOCATION_ID,
        KIND_OF_PART_ID,
        MANUFACTURER_ID,
        IS_RECORD_DELETED,
        HISTORY_INSERTION_TIME,
        HISTORY_INSERTION_USER,
        BARCODE,
        SERIAL_NUMBER,
        VERSION,
        NAME_LABEL,
        PRODUCTION_DATE,
        BATCH_NUMBER,
        INSTALLED_DATE,
        REMOVED_DATE,
        INSTALLED_BY_USER,
        REMOVED_BY_USER,
        COMMENT_DESCRIPTION,
        RECORD_LASTUPDATE_TIME,
        RECORD_LASTUPDATE_USER
    )
    values
    (
            seqPkId    ,
        :OLD.PART_ID,
        :OLD.LOCATION_ID,
        :OLD.KIND_OF_PART_ID,
        :OLD.MANUFACTURER_ID,
        :OLD.IS_RECORD_DELETED,
        :new.RECORD_LASTUPDATE_TIME ,
        :new.RECORD_LASTUPDATE_USER,
        :OLD.BARCODE,
        :OLD.SERIAL_NUMBER,
        :OLD.VERSION,
        :OLD.NAME_LABEL,
        :OLD.PRODUCTION_DATE,
        :OLD.BATCH_NUMBER,
        :OLD.INSTALLED_DATE,
        :OLD.REMOVED_DATE,
        :OLD.INSTALLED_BY_USER,
        :OLD.REMOVED_BY_USER,
        :OLD.COMMENT_DESCRIPTION,
        recordChangeTime,
        recordChangeUser
    );



   EXCEPTION
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
END ;
/
SHOW ERROR


PROMPT Creating Trigger 'TR_INS_PARTS'
CREATE OR REPLACE TRIGGER TR_INS_PARTS
 BEFORE INSERT
 ON PARTS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN

   if :new.part_id is null then     
      SELECT ANY_PHYSCLPART_ID_SEQ.NEXTVAL INTO :new.part_id FROM dual;
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








PROMPT Creating Trigger 'TR_INS_PHYSICAL_PARTS_TREE'
CREATE OR REPLACE TRIGGER TR_INS_PHYSICAL_PARTS_TREE
 BEFORE INSERT
 ON PHYSICAL_PARTS_TREE
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN

   if :new.part_id is null then     
      SELECT PHSCLPRTSTREE_ID_SEQ.NEXTVAL INTO :new.part_id FROM dual;
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


PROMPT Creating Trigger 'TR_UPD_PHYSICAL_PARTS_TREE'
CREATE OR REPLACE TRIGGER TR_UPD_PHYSICAL_PARTS_TREE
 BEFORE UPDATE
 ON PHYSICAL_PARTS_TREE
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
DECLARE
 
    seqPkId number(38,0);
    recordChangeTime TIMESTAMP(6) WITH TIME ZONE;
    recordChangeUser VARCHAR2(50 );
BEGIN
      SELECT PHSCLPRTSTREE_HISTORY_ID_SEQ.NEXTVAL INTO seqPkId FROM dual;

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
   :new.PART_ID :=  :OLD.PART_ID;

   -- update last change record info, if needed
       :new.RECORD_LASTUPDATE_TIME  := SYSDATE;

   if :new.RECORD_LASTUPDATE_USER is null then
       :new.RECORD_LASTUPDATE_USER := USER;
   end if;

    insert into  CMS_&det._CORE_CONSTRUCT.PHYSICAL_PARTS_TREE_HST
    (
        HISTORY_RECORD_ID,
        RELATIONSHIP_ID,
        PART_PARENT_ID,
        PART_ID,
        IS_RECORD_DELETED,
        HISTORY_INSERTION_TIME,
        HISTORY_INSERTION_USER,
        RECORD_LASTUPDATE_TIME,
        RECORD_LASTUPDATE_USER,
        DDD_PART_NAME
    )
    values
    (
          seqPkId ,
        :OLD.RELATIONSHIP_ID,
        :OLD.PART_PARENT_ID,
        :OLD.PART_ID,
        :OLD.IS_RECORD_DELETED,
        :new.RECORD_LASTUPDATE_TIME ,
        :new.RECORD_LASTUPDATE_USER,
        recordChangeTime,
        recordChangeUser,
        :OLD.DDD_PART_NAME
    );



   EXCEPTION
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
END ;
/
SHOW ERROR






PROMPT Creating Trigger 'TR_UPD_MANUFACTURERS'
CREATE OR REPLACE TRIGGER TR_UPD_MANUFACTURERS
 BEFORE UPDATE
 ON MANUFACTURERS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
DECLARE



    seqPkId number(38,0);
    recordChangeTime TIMESTAMP(6) WITH TIME ZONE;
    recordChangeUser VARCHAR2(50 );
BEGIN
      SELECT ANY_MNFCTR_HISTORY_ID_SEQ.NEXTVAL INTO seqPkId FROM dual;

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
   :new.MANUFACTURER_ID :=  :OLD.MANUFACTURER_ID;

   -- update last change record info, if needed
       :new.RECORD_LASTUPDATE_TIME  := SYSDATE;

   if :new.RECORD_LASTUPDATE_USER is null then
       :new.RECORD_LASTUPDATE_USER := USER;
   end if;

    insert into CMS_&det._CORE_CONSTRUCT.MANUFACTURERS_HST
    (
        HISTORY_RECORD_ID,
        MANUFACTURER_ID,
        IS_RECORD_DELETED,
        MANUFACTURER_NAME,
        HISTORY_INSERTION_TIME,
        HISTORY_INSERTION_USER,
        RECORD_LASTUPDATE_TIME,
        RECORD_LASTUPDATE_USER,
        COMMENT_DESCRIPTION
    )
    values
    (
            seqPkId,
        :OLD.MANUFACTURER_ID,
        :OLD.IS_RECORD_DELETED,		    
        :OLD.MANUFACTURER_NAME,
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


PROMPT Creating Trigger 'TR_INS_MANUFACTURERS'
CREATE OR REPLACE TRIGGER TR_INS_MANUFACTURERS
 BEFORE INSERT
 ON MANUFACTURERS
 REFERENCING OLD AS OLD NEW AS NEW
 FOR EACH ROW
BEGIN

   if :new.MANUFACTURER_ID is null then     
      SELECT ANY_MNFCTR_ID_SEQ.NEXTVAL INTO :new.MANUFACTURER_ID FROM dual;
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









