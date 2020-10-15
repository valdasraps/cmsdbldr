delete from ASSEMBLY_PART_DEFINITIONS;
delete from ASSEMBLY_STEP_DEFINITIONS;
delete from ASSEMBLY_PROCESSES;

COLUMN l_product_kop_id NEW_VALUE l_product_kop_id
select KIND_OF_PART_ID as l_product_kop_id from CMS_TST_CORE_CONSTRUCT.kinds_of_parts where DISPLAY_NAME = 'TEST Tower';

COLUMN l_jig_kop_id NEW_VALUE l_jig_kop_id
select KIND_OF_PART_ID as l_jig_kop_id from CMS_TST_CORE_CONSTRUCT.kinds_of_parts where DISPLAY_NAME = 'TEST Jig';

COLUMN l_iv_koc_id NEW_VALUE l_iv_koc_id
select KIND_OF_CONDITION_ID as l_iv_koc_id from CMS_TST_CORE_COND.kinds_of_conditions where NAME = 'IV';

COLUMN l_prep_attr_id NEW_VALUE l_prep_attr_id
select ATTRIBUTE_ID as l_prep_attr_id from cms_tst_core_attribute.POSITION_SCHEMAS where NAME = 'Prepared for assembly';

-- Processes

insert into ASSEMBLY_PROCESSES (APR_ID,APR_NAME,APR_DESCR,APR_PRODUCT_KOP_ID)
values (1, 'TEST Tower assembly','TEST Tower assembly', &l_product_kop_id);

-- Steps

insert into ASSEMBLY_STEP_DEFINITIONS (ASD_ID,ASD_NUMBER,ASD_APR_ID,ASD_NAME,ASD_DESCR)
values (1,1,1,'Step 1','First step description');

insert into ASSEMBLY_STEP_DEFINITIONS (ASD_ID,ASD_NUMBER,ASD_APR_ID,ASD_NAME,ASD_DESCR)
values (2,2,1,'Step 2','Second step description');

insert into ASSEMBLY_STEP_DEFINITIONS (ASD_ID,ASD_NUMBER,ASD_APR_ID,ASD_NAME,ASD_DESCR)
values (3,3,1,'Step 3','Third step description');

insert into ASSEMBLY_STEP_DEFINITIONS (ASD_ID,ASD_NUMBER,ASD_APR_ID,ASD_NAME,ASD_DESCR)
values (4,4,1,'Step 4','Fourth step description');

-- Parts

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (1,1,1,&l_product_kop_id,'PRODUCT','N','Tower product','Tower product at step 1');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (2,2,1,&l_jig_kop_id,'JIG','N','Jig','Jig for step 1');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (3,1,2,&l_product_kop_id,'PRODUCT','N','Tower product','Tower product at step 2');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (4,2,2,&l_jig_kop_id,'JIG','N','Jig','Jig for step 2');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (5,1,3,&l_product_kop_id,'PRODUCT','N','Tower product','Tower product at step 3');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (6,2,3,&l_jig_kop_id,'JIG','N','Jig','Jig for step 3');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (7,1,4,&l_product_kop_id,'PRODUCT','N','Tower product','Tower product at step 4');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (8,2,4,&l_jig_kop_id,'JIG','N','Jig','Jig for step 4');

-- Data

insert into ASSEMBLY_DATA_DEFINITIONS (ADD_ID,ADD_NUMBER,ADD_KOC_ID,ADD_APD_ID,ADD_NAME,ADD_DESCR)
values (1,1,&l_iv_koc_id,1,'Tower IV measurements','IV measurement step 1 description');

insert into ASSEMBLY_DATA_DEFINITIONS (ADD_ID,ADD_NUMBER,ADD_KOC_ID,ADD_APD_ID,ADD_NAME,ADD_DESCR)
values (2,1,&l_iv_koc_id,3,'Tower IV measurements','IV measurement step 2 description');

insert into ASSEMBLY_DATA_DEFINITIONS (ADD_ID,ADD_NUMBER,ADD_KOC_ID,ADD_APD_ID,ADD_NAME,ADD_DESCR)
values (3,1,&l_iv_koc_id,5,'Tower IV measurements','IV measurement step 3 description');

insert into ASSEMBLY_DATA_DEFINITIONS (ADD_ID,ADD_NUMBER,ADD_KOC_ID,ADD_APD_ID,ADD_NAME,ADD_DESCR)
values (4,1,&l_iv_koc_id,7,'Tower IV measurements','IV measurement step 4 description');

-- Attributes

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_ID,AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_IS_SELECTABLE)
values (1,7,&l_prep_attr_id,'N');