WHENEVER SQLERROR EXIT SQL.SQLCODE

PROMPT Setting parameters

@params

PROMPT Executing SYS stuff

@01-sys/01-tbs
@01-sys/02-rol
@01-sys/03-usr

PROMPT Executing CMS_&det._CORE_IOV_MGMNT stuff
connect CMS_&det._CORE_IOV_MGMNT/&owner_password@&&1

@07-core_iov_mgmnt_cfg/01-tab
@07-core_iov_mgmnt_cfg/02-ind
@07-core_iov_mgmnt_cfg/03-con
@07-core_iov_mgmnt_cfg/04-seq
@07-core_iov_mgmnt_cfg/05-trg
@07-core_iov_mgmnt_cfg/06-gra


quit
