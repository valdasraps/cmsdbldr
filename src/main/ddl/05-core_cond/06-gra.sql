
PROMPT Granting Object Privileges
GRANT SELECT ON COND_RUN_ID_SEQ TO CMS_&det._CORE_CONDITION_WRITER
/

GRANT SELECT ON COND_RUN_ID_SEQ TO CMS_&det._CORE_CONDITION_ADMIN
/

GRANT SELECT ON COND_RUN_HISTORY_ID_SEQ TO CMS_&det._CORE_CONDITION_ADMIN
/

GRANT SELECT ON COND_RUN_HISTORY_ID_SEQ TO CMS_&det._CORE_CONDITION_WRITER
/

GRANT SELECT ON COND_DATA_SET_ID_SEQ TO CMS_&det._CORE_CONDITION_ADMIN
/

GRANT SELECT ON COND_DATA_SET_ID_SEQ TO CMS_&det._CORE_CONDITION_WRITER
/

GRANT SELECT ON ANY_COND_RECORD_ID_SEQ TO CMS_&det._CORE_CONDITION_ADMIN
/

GRANT SELECT ON ANY_COND_RECORD_ID_SEQ TO CMS_&det._CORE_CONDITION_WRITER
/

GRANT SELECT ON COND_TO_PART_RLTNSHPS_HST TO CMS_&det._CORE_CONDITION_READER
/

GRANT DELETE ON COND_TO_PART_RLTNSHPS_HST TO CMS_&det._CORE_CONDITION_ADMIN
/

GRANT INSERT, UPDATE ON COND_TO_PART_RLTNSHPS_HST TO CMS_&det._CORE_CONDITION_WRITER
/

GRANT DELETE ON COND_TO_PART_RLTNSHPS TO CMS_&det._CORE_CONDITION_ADMIN
/

GRANT SELECT ON COND_TO_PART_RLTNSHPS TO CMS_&det._CORE_CONDITION_READER
/

GRANT INSERT, UPDATE ON COND_TO_PART_RLTNSHPS TO CMS_&det._CORE_CONDITION_WRITER
/

GRANT DELETE ON KIND_OF_CONDITIONS_HST TO CMS_&det._CORE_CONDITION_ADMIN
/

GRANT SELECT ON KIND_OF_CONDITIONS_HST TO CMS_&det._CORE_CONDITION_READER
/

GRANT INSERT, UPDATE ON KIND_OF_CONDITIONS_HST TO CMS_&det._CORE_CONDITION_WRITER
/

GRANT SELECT ON KINDS_OF_CONDITIONS TO CMS_&det._CORE_CONDITION_READER
/

GRANT INSERT, UPDATE ON KINDS_OF_CONDITIONS TO CMS_&det._CORE_CONDITION_WRITER
/

GRANT DELETE ON KINDS_OF_CONDITIONS TO CMS_&det._CORE_CONDITION_ADMIN
/

GRANT DELETE ON COND_TO_ATTR_RLTNSHPS_HST TO CMS_&det._CORE_CONDITION_ADMIN
/

GRANT INSERT, UPDATE ON COND_TO_ATTR_RLTNSHPS_HST TO CMS_&det._CORE_CONDITION_WRITER
/

GRANT SELECT ON COND_TO_ATTR_RLTNSHPS_HST TO CMS_&det._CORE_CONDITION_READER
/

GRANT DELETE ON COND_TO_ATTR_RLTNSHPS TO CMS_&det._CORE_CONDITION_ADMIN
/

GRANT INSERT, UPDATE ON COND_TO_ATTR_RLTNSHPS TO CMS_&det._CORE_CONDITION_WRITER
/

GRANT SELECT ON COND_TO_ATTR_RLTNSHPS TO CMS_&det._CORE_CONDITION_READER
/

GRANT DELETE ON COND_RUNS_HST TO CMS_&det._CORE_CONDITION_ADMIN
/

GRANT SELECT ON COND_RUNS_HST TO CMS_&det._CORE_CONDITION_READER
/

GRANT INSERT, UPDATE ON COND_RUNS_HST TO CMS_&det._CORE_CONDITION_WRITER
/

GRANT DELETE ON COND_RUNS TO CMS_&det._CORE_CONDITION_ADMIN
/

GRANT SELECT ON COND_RUNS TO CMS_&det._CORE_CONDITION_READER
/

GRANT INSERT, UPDATE ON COND_RUNS TO CMS_&det._CORE_CONDITION_WRITER
/

GRANT DELETE ON COND_DATA_SETS TO CMS_&det._CORE_CONDITION_ADMIN
/

GRANT REFERENCES, SELECT ON COND_DATA_SETS TO CMS_&det._&subdet._COND
/

GRANT SELECT ON COND_DATA_SETS TO CMS_&det._CORE_CONDITION_READER
/

-- GRANT REFERENCES, SELECT ON COND_DATA_SETS TO CMS_&det._HCAL_COND
/

GRANT REFERENCES, SELECT ON COND_DATA_SETS TO CMS_&det._CORE_IOV_MGMNT
/

GRANT REFERENCES, SELECT ON KINDS_OF_CONDITIONS TO CMS_&det._CORE_IOV_MGMNT
/

-- GRANT REFERENCES, SELECT ON COND_DATA_SETS TO CMS_&det._PIXEL_COND
/

GRANT INSERT, UPDATE ON COND_DATA_SETS TO CMS_&det._CORE_CONDITION_WRITER
/

GRANT DELETE ON COND_ATTR_LISTS TO CMS_&det._CORE_CONDITION_ADMIN
/

GRANT INSERT, UPDATE ON COND_ATTR_LISTS TO CMS_&det._CORE_CONDITION_WRITER
/

GRANT SELECT ON COND_ATTR_LISTS TO CMS_&det._CORE_CONDITION_READER
/

GRANT DELETE ON CHANNEL_MAPS_BASE TO CMS_&det._CORE_CONDITION_ADMIN
/

GRANT INSERT, UPDATE ON CHANNEL_MAPS_BASE TO CMS_&det._CORE_CONDITION_WRITER
/

GRANT SELECT ON CHANNEL_MAPS_BASE TO CMS_&det._CORE_CONDITION_READER
/

GRANT REFERENCES, SELECT ON CHANNEL_MAPS_BASE TO CMS_&det._&subdet._COND
/

GRANT REFERENCES, SELECT ON CHANNEL_MAPS_BASE TO CMS_&det._&subdet._COND
/


