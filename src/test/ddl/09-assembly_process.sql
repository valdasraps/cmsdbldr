delete from ASSEMBLY_DATA_DEFINITIONS;
delete from ASSEMBLY_ATTRIBUTE_DEFINITIONS;
delete from ASSEMBLY_PART_DEFINITIONS;
delete from ASSEMBLY_STEP_DEFINITIONS;
delete from ASSEMBLY_PROCESSES;

COLUMN l_sensor_kop_id NEW_VALUE l_sensor_kop_id
select KIND_OF_PART_ID as l_sensor_kop_id from CMS_&det._CORE_CONSTRUCT.kinds_of_parts where DISPLAY_NAME = '2S Sensor';

COLUMN l_module_kop_id NEW_VALUE l_module_kop_id
select KIND_OF_PART_ID as l_module_kop_id from CMS_&det._CORE_CONSTRUCT.kinds_of_parts where DISPLAY_NAME = '2S Module';

COLUMN l_hybrid_kop_id NEW_VALUE l_hybrid_kop_id
select KIND_OF_PART_ID as l_hybrid_kop_id from CMS_&det._CORE_CONSTRUCT.kinds_of_parts where DISPLAY_NAME = '8CBC3 Front-end Hybrid';

COLUMN l_jig_kop_id NEW_VALUE l_jig_kop_id
select KIND_OF_PART_ID as l_jig_kop_id from CMS_&det._CORE_CONSTRUCT.kinds_of_parts where DISPLAY_NAME = 'Assembly Jig';

COLUMN l_iv_koc_id NEW_VALUE l_iv_koc_id
select KIND_OF_CONDITION_ID as l_iv_koc_id from CMS_&det._CORE_COND.kinds_of_conditions where NAME = 'IV';

COLUMN l_prep_attr_id NEW_VALUE l_prep_attr_id
select ATTRIBUTE_ID as l_prep_attr_id from cms_&det._core_attribute.POSITION_SCHEMAS where NAME = 'Prepared for assembly';

COLUMN l_top_attr_id NEW_VALUE l_top_attr_id
select ATTRIBUTE_ID as l_top_attr_id from cms_&det._core_attribute.POSITION_SCHEMAS where NAME = 'TOP';

COLUMN l_bottom_attr_id NEW_VALUE l_bottom_attr_id
select ATTRIBUTE_ID as l_bottom_attr_id from cms_&det._core_attribute.POSITION_SCHEMAS where NAME = 'BOTTOM';

-- Processes

insert into ASSEMBLY_PROCESSES (APR_ID,APR_NAME,APR_DESCR,APR_PRODUCT_KOP_ID)
values (1, '2S Sensor preparation','2S Sensor preparation for 2S Module assembly process', &l_sensor_kop_id);

insert into ASSEMBLY_PROCESSES (APR_ID,APR_NAME,APR_DESCR,APR_PRODUCT_KOP_ID)
values (2, '2S Module assembly','2S Module assembly process', &l_module_kop_id);

-- Steps

insert into ASSEMBLY_STEP_DEFINITIONS (ASD_ID,ASD_NUMBER,ASD_APR_ID,ASD_NAME,ASD_DESCR)
values (1,1,1,'Kapton Isolator Assembly','Glue the kapton isolator on the sensor surface');

insert into ASSEMBLY_STEP_DEFINITIONS (ASD_ID,ASD_NUMBER,ASD_APR_ID,ASD_NAME,ASD_DESCR)
values (2,2,1,'HV Tail Assembly','Glue HV tail connector to the isolated surface');

insert into ASSEMBLY_STEP_DEFINITIONS (ASD_ID,ASD_NUMBER,ASD_APR_ID,ASD_NAME,ASD_DESCR)
values (3,3,1,'HV Tail Wire Bonding','Wire bound the tail connector to the power connection pad on the sensor');

insert into ASSEMBLY_STEP_DEFINITIONS (ASD_ID,ASD_NUMBER,ASD_APR_ID,ASD_NAME,ASD_DESCR)
values (4,4,1,'Encapsulation','Encapsulation of the hV tail wire bounds');

insert into ASSEMBLY_STEP_DEFINITIONS (ASD_ID,ASD_NUMBER,ASD_APR_ID,ASD_NAME,ASD_DESCR)
values (5,1,2,'Sensor Sandwich Assembly','Sandwich production with the two top and bottom sensors glued on the bridge');

insert into ASSEMBLY_STEP_DEFINITIONS (ASD_ID,ASD_NUMBER,ASD_APR_ID,ASD_NAME,ASD_DESCR)
values (6,2,2,'Hybrid Assembly','Assembly of the FEH and SEH on the sensor sandwich');

insert into ASSEMBLY_STEP_DEFINITIONS (ASD_ID,ASD_NUMBER,ASD_APR_ID,ASD_NAME,ASD_DESCR)
values (7,3,2,'Wire Bonding','Wiring between hybrids and sensors');

