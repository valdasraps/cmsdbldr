
PROMPT Creating Primary Key on 'COND_TAGS'
ALTER TABLE COND_TAGS
 ADD (CONSTRAINT CNDTAG_PK PRIMARY KEY 
  (COND_TAG_ID)
 USING INDEX 
 STORAGE
 (
   INITIAL 1M
   NEXT 1M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA)
/

PROMPT Creating Primary Key on 'COND_IOV2TAG_MAPS'
ALTER TABLE COND_IOV2TAG_MAPS
 ADD (CONSTRAINT CNDIOV2TAG_PK PRIMARY KEY 
  (COND_IOV_RECORD_ID
  ,COND_TAG_ID)
 USING INDEX 
 STORAGE
 (
   INITIAL 1M
   NEXT 1M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA)
/

PROMPT Creating Primary Key on 'COND_DATASET2IOV_MAPS'
ALTER TABLE COND_DATASET2IOV_MAPS
 ADD (CONSTRAINT CNDDSIOVMP_PK PRIMARY KEY 
  (CONDITION_DATA_SET_ID
  ,COND_IOV_RECORD_ID)
 USING INDEX 
 PCTFREE 10
 STORAGE
 (
   INITIAL 1M
   NEXT 1M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA)
/

PROMPT Creating Primary Key on 'COND_IOVS'
ALTER TABLE COND_IOVS
 ADD (CONSTRAINT CNDIOV_PK PRIMARY KEY 
  (COND_IOV_RECORD_ID)
 USING INDEX 
 STORAGE
 (
   INITIAL 1M
   NEXT 1M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA)
/


 
PROMPT Creating Check Constraint on 'COND_TAGS'
ALTER TABLE COND_TAGS
 ADD (CONSTRAINT AVCON_1138998134_IS_RE_000 CHECK (IS_RECORD_DELETED BETWEEN 'F' 
AND 'F' OR IS_RECORD_DELETED BETWEEN 'T' AND 'T'))
/
   
PROMPT Creating Check Constraint on 'COND_IOVS'
ALTER TABLE COND_IOVS
 ADD (CONSTRAINT AVCON_1138998134_IS_RE_002 CHECK (IS_RECORD_DELETED BETWEEN 'F' 
AND 'F' OR IS_RECORD_DELETED BETWEEN 'T' AND 'T'))
/


PROMPT Creating Foreign Key on 'COND_TAGS'
ALTER TABLE COND_TAGS ADD (CONSTRAINT
 CNDTAG_CNDTAG_FK FOREIGN KEY 
  (COND_TAG_PARENT_ID) REFERENCES COND_TAGS
  (COND_TAG_ID))
/

PROMPT Creating Foreign Key on 'COND_IOV2TAG_MAPS'
ALTER TABLE COND_IOV2TAG_MAPS ADD (CONSTRAINT
 CNDIOV2TAG_CNDIOV_FK FOREIGN KEY 
  (COND_IOV_RECORD_ID) REFERENCES COND_IOVS
  (COND_IOV_RECORD_ID))
/

PROMPT Creating Foreign Key on 'COND_IOV2TAG_MAPS'
ALTER TABLE COND_IOV2TAG_MAPS ADD (CONSTRAINT
 CNDIOV2TAG_CNDTAG_FK FOREIGN KEY 
  (COND_TAG_ID) REFERENCES COND_TAGS
  (COND_TAG_ID))
/

PROMPT Creating Foreign Key on 'COND_DATASET2IOV_MAPS'
ALTER TABLE COND_DATASET2IOV_MAPS ADD (CONSTRAINT
 CNDDSIOVMP_CNDIOV_FK FOREIGN KEY 
  (COND_IOV_RECORD_ID) REFERENCES COND_IOVS
  (COND_IOV_RECORD_ID))
/

PROMPT Creating Foreign Key on 'COND_DATASET2IOV_MAPS'
ALTER TABLE COND_DATASET2IOV_MAPS ADD (CONSTRAINT
 CNDDSIOVMP_CNDTST_FK FOREIGN KEY 
  (CONDITION_DATA_SET_ID) REFERENCES CMS_&det._CORE_COND.COND_DATA_SETS
  (CONDITION_DATA_SET_ID))
/


