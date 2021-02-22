WHENEVER SQLERROR EXIT SQL.SQLCODE
SET SQLBLANKLINES ON

PROMPT Setting parameters

@params

PROMPT Executing CMS_&det._&subdet._CONSTRUCT stuff
connect CMS_&det._&subdet._CONSTRUCT/&owner_password@&&1

@09-assembly/assembly

quit