insert into ASSEMBLY_STEP_DEFINITIONS (ASD_ID,ASD_NUMBER,ASD_APR_ID,ASD_NAME,ASD_DESCR)
values (8,4,2,'Top Encapsulation','Encapsulation of the wire bounds on the top sensor');

insert into ASSEMBLY_STEP_DEFINITIONS (ASD_ID,ASD_NUMBER,ASD_APR_ID,ASD_NAME,ASD_DESCR)
values (9,5,2,'Bottom Encapsulation','Encapsulation of the wire bounds on the bottom sensor');

-- Parts

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (1,1,1,&l_sensor_kop_id,'PRODUCT','N','2S Sensor','2S Sensor to be glued to the Kapton isolator');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (2,2,1,&l_jig_kop_id,'JIG','N','Assembly jig','Jig for gluing the kapton isolator on the sensor');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (3,1,2,&l_sensor_kop_id,'PRODUCT','N','2S Sensor','2S Sensor on which the HV tail is to be glued');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (4,2,2,&l_jig_kop_id,'JIG','N','Assembly jig','Jig for gluing the HV tail');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (5,1,3,&l_sensor_kop_id,'PRODUCT','N','2S Sensor','2S Sensor to be wire bounded to the HV tail');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (6,2,3,&l_jig_kop_id,'JIG','N','Assembly jig','Jig for wire bounding the HV tail');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (7,1,4,&l_sensor_kop_id,'PRODUCT','N','2S Sensor','2S Sensor on which the wire bounds with the HV tail have to be encapsulated');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (8,2,4,&l_jig_kop_id,'JIG','N','Assembly jig','Jig for encapsulation');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (9,1,5,&l_module_kop_id,'PRODUCT','Y','2S Module Assembled','The 2S module produced at the end of the assembly process');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (10,2,5,&l_sensor_kop_id,'COMPONENT','Y','2S Sensor Top','2S sensor with HV tail which is being assembled as TOP sensor in the sensor sandwich');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (11,3,5,&l_sensor_kop_id,'COMPONENT','Y','2S Sensor Bottom','2S sensor with HV tail which is being assembled as BOTTOM sensor in the sensor sandwich');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (12,4,5,&l_jig_kop_id,'JIG','N','Assembly jig','Jig for the sensor sandwich');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (13,1,6,&l_module_kop_id,'PRODUCT','N','2S Module Assembled','The 2S module produced at the end of the assembly process');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (14,2,6,&l_sensor_kop_id,'COMPONENT','N','2S Sensor Top','2S sensor with HV tail which is assembled as TOP sensor in the sensor sandwich');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (15,3,6,&l_sensor_kop_id,'COMPONENT','N','2S Sensor Bottom','2S sensor with HV tail which is assembled as BOTTOM sensor in the sensor sandwich');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (16,4,6,&l_hybrid_kop_id,'COMPONENT','Y','Front-end Hybrid','Fornt-end hybrid which is being assembled to the sensor sandwich');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (17,5,6,&l_jig_kop_id,'JIG','N','Assembly jig','Jig for the Hybrid asssembly');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (18,1,7,&l_module_kop_id,'PRODUCT','N','2S Module Assembled','The 2S module produced at the end of the assembly process');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (19,2,7,&l_sensor_kop_id,'COMPONENT','N','2S Sensor Top','2S sensor with HV tail which is assembled as TOP sensor in the sensor sandwich');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (20,3,7,&l_sensor_kop_id,'COMPONENT','N','2S Sensor Bottom','2S sensor with HV tail which is assembled as BOTTOM sensor in the sensor sandwich');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (21,4,7,&l_jig_kop_id,'JIG','N','Assembly jig','Jig for the Hybrid asssembly');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (22,1,8,&l_module_kop_id,'PRODUCT','N','2S Module Assembled','The 2S module produced at the end of the assembly process');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (23,2,8,&l_sensor_kop_id,'COMPONENT','N','2S Sensor Top','2S sensor with HV tail which is assembled as TOP sensor in the sensor sandwich');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (24,3,8,&l_jig_kop_id,'JIG','N','Assembly jig','Jig for the Hybrid asssembly');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (25,1,9,&l_module_kop_id,'PRODUCT','N','2S Module Assembled','The 2S module produced at the end of the assembly process');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (26,2,9,&l_sensor_kop_id,'COMPONENT','N','2S Sensor Bottom','2S sensor with HV tail which is assembled as BOTTOM sensor in the sensor sandwich');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_IS_NEW,APD_NAME,APD_DESCR)
values (27,3,9,&l_jig_kop_id,'JIG','N','Assembly jig','Jig for the Hybrid asssembly');

-- Data

insert into ASSEMBLY_DATA_DEFINITIONS (ADD_ID,ADD_NUMBER,ADD_KOC_ID,ADD_APD_ID,ADD_NAME,ADD_DESCR)
values (1,1,&l_iv_koc_id,1,'IV quality check','IV quality check after the kapton insulator gluing');

