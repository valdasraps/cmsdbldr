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

COLUMN l_bad_attr_id NEW_VALUE l_bad_attr_id
select ATTRIBUTE_ID as l_bad_attr_id from cms_&det._core_attribute.POSITION_SCHEMAS where NAME = 'Bad';

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

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_APD_ID,APD_NAME,APD_DESCR)
values (1,1,1,&l_sensor_kop_id,'PRODUCT',null,'2S Sensor','2S Sensor to be glued to the Kapton isolator');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_APD_ID,APD_NAME,APD_DESCR)
values (2,2,1,&l_jig_kop_id,'JIG',null,'Assembly jig','Jig for gluing the kapton isolator on the sensor');


insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_APD_ID,APD_NAME,APD_DESCR)
values (3,1,2,&l_sensor_kop_id,'PRODUCT',1,'2S Sensor','2S Sensor on which the HV tail is to be glued');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_APD_ID,APD_NAME,APD_DESCR)
values (4,2,2,&l_jig_kop_id,'JIG',null,'Assembly jig','Jig for gluing the HV tail');


insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_APD_ID,APD_NAME,APD_DESCR)
values (5,1,3,&l_sensor_kop_id,'PRODUCT',3,'2S Sensor','2S Sensor to be wire bounded to the HV tail');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_APD_ID,APD_NAME,APD_DESCR)
values (6,2,3,&l_jig_kop_id,'JIG',null,'Assembly jig','Jig for wire bounding the HV tail');


insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_APD_ID,APD_NAME,APD_DESCR)
values (7,1,4,&l_sensor_kop_id,'PRODUCT',5,'2S Sensor','2S Sensor on which the wire bounds with the HV tail have to be encapsulated');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_APD_ID,APD_NAME,APD_DESCR)
values (8,2,4,&l_jig_kop_id,'JIG',null,'Assembly jig','Jig for encapsulation');




insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_APD_ID,APD_NAME,APD_DESCR)
values (9,1,5,&l_module_kop_id,'PRODUCT',null,'2S Module Assembled','The 2S module produced at the end of the assembly process');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_APD_ID,APD_NAME,APD_DESCR)
values (10,2,5,&l_sensor_kop_id,'COMPONENT',null,'2S Sensor Top','2S sensor with HV tail which is being assembled as TOP sensor in the sensor sandwich');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_APD_ID,APD_NAME,APD_DESCR)
values (11,3,5,&l_sensor_kop_id,'COMPONENT',null,'2S Sensor Bottom','2S sensor with HV tail which is being assembled as BOTTOM sensor in the sensor sandwich');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_APD_ID,APD_NAME,APD_DESCR)
values (12,4,5,&l_jig_kop_id,'JIG',null,'Assembly jig','Jig for the sensor sandwich');


insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_APD_ID,APD_NAME,APD_DESCR)
values (13,1,6,&l_module_kop_id,'PRODUCT',9,'2S Module Assembled','The 2S module produced at the end of the assembly process');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_APD_ID,APD_NAME,APD_DESCR)
values (14,2,6,&l_sensor_kop_id,'COMPONENT',10,'2S Sensor Top','2S sensor with HV tail which is assembled as TOP sensor in the sensor sandwich');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_APD_ID,APD_NAME,APD_DESCR)
values (15,3,6,&l_sensor_kop_id,'COMPONENT',11,'2S Sensor Bottom','2S sensor with HV tail which is assembled as BOTTOM sensor in the sensor sandwich');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_APD_ID,APD_NAME,APD_DESCR)
values (16,4,6,&l_hybrid_kop_id,'COMPONENT',null,'Front-end Hybrid','Fornt-end hybrid which is being assembled to the sensor sandwich');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_APD_ID,APD_NAME,APD_DESCR)
values (17,5,6,&l_jig_kop_id,'JIG',null,'Assembly jig','Jig for the Hybrid asssembly');


insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_APD_ID,APD_NAME,APD_DESCR)
values (18,1,7,&l_module_kop_id,'PRODUCT',13,'2S Module Assembled','The 2S module produced at the end of the assembly process');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_APD_ID,APD_NAME,APD_DESCR)
values (19,2,7,&l_sensor_kop_id,'COMPONENT',14,'2S Sensor Top','2S sensor with HV tail which is assembled as TOP sensor in the sensor sandwich');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_APD_ID,APD_NAME,APD_DESCR)
values (20,3,7,&l_sensor_kop_id,'COMPONENT',15,'2S Sensor Bottom','2S sensor with HV tail which is assembled as BOTTOM sensor in the sensor sandwich');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_APD_ID,APD_NAME,APD_DESCR)
values (21,4,7,&l_jig_kop_id,'JIG',null,'Assembly jig','Jig for the Hybrid asssembly');


insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_APD_ID,APD_NAME,APD_DESCR)
values (22,1,8,&l_module_kop_id,'PRODUCT',18,'2S Module Assembled','The 2S module produced at the end of the assembly process');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_APD_ID,APD_NAME,APD_DESCR)
values (23,2,8,&l_sensor_kop_id,'COMPONENT',19,'2S Sensor Top','2S sensor with HV tail which is assembled as TOP sensor in the sensor sandwich');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_APD_ID,APD_NAME,APD_DESCR)
values (24,3,8,&l_jig_kop_id,'JIG',null,'Assembly jig','Jig for the Hybrid asssembly');


insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_APD_ID,APD_NAME,APD_DESCR)
values (25,1,9,&l_module_kop_id,'PRODUCT',22,'2S Module Assembled','The 2S module produced at the end of the assembly process');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_APD_ID,APD_NAME,APD_DESCR)
values (26,2,9,&l_sensor_kop_id,'COMPONENT',20,'2S Sensor Bottom','2S sensor with HV tail which is assembled as BOTTOM sensor in the sensor sandwich');

insert into ASSEMBLY_PART_DEFINITIONS (APD_ID,APD_NUMBER,APD_ASD_ID,APD_KOP_ID,APD_TYPE,APD_APD_ID,APD_NAME,APD_DESCR)
values (27,3,9,&l_jig_kop_id,'JIG',null,'Assembly jig','Jig for the Hybrid asssembly');

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

INSERT INTO ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_GET_ACTION,AAD_SET_ACTION,AAD_SET_STATUS) 
  WITH names AS ( 
    SELECT 1 a,   &l_prep_attr_id b,  'F' c,  null d,   null e        FROM dual UNION ALL 
    SELECT 3,   &l_prep_attr_id,  'F',  null,   null         FROM dual UNION ALL 
    SELECT 5,   &l_prep_attr_id,  'F',  null,   null         FROM dual UNION ALL 
    SELECT 7,   &l_prep_attr_id,  'F',  null,   null         FROM dual UNION ALL 
    SELECT 7,   &l_prep_attr_id,  null, 'T',    'COMPLETED'  FROM dual
  ) 
  SELECT * FROM names;

INSERT INTO ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_GET_ACTION,AAD_SET_ACTION,AAD_SET_STATUS) 
  WITH names AS ( 
    SELECT 1 a,   &l_bad_attr_id b,  'F' c,  null d,   null e       FROM dual UNION ALL 
    SELECT 1,     &l_bad_attr_id,    null,   'T',     'FAILED'      FROM dual UNION ALL 
    SELECT 3,     &l_bad_attr_id,    null,   'T',     'FAILED'      FROM dual UNION ALL 
    SELECT 5,     &l_bad_attr_id,    null,   'T',     'FAILED'      FROM dual UNION ALL 
    SELECT 7,     &l_bad_attr_id,    null,   'T',     'FAILED'      FROM dual
  ) 
  SELECT * FROM names;

