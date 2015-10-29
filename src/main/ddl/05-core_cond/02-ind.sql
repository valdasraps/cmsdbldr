
PROMPT Creating Index 'KOCHT_KNDCND_FK_I'
CREATE INDEX KOCHT_KNDCND_FK_I ON KIND_OF_CONDITIONS_HST
 (KIND_OF_CONDITION_ID)
 PCTFREE 10
 STORAGE
 (
   INITIAL 1M
   NEXT 1M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Index 'CNDTST_CNDTST_FK_I'
CREATE INDEX CNDTST_CNDTST_FK_I ON COND_DATA_SETS
 (AGGREGATED_COND_DATA_SET_ID)
 PCTFREE 10
 STORAGE
 (
   INITIAL 1M
   NEXT 1M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Index 'CNDTST_PRTTREE_FK_I'
CREATE INDEX CNDTST_PRTTREE_FK_I ON COND_DATA_SETS
 (PART_ID)
 PCTFREE 10
 STORAGE
 (
   INITIAL 1M
   NEXT 1M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Index 'CNDTST_CNDRUN_FK_I'
CREATE INDEX CNDTST_CNDRUN_FK_I ON COND_DATA_SETS
 (COND_RUN_ID)
 PCTFREE 10
 STORAGE
 (
   INITIAL 1M
   NEXT 1M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Index 'CNDTST_CHMPBS_FK_I'
CREATE INDEX CNDTST_CHMPBS_FK_I ON COND_DATA_SETS
 (CHANNEL_MAP_ID)
 PCTFREE 10
 STORAGE
 (
   INITIAL 1M
   NEXT 1M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Index 'CNDTST_KNDCND_FK_I'
CREATE INDEX CNDTST_KNDCND_FK_I ON COND_DATA_SETS
 (KIND_OF_CONDITION_ID)
 PCTFREE 10
 STORAGE
 (
   INITIAL 1M
   NEXT 1M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Index 'CARHST_CNATRL_FK_I'
CREATE INDEX CARHST_CNATRL_FK_I ON COND_TO_ATTR_RLTNSHPS_HST
 (RELATIONSHIP_ID)
 PCTFREE 10
 STORAGE
 (
   INITIAL 1M
   NEXT 1M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Index 'CNPTREL_KNDPRT_FK_I'
CREATE INDEX CNPTREL_KNDPRT_FK_I ON COND_TO_PART_RLTNSHPS
 (KIND_OF_PART_ID)
 PCTFREE 10
 STORAGE
 (
   INITIAL 1M
   NEXT 1M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Index 'CNPTREL_KNDCND_FK_I'
CREATE INDEX CNPTREL_KNDCND_FK_I ON COND_TO_PART_RLTNSHPS
 (KIND_OF_CONDITION_ID)
 PCTFREE 10
 STORAGE
 (
   INITIAL 1M
   NEXT 1M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Index 'CNATRL_KNDCND_FK_I'
CREATE INDEX CNATRL_KNDCND_FK_I ON COND_TO_ATTR_RLTNSHPS
 (KIND_OF_CONDITION_ID)
 PCTFREE 10
 STORAGE
 (
   INITIAL 1M
   NEXT 1M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Index 'CNATRL_ATRCTG_FK_I'
CREATE INDEX CNATRL_ATRCTG_FK_I ON COND_TO_ATTR_RLTNSHPS
 (ATTR_CATALOG_ID)
 PCTFREE 10
 STORAGE
 (
   INITIAL 1M
   NEXT 1M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Index 'CNATLS_ATRBSE_FK_I'
CREATE INDEX CNATLS_ATRBSE_FK_I ON COND_ATTR_LISTS
 (ATTRIBUTE_ID)
 PCTFREE 10
 STORAGE
 (
   INITIAL 1M
   NEXT 1M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Index 'CNATLS_CNATRL_FK_I'
CREATE INDEX CNATLS_CNATRL_FK_I ON COND_ATTR_LISTS
 (RELATIONSHIP_ID)
 PCTFREE 10
 STORAGE
 (
   INITIAL 1M
   NEXT 1M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Index 'CNATLS_CNDTST_FK_I'
CREATE INDEX CNATLS_CNDTST_FK_I ON COND_ATTR_LISTS
 (CONDITION_DATA_SET_ID)
 PCTFREE 10
 STORAGE
 (
   INITIAL 1M
   NEXT 1M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Index 'CPTHST_CNPTREL_FK_I'
CREATE INDEX CPTHST_CNPTREL_FK_I ON COND_TO_PART_RLTNSHPS_HST
 (RELATIONSHIP_ID)
 PCTFREE 10
 STORAGE
 (
   INITIAL 1M
   NEXT 1M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

PROMPT Creating Index 'CNRNHT_CNDRUN_FK_I'
CREATE INDEX CNRNHT_CNDRUN_FK_I ON COND_RUNS_HST
 (COND_RUN_ID)
 PCTFREE 10
 STORAGE
 (
   INITIAL 1M
   NEXT 1M
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

CREATE INDEX CONDDATASET_TNAME_I ON COND_DATA_SETS
 (EXTENSION_TABLE_NAME)
 PCTFREE 10
 STORAGE
 (
   INITIAL 256K
   NEXT 256K
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

CREATE INDEX CONDATTRREL_NAME_I ON COND_TO_ATTR_RLTNSHPS
 (DISPLAY_NAME)
 PCTFREE 10
 STORAGE
 (
   INITIAL 256K
   NEXT 256K
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

CREATE INDEX KINDOFKOND_NAME_I ON KINDS_OF_CONDITIONS
 (NAME)
 PCTFREE 10
 STORAGE
 (
   INITIAL 256K
   NEXT 256K
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

CREATE INDEX KINDOFKOND_TNAME_I ON KINDS_OF_CONDITIONS
 (EXTENSION_TABLE_NAME)
 PCTFREE 10
 STORAGE
 (
   INITIAL 256K
   NEXT 256K
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

CREATE INDEX CHANBASE_TNAME_I ON CHANNEL_MAPS_BASE
 (EXTENSION_TABLE_NAME)
 PCTFREE 10
 STORAGE
 (
   INITIAL 256K
   NEXT 256K
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

CREATE INDEX CONDRUN_NAME_I ON COND_RUNS
 (RUN_NAME)
 PCTFREE 10
 STORAGE
 (
   INITIAL 256K
   NEXT 256K
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/

CREATE INDEX CONDRUN_BTIME_I ON COND_RUNS
 (RUN_BEGIN_TIMESTAMP)
 PCTFREE 10
 STORAGE
 (
   INITIAL 256K
   NEXT 256K
   PCTINCREASE 0
   MINEXTENTS 1
   MAXEXTENTS UNLIMITED
 ) TABLESPACE CMS_&det._PROCERNIT_DATA
/