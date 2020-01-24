WHENEVER SQLERROR EXIT SQL.SQLCODE
SET SQLBLANKLINES ON

PROMPT Setting parameters

@params

PROMPT Executing CMS_&det._&subdet._COND stuff
connect CMS_&det._&subdet._COND/&owner_password@&&1

@10-hybrids_tests/hybrids_testing

quit