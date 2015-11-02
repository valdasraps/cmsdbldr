
PROMPT Granting Object Privileges
GRANT SELECT ON CNDIOV_ID_SEQ TO CMS_&det._CORE_IOV_MGMNT_WRITER
/

GRANT SELECT ON CNDIOV_ID_SEQ TO CMS_&det._CORE_IOV_MGMNT_ADMIN
/

GRANT SELECT ON CNDTAG_ID_SEQ TO CMS_&det._CORE_IOV_MGMNT_WRITER
/

GRANT SELECT ON CNDTAG_ID_SEQ TO CMS_&det._CORE_IOV_MGMNT_ADMIN
/

GRANT INSERT, UPDATE ON COND_TAGS TO CMS_&det._CORE_IOV_MGMNT_WRITER
/

GRANT SELECT ON COND_TAGS TO CMS_&det._CORE_IOV_MGMNT_READER
/

GRANT DELETE ON COND_TAGS TO CMS_&det._CORE_IOV_MGMNT_ADMIN
/

GRANT DELETE ON COND_IOVS TO CMS_&det._CORE_IOV_MGMNT_ADMIN
/

GRANT SELECT ON COND_IOVS TO CMS_&det._CORE_IOV_MGMNT_READER
/

GRANT INSERT, UPDATE ON COND_IOVS TO CMS_&det._CORE_IOV_MGMNT_WRITER
/

GRANT DELETE ON COND_DATASET2IOV_MAPS TO CMS_&det._CORE_IOV_MGMNT_ADMIN
/

GRANT SELECT ON COND_DATASET2IOV_MAPS TO CMS_&det._CORE_IOV_MGMNT_READER
/

GRANT INSERT, UPDATE ON COND_DATASET2IOV_MAPS TO CMS_&det._CORE_IOV_MGMNT_WRITER
/

GRANT INSERT, UPDATE ON COND_IOV2TAG_MAPS TO CMS_&det._CORE_IOV_MGMNT_WRITER
/

GRANT SELECT ON COND_IOV2TAG_MAPS TO CMS_&det._CORE_IOV_MGMNT_READER
/

GRANT DELETE ON COND_IOV2TAG_MAPS TO CMS_&det._CORE_IOV_MGMNT_ADMIN
/

