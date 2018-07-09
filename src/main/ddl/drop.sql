@params.sql

begin
  for s in (select sid || ',' || SERIAL# as s from V$SESSION where USERNAME in (
    'CMS_&det._CORE_ATTRIBUTE',
    'CMS_&det._CORE_COND',
    'CMS_&det._CORE_CONSTRUCT',
    'CMS_&det._CORE_IOV_MGMNT',
    'CMS_&det._CORE_MANAGEMNT',
    'CMS_&det._PRTTYPE_&subdet._ADMIN',
    'CMS_&det._PRTTYPE_&subdet._READER',
    'CMS_&det._PRTTYPE_&subdet._WRITER',
    'CMS_&det._&subdet._COND',
    'CMS_&det._&subdet._CONSTRUCT',
    'CMS_&det._&subdet._VIEW')) loop
    EXECUTE IMMEDIATE ('alter system kill session ''' ||  s.s || ''' immediate');
  end loop;
end;
/

drop user CMS_&det._CORE_ATTRIBUTE cascade;
drop user CMS_&det._CORE_COND cascade;
drop user CMS_&det._CORE_CONSTRUCT cascade;
drop user CMS_&det._CORE_IOV_MGMNT cascade;
drop user CMS_&det._CORE_MANAGEMNT cascade;
drop user CMS_&det._PRTTYPE_&subdet._ADMIN cascade;
drop user CMS_&det._PRTTYPE_&subdet._READER cascade;
drop user CMS_&det._PRTTYPE_&subdet._WRITER cascade;
drop user CMS_&det._&subdet._COND cascade;
drop user CMS_&det._&subdet._CONSTRUCT cascade;
drop user CMS_&det._&subdet._VIEW cascade;

drop role CMS_&det._CORE_ATTRIBUTE_ADMIN;
drop role CMS_&det._CORE_ATTRIBUTE_READER;
drop role CMS_&det._CORE_ATTRIBUTE_WRITER;
drop role CMS_&det._CORE_CONDITION_ADMIN;
drop role CMS_&det._CORE_CONDITION_READER;
drop role CMS_&det._CORE_CONDITION_WRITER;
drop role CMS_&det._CORE_CONSTRUCT_ADMIN;
drop role CMS_&det._CORE_CONSTRUCT_READER;
drop role CMS_&det._CORE_CONSTRUCT_WRITER;
drop role CMS_&det._CORE_IOV_MGMNT_ADMIN;
drop role CMS_&det._CORE_IOV_MGMNT_READER;
drop role CMS_&det._CORE_IOV_MGMNT_WRITER;
drop role CMS_&det._CORE_MANAGEMNT_ADMIN;
drop role CMS_&det._CORE_MANAGEMNT_READER;
drop role CMS_&det._CORE_MANAGEMNT_WRITER;
drop role CMS_&det._SCHEMA_OWNER_ROLE;
drop role CMS_&det._SYS_PRIV_ROLE;
drop role CMS_&det._&subdet._APP_ADMIN;
drop role CMS_&det._&subdet._APP_READER;
drop role CMS_&det._&subdet._APP_WRITER;
drop role CMS_&det._&subdet._CONDITION_ADMIN;
drop role CMS_&det._&subdet._CONDITION_READER;
drop role CMS_&det._&subdet._CONDITION_WRITER;
drop role CMS_&det._&subdet._CONSTRUCT_ADMIN;
drop role CMS_&det._&subdet._CONSTRUCT_READER;
drop role CMS_&det._&subdet._CONSTRUCT_WRITER;
drop role CMS_&det._USER_ROLE;


DROP TABLESPACE CMS_&det._PROCERNIT_DATA INCLUDING CONTENTS AND DATAFILES;

-- in case of ORA-02429
-- SELECT INDEX_NAME,TABLE_NAME,TABLESPACE_NAME FROM DBA_INDEXES WHERE TABLESPACE_NAME= 'CMS_TST_PROCERNIT_DATA';
-- drop table ATTR_CATALOGS_HST;

quit
