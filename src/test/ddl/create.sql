WHENEVER SQLERROR EXIT SQL.SQLCODE

@params

PROMPT Executing CMS_&det._CORE_ATTRIBUTE stuff
connect CMS_&det._CORE_ATTRIBUTE/&owner_password@&&1

@01-core_attribute

PROMPT Executing CMS_&det._CORE_CONSTRUCT stuff
connect CMS_&det._CORE_CONSTRUCT/&owner_password@&&1

@02-core_construct

PROMPT Executing CMS_&det._CORE_COND stuff
connect CMS_&det._CORE_COND/&owner_password@&&1

GRANT SELECT ON CMS_&det._CORE_COND.ANY_COND_RECORD_ID_SEQ TO CMS_&det._&subdet._COND;

PROMPT Executing CMS_&det._&subdet._COND stuff
connect CMS_&det._&subdet._COND/&owner_password@&&1

@03-test_cond

PROMPT Executing CMS_&det._CORE_COND stuff
connect CMS_&det._CORE_COND/&owner_password@&&1

@04-core_cond

PROMPT Executing CMS_&det._CORE_IOV_MGMNT stuff
connect CMS_&det._CORE_IOV_MGMNT/&owner_password@&&1

@05-core-iov-mgmnt

PROMPT Executing CMS_&det._&subdet._CONSTRUCT stuff
connect CMS_&det._&subdet._CONSTRUCT/&owner_password@&&1

@06-part_extension

@09-assembly_process

PROMPT Executing CMS_&det._&subdet._COND stuff
connect CMS_&det._&subdet._COND/&owner_password@&&1

@07-hybrids_testing

quit