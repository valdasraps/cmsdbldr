WHENEVER SQLERROR EXIT SQL.SQLCODE

PROMPT Setting parameters

@params

PROMPT Executing SYS stuff

@01-sys/01-tbs
@01-sys/02-rol
@01-sys/03-usr

PROMPT Executing CMS_&det._CORE_ATTRIBUTE stuff
connect CMS_&det._CORE_ATTRIBUTE/&owner_password@&&1

@02-core_attribute/01-tab
@02-core_attribute/02-ind
@02-core_attribute/03-con
@02-core_attribute/04-seq
@02-core_attribute/05-trg
@02-core_attribute/06-gra
@02-core_attribute/07-vie
@02-core_attribute/99-fin

PROMPT Executing CMS_&det._CORE_MANAGEMNT stuff
connect CMS_&det._CORE_MANAGEMNT/&owner_password@&&1

@03-core_managemnt/01-tab
@03-core_managemnt/02-ind
@03-core_managemnt/03-con
@03-core_managemnt/04-seq
@03-core_managemnt/05-trg
@03-core_managemnt/06-gra
@03-core_managemnt/07-aud

PROMPT Executing CMS_&det._CORE_CONSTRUCT stuff
connect CMS_&det._CORE_CONSTRUCT/&owner_password@&&1

@04-core_construct/01-tab
@04-core_construct/02-ind
@04-core_construct/03-con
@04-core_construct/04-seq
@04-core_construct/05-trg
@04-core_construct/06-gra

PROMPT Executing CMS_&det._CORE_COND stuff
connect CMS_&det._CORE_COND/&owner_password@&&1

@05-core_cond/01-tab
@05-core_cond/02-ind
@05-core_cond/03-con
@05-core_cond/04-seq
@05-core_cond/05-trg
@05-core_cond/06-gra

PROMPT Executing CMS_&det._CORE_IOV_MGMNT stuff
connect CMS_&det._CORE_IOV_MGMNT/&owner_password@&&1

@06-core_iov_mgmnt/01-tab
@06-core_iov_mgmnt/02-ind
@06-core_iov_mgmnt/03-con
@06-core_iov_mgmnt/04-seq
@06-core_iov_mgmnt/05-trg
@06-core_iov_mgmnt/06-gra

quit
