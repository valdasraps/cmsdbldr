-- Create Channels table

INSERT INTO CMS_TST_CORE_COND.CHANNEL_MAPS_BASE (CHANNEL_MAP_ID, IS_RECORD_DELETED, EXTENSION_TABLE_NAME)
    SELECT CHANNEL_MAP_ID, 'F', 'TEST_CHANNELS' FROM CMS_TST_TEST_COND.TEST_CHANNELS;

-- Create Kind of condition

INSERT INTO CMS_TST_CORE_COND.KINDS_OF_CONDITIONS (NAME, EXTENSION_TABLE_NAME, IS_RECORD_DELETED) 
   VALUES ('IV', 'TEST_IV', 'F');
   
INSERT INTO CMS_TST_CORE_COND.KINDS_OF_CONDITIONS (NAME, EXTENSION_TABLE_NAME, IS_RECORD_DELETED) 
   VALUES ('FILES', 'TEST_FILES', 'F');

-- Condition to part relationship

insert into CMS_TST_CORE_COND.COND_TO_PART_RLTNSHPS (KIND_OF_PART_ID, KIND_OF_CONDITION_ID, IS_RECORD_DELETED, DISPLAY_NAME)
select KIND_OF_PART_ID, KIND_OF_CONDITION_ID, 'F', KOP.DISPLAY_NAME || ' to ' || KOC.NAME 
  from CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS KOP, CMS_TST_CORE_COND.KINDS_OF_CONDITIONS KOC;
   
-- Condition to attribute relationship

INSERT INTO CMS_TST_CORE_COND.COND_TO_ATTR_RLTNSHPS (KIND_OF_CONDITION_ID, ATTR_CATALOG_ID, DISPLAY_NAME, IS_RECORD_DELETED) VALUES
 ((SELECT KIND_OF_CONDITION_ID FROM CMS_TST_CORE_COND.KINDS_OF_CONDITIONS WHERE NAME='IV'),
  (SELECT ATTR_CATALOG_ID FROM CMS_TST_CORE_ATTRIBUTE.ATTR_CATALOGS WHERE DISPLAY_NAME='TEST Position'),
  'TEST',
  'F');

INSERT INTO CMS_TST_CORE_COND.COND_TO_PART_RLTNSHPS (KIND_OF_PART_ID, KIND_OF_CONDITION_ID, IS_RECORD_DELETED, DISPLAY_NAME)
      VALUES ((SELECT KIND_OF_PART_ID FROM CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS WHERE DISPLAY_NAME = 'TEST Tower'),
                      (SELECT KIND_OF_CONDITION_ID FROM CMS_TST_CORE_COND.KINDS_OF_CONDITIONS WHERE NAME='IV'), 'F', 'TEST'); 