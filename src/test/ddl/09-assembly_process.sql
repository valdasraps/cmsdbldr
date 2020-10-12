delete from ASSEMBLY_PART_DEFINITIONS;
delete from ASSEMBLY_STEP_DEFINITIONS;
delete from ASSEMBLY_PROCESSES;

COLUMN l_product_kop_id NEW_VALUE l_product_kop_id
select KIND_OF_PART_ID as l_product_kop_id from CMS_TST_CORE_CONSTRUCT.kinds_of_parts where DISPLAY_NAME = 'TEST Tower';

COLUMN l_jig_kop_id NEW_VALUE l_jig_kop_id
select KIND_OF_PART_ID as l_jig_kop_id from CMS_TST_CORE_CONSTRUCT.kinds_of_parts where DISPLAY_NAME = 'TEST Jig';

COLUMN l_iv_koc_id NEW_VALUE l_iv_koc_id
select KIND_OF_CONDITION_ID as l_iv_koc_id from CMS_TST_CORE_COND.kinds_of_conditions where NAME = 'IV';

insert into ASSEMBLY_PROCESSES (APR_ID,APR_NAME,APR_DESCR,APR_PRODUCT_KOP_ID)
values (1, 'TEST Tower assembly','TEST Tower assembly', &l_product_kop_id);

insert into ASSEMBLY_STEP_DEFINITIONS (ASD_ID,ASD_NUMBER,ASD_APR_ID,ASD_NAME,ASD_DESCR)
values (1,1,1,'First step','First step description');

insert into ASSEMBLY_STEP_DEFINITIONS (ASD_ID,ASD_NUMBER,ASD_APR_ID,ASD_NAME,ASD_DESCR)
values (2,2,1,'Second step','Second step description');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (1,1,1,&l_product_kop_id,'PRODUCT','N','Tower product','Tower product description');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (2,2,1,&l_jig_kop_id,'JIG','N','Jig','Jig description');

insert into ASSEMBLY_DATA_DEFINITIONS (ADD_ID,ADD_NUMBER,ADD_KOC_ID,ADD_APD_ID,ADD_NAME,ADD_DESCR)
values (1,1,&l_iv_koc_id,1,'Tower IV measurements','IV measurement description');