-- Module, step 1

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_GET_ACTION)
values (9,&l_bad_attr_id,'F');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_SET_ACTION,AAD_SET_STATUS)
values (9,&l_bad_attr_id,'T','FAILED');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_GET_ACTION)
values (10,&l_prep_attr_id,'T');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_GET_ACTION)
values (10,&l_bad_attr_id,'F');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_SET_ACTION,AAD_SET_STATUS)
values (10,&l_top_attr_id,'T','COMPLETED');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_SET_ACTION,AAD_SET_STATUS)
values (10,&l_bad_attr_id,'T','FAILED');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_GET_ACTION)
values (11,&l_prep_attr_id,'T');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_GET_ACTION)
values (11,&l_bad_attr_id,'F');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_SET_ACTION,AAD_SET_STATUS)
values (11,&l_bottom_attr_id,'T','COMPLETED');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_SET_ACTION,AAD_SET_STATUS)
values (11,&l_bad_attr_id,'T','FAILED');

-- Module, step 2

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_SET_ACTION,AAD_SET_STATUS)
values (13,&l_bad_attr_id,'T','FAILED');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_GET_ACTION)
values (14,&l_prep_attr_id,'T');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_GET_ACTION)
values (14,&l_top_attr_id,'T');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_GET_ACTION)
values (14,&l_bad_attr_id,'F');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_SET_ACTION,AAD_SET_STATUS)
values (14,&l_bad_attr_id,'T','FAILED');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_GET_ACTION)
values (15,&l_prep_attr_id,'T');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_GET_ACTION)
values (15,&l_bottom_attr_id,'T');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_GET_ACTION)
values (15,&l_bad_attr_id,'F');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_SET_ACTION,AAD_SET_STATUS)
values (15,&l_bad_attr_id,'T','FAILED');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_SET_ACTION,AAD_SET_STATUS)
values (16,&l_bad_attr_id,'T','FAILED');

-- Module, step 3

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_SET_ACTION,AAD_SET_STATUS)
values (18,&l_bad_attr_id,'T','FAILED');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_GET_ACTION)
values (19,&l_prep_attr_id,'T');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_GET_ACTION)
values (19,&l_top_attr_id,'T');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_GET_ACTION)
values (19,&l_bad_attr_id,'F');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_SET_ACTION,AAD_SET_STATUS)
values (19,&l_bad_attr_id,'T','FAILED');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_GET_ACTION)
values (20,&l_prep_attr_id,'T');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_GET_ACTION)
values (20,&l_bottom_attr_id,'T');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_GET_ACTION)
values (20,&l_bad_attr_id,'F');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_SET_ACTION,AAD_SET_STATUS)
values (20,&l_bad_attr_id,'T','FAILED');

-- Module, step 4

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_SET_ACTION,AAD_SET_STATUS)
values (22,&l_bad_attr_id,'T','FAILED');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_GET_ACTION)
values (23,&l_prep_attr_id,'T');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_GET_ACTION)
values (23,&l_top_attr_id,'T');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_GET_ACTION)
values (23,&l_bad_attr_id,'F');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_SET_ACTION,AAD_SET_STATUS)
values (23,&l_bad_attr_id,'T','FAILED');

-- Module, step 5

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_SET_ACTION,AAD_SET_STATUS)
values (25,&l_bad_attr_id,'T','FAILED');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_GET_ACTION)
values (26,&l_prep_attr_id,'T');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_GET_ACTION)
values (26,&l_bottom_attr_id,'T');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_GET_ACTION)
values (26,&l_bad_attr_id,'F');

insert into ASSEMBLY_ATTRIBUTE_DEFINITIONS (AAD_APD_ID,AAD_ATTRIBUTE_ID,AAD_SET_ACTION,AAD_SET_STATUS)
values (26,&l_bad_attr_id,'T','FAILED');

-- Locations

insert into ASSEMBLY_SAME_LOCATIONS values (-1, -2);