insert into ASSEMBLY_DATA_DEFINITIONS (ADD_ID,ADD_NUMBER,ADD_KOC_ID,ADD_APD_ID,ADD_NAME,ADD_DESCR)
values (2,1,&l_iv_koc_id,3,'IV quality check','IV quality check after the HV tail gluing');

insert into ASSEMBLY_DATA_DEFINITIONS (ADD_ID,ADD_NUMBER,ADD_KOC_ID,ADD_APD_ID,ADD_NAME,ADD_DESCR)
values (3,1,&l_iv_koc_id,5,'IV quality check','IV quality check after the HV tail wire bounding');

insert into ASSEMBLY_DATA_DEFINITIONS (ADD_ID,ADD_NUMBER,ADD_KOC_ID,ADD_APD_ID,ADD_NAME,ADD_DESCR)
values (4,1,&l_iv_koc_id,7,'IV quality check','IV quality check after the encapsulation');

insert into ASSEMBLY_DATA_DEFINITIONS (ADD_ID,ADD_NUMBER,ADD_KOC_ID,ADD_APD_ID,ADD_NAME,ADD_DESCR)
values (5,1,&l_iv_koc_id,11,'IV Sensor Bottom','IV quality check after sensor sandwich assembly');

insert into ASSEMBLY_DATA_DEFINITIONS (ADD_ID,ADD_NUMBER,ADD_KOC_ID,ADD_APD_ID,ADD_NAME,ADD_DESCR)
values (6,1,&l_iv_koc_id,14,'IV Sensor Top','IV quality check after hybrid assembly');

insert into ASSEMBLY_DATA_DEFINITIONS (ADD_ID,ADD_NUMBER,ADD_KOC_ID,ADD_APD_ID,ADD_NAME,ADD_DESCR)
values (7,1,&l_iv_koc_id,15,'IV Sensor Bottom','IV quality check after hybrid assembly');

insert into ASSEMBLY_DATA_DEFINITIONS (ADD_ID,ADD_NUMBER,ADD_KOC_ID,ADD_APD_ID,ADD_NAME,ADD_DESCR)
values (8,1,&l_iv_koc_id,19,'IV Sensor Top','IV quality check after wire bonding');

insert into ASSEMBLY_DATA_DEFINITIONS (ADD_ID,ADD_NUMBER,ADD_KOC_ID,ADD_APD_ID,ADD_NAME,ADD_DESCR)
values (9,1,&l_iv_koc_id,20,'IV Sensor Bottom','IV quality check after wire bonding');

insert into ASSEMBLY_DATA_DEFINITIONS (ADD_ID,ADD_NUMBER,ADD_KOC_ID,ADD_APD_ID,ADD_NAME,ADD_DESCR)
values (10,1,&l_iv_koc_id,23,'IV Sensor Top','IV quality check after top encapsulation');

insert into ASSEMBLY_DATA_DEFINITIONS (ADD_ID,ADD_NUMBER,ADD_KOC_ID,ADD_APD_ID,ADD_NAME,ADD_DESCR)
values (11,1,&l_iv_koc_id,26,'IV Sensor Bottom','IV quality check after bottom encapsulation');


-- Attributes

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_ID,AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_IS_SELECTABLE,AAD_STEP_STATUS)
values (1,7,&l_prep_attr_id,'N','COMPLETED');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_ID,AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_IS_SELECTABLE)
values (2,10,&l_prep_attr_id,'Y');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_ID,AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_IS_SELECTABLE,AAD_STEP_STATUS)
values (3,10,&l_top_attr_id,'N','COMPLETED');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_ID,AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_IS_SELECTABLE)
values (4,11,&l_prep_attr_id,'Y');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_ID,AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_IS_SELECTABLE)
values (5,11,&l_bottom_attr_id,'N');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_ID,AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_IS_SELECTABLE)
values (6,14,&l_prep_attr_id,'Y');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_ID,AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_IS_SELECTABLE)
values (7,14,&l_top_attr_id,'Y');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_ID,AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_IS_SELECTABLE)
values (8,15,&l_prep_attr_id,'Y');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_ID,AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_IS_SELECTABLE)
values (9,15,&l_bottom_attr_id,'Y');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_ID,AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_IS_SELECTABLE)
values (10,19,&l_prep_attr_id,'Y');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_ID,AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_IS_SELECTABLE)
values (11,19,&l_top_attr_id,'Y');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_ID,AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_IS_SELECTABLE)
values (12,20,&l_prep_attr_id,'Y');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_ID,AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_IS_SELECTABLE)
values (13,20,&l_bottom_attr_id,'Y');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_ID,AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_IS_SELECTABLE)
values (14,23,&l_prep_attr_id,'Y');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_ID,AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_IS_SELECTABLE)
values (15,23,&l_top_attr_id,'Y');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_ID,AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_IS_SELECTABLE)
values (16,26,&l_prep_attr_id,'Y');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_ID,AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_IS_SELECTABLE)
values (17,26,&l_bottom_attr_id,'Y');