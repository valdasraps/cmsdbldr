WHENEVER SQLERROR EXIT SQL.SQLCODE


PROMPT Executing CMS_TST_CORE_ATTRIBUTE stuff
connect CMS_TST_CORE_ATTRIBUTE/testing@&&1

@01-core_attribute

PROMPT Executing CMS_TST_CORE_CONSTRUCT stuff
connect CMS_TST_CORE_CONSTRUCT/testing@&&1

@02-core_construct

PROMPT Executing CMS_TST_CORE_COND stuff
connect CMS_TST_CORE_COND/testing@&&1

GRANT SELECT ON CMS_TST_CORE_COND.ANY_COND_RECORD_ID_SEQ TO CMS_TST_TEST_COND;

PROMPT Executing CMS_TST_TEST_COND stuff
connect CMS_TST_TEST_COND/testing@&&1

@03-test_cond

PROMPT Executing CMS_TST_CORE_COND stuff
connect CMS_TST_CORE_COND/testing@&&1

@04-core_cond

PROMPT Executing CMS_TST_CORE_IOV_MGMNT stuff
connect CMS_TST_CORE_IOV_MGMNT/testing@&&1

@05-core-iov-mgmnt

quit