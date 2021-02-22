WHENEVER SQLERROR EXIT SQL.SQLCODE

@params

PROMPT ----------------------------------------------------------

PROMPT Cleaning db records for tests

PROMPT connecting to CMS_&det._&subdet._COND
connect CMS_&det._&subdet._COND/&owner_password@&&1

PROMPT Drop CMS_&det._&subdet._COND.TEST_COORDINATES table.
drop table CMS_&det._&subdet._COND.TEST_COORDINATES
/

PROMPT Drop CMS_&det._&subdet._COND.TEST_CHANNELS table.
drop table CMS_&det._&subdet._COND.TEST_CHANNELS
/
PROMPT Drop CMS_&det._&subdet._COND.TEST_FILES table.
drop table CMS_&det._&subdet._COND.TEST_FILES
/
PROMPT Drop CMS_&det._&subdet._COND.TEST_IV table.
drop table CMS_&det._&subdet._COND.TEST_IV
/

PROMPT connecting to CMS_&det._CORE_IOV_MGMNT
connect CMS_&det._CORE_IOV_MGMNT/&owner_password@&&1

PROMPT Deleting CMS_&det._CORE_IOV_MGMNT.COND_DATASET2IOV_MAPS table.
delete from CMS_&det._CORE_IOV_MGMNT.COND_DATASET2IOV_MAPS
/
PROMPT Deleting CMS_&det._CORE_IOV_MGMNT.COND_IOV2TAG_MAPS table.
delete from CMS_&det._CORE_IOV_MGMNT.COND_IOV2TAG_MAPS
/
PROMPT Deleting CMS_&det._CORE_IOV_MGMNT.COND_TAGS table.
delete from CMS_&det._CORE_IOV_MGMNT.COND_TAGS
/
PROMPT Deleting CMS_&det._CORE_IOV_MGMNT.COND_IOVS table.
delete from CMS_&det._CORE_IOV_MGMNT.COND_IOVS
/

PROMPT connecting to CMS_&det._CORE_COND
connect CMS_&det._CORE_COND/&owner_password@&&1


PROMPT Deleting CMS_&det._CORE_COND.COND_ATTR_LISTS table.
delete from CMS_&det._CORE_COND.COND_ATTR_LISTS
/
PROMPT Deleting CMS_&det._CORE_COND.COND_TO_ATTR_RLTNSHPS table.
delete from CMS_&det._CORE_COND.COND_TO_ATTR_RLTNSHPS
/
PROMPT Deleting CMS_&det._CORE_COND.COND_TO_PART_RLTNSHPS table.
delete from CMS_&det._CORE_COND.COND_TO_PART_RLTNSHPS
/
PROMPT Deleting CMS_&det._CORE_COND.COND_DATA_SETS
delete from CMS_&det._CORE_COND.COND_DATA_SETS
/
PROMPT Deleting CMS_&det._CORE_COND.CHANNEL_MAPS_BASE table.
delete from CMS_&det._CORE_COND.CHANNEL_MAPS_BASE
/
PROMPT Deleting CMS_&det._CORE_COND.KINDS_OF_CONDITIONS table.
delete from CMS_&det._CORE_COND.KINDS_OF_CONDITIONS
/
PROMPT Deleting CMS_&det._CORE_COND.COND_RUNS table.
delete from CMS_&det._CORE_COND.COND_RUNS
/

PROMPT connecting to CMS_&det._CORE_CONSTRUCT
connect CMS_&det._CORE_CONSTRUCT/&owner_password@&&1

PROMPT Deleting CMS_&det._CORE_CONSTRUCT.PHYSICAL_PARTS_TREE table.
delete from CMS_&det._CORE_CONSTRUCT.PHYSICAL_PARTS_TREE
/
PROMPT Deleting CMS_&det._CORE_CONSTRUCT.PART_ATTR_LISTS table.
delete from CMS_&det._CORE_CONSTRUCT.PART_ATTR_LISTS
/
PROMPT Deleting CMS_&det._CORE_CONSTRUCT.PARTS table.
delete from CMS_&det._CORE_CONSTRUCT.PARTS
/
PROMPT Deleting CMS_&det._CORE_CONSTRUCT.PART_TO_ATTR_RLTNSHPS table.
delete from CMS_&det._CORE_CONSTRUCT.PART_TO_ATTR_RLTNSHPS
/
PROMPT Deleting CMS_&det._CORE_CONSTRUCT.PART_TO_PART_RLTNSHPS table.
delete from CMS_&det._CORE_CONSTRUCT.PART_TO_PART_RLTNSHPS
/
PROMPT Deleting CMS_&det._CORE_CONSTRUCT.KINDS_OF_PARTS table.
delete from CMS_&det._CORE_CONSTRUCT.KINDS_OF_PARTS
/
PROMPT Deleting CMS_&det._CORE_CONSTRUCT.SUBDETECTORS table.
delete from CMS_&det._CORE_CONSTRUCT.SUBDETECTORS
/
PROMPT Deleting CMS_&det._CORE_CONSTRUCT.MANUFACTURERS table.
delete from CMS_&det._CORE_CONSTRUCT.MANUFACTURERS
/

PROMPT connecting to CMS_&det._CORE_ATTRIBUTE
connect CMS_&det._CORE_ATTRIBUTE/&owner_password@&&1

PROMPT Deleting CMS_&det._CORE_ATTRIBUTE.POSITION_SCHEMAS table.
delete from CMS_&det._CORE_ATTRIBUTE.POSITION_SCHEMAS
/
PROMPT Deleting CMS_&det._CORE_ATTRIBUTE.COND_ALGORITHMS table.
delete from CMS_&det._CORE_ATTRIBUTE.COND_ALGORITHMS
/
PROMPT Deleting CMS_&det._CORE_ATTRIBUTE.ATTR_BASES table.
delete from CMS_&det._CORE_ATTRIBUTE.ATTR_BASES
/
PROMPT Deleting CMS_&det._CORE_ATTRIBUTE.ATTR_CATALOGS table.
delete from CMS_&det._CORE_ATTRIBUTE.ATTR_CATALOGS
/



PROMPT connecting to CMS_&det._CORE_MANAGEMNT
connect CMS_&det._CORE_MANAGEMNT/&owner_password@&&1

PROMPT Deleting CMS_&det._CORE_MANAGEMNT.CONDITIONS_DATA_AUDITLOG table.
delete from CMS_&det._CORE_MANAGEMNT.CONDITIONS_DATA_AUDITLOG
/
PROMPT Deleting CMS_&det._CORE_MANAGEMNT.LOCATIONS table.
delete from CMS_&det._CORE_MANAGEMNT.LOCATIONS
/
PROMPT Deleting CMS_&det._CORE_MANAGEMNT.INSTITUTIONS table.
delete from CMS_&det._CORE_MANAGEMNT.INSTITUTIONS
/

PROMPT connecting to CMS_&det._&subdet._CONSTRUCT
connect CMS_&det._&subdet._CONSTRUCT/&owner_password@&&1

delete from SHIPMENT_ITEMS
/

delete from SHIPMENTS
/

delete from REQUEST_ITEMS
/

delete from REQUESTS
/

delete from PART_DETAILS
/

delete from PART_DETAILS_HST

@create.sql

quit