-- Add required subdetector

INSERT INTO CMS_TST_CORE_CONSTRUCT.SUBDETECTORS (SUBDETECTOR_NAME, IS_RECORD_DELETED) 
    SELECT 'TEST', 'F' 
    FROM dual 
    WHERE NOT EXISTS (select * FROM CMS_TST_CORE_CONSTRUCT.SUBDETECTORS WHERE (SUBDETECTOR_NAME ='TEST' AND IS_RECORD_DELETED = 'F'));

-- Add some kind of parts
 
INSERT INTO CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS (SUBDETECTOR_ID, EXTENSION_TABLE_NAME, IS_RECORD_DELETED, IS_DETECTOR_PART, IS_IMAGINARY_PART, DISPLAY_NAME)
   SELECT (SELECT SUBDETECTOR_ID FROM CMS_TST_CORE_CONSTRUCT.SUBDETECTORS where SUBDETECTOR_NAME = 'TEST'), 'PART','F', 'F', 'F', 'TEST ROOT' 
   FROM dual 
   WHERE NOT EXISTS (select * from CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS WHERE DISPLAY_NAME = 'TEST ROOT');
 
INSERT INTO CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS (SUBDETECTOR_ID, EXTENSION_TABLE_NAME, IS_RECORD_DELETED, IS_DETECTOR_PART, IS_IMAGINARY_PART, DISPLAY_NAME)
   SELECT (SELECT SUBDETECTOR_ID FROM CMS_TST_CORE_CONSTRUCT.SUBDETECTORS where SUBDETECTOR_NAME = 'TEST'), 'PART','F', 'F', 'F', 'TEST Tower' 
   FROM dual 
   WHERE NOT EXISTS (select * from CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS WHERE DISPLAY_NAME = 'TEST Tower');

INSERT INTO CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS (SUBDETECTOR_ID, EXTENSION_TABLE_NAME, IS_RECORD_DELETED, IS_DETECTOR_PART, IS_IMAGINARY_PART, DISPLAY_NAME)
   SELECT (SELECT SUBDETECTOR_ID FROM CMS_TST_CORE_CONSTRUCT.SUBDETECTORS where SUBDETECTOR_NAME = 'TEST'), 'PART','F', 'F', 'F', 'TEST Pack' 
   FROM DUAL 
   WHERE NOT EXISTS (select * from CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS WHERE DISPLAY_NAME = 'TEST Pack');

INSERT INTO CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS (SUBDETECTOR_ID, EXTENSION_TABLE_NAME, IS_RECORD_DELETED, IS_DETECTOR_PART, IS_IMAGINARY_PART, DISPLAY_NAME)
   SELECT (SELECT SUBDETECTOR_ID FROM CMS_TST_CORE_CONSTRUCT.SUBDETECTORS where SUBDETECTOR_NAME = 'TEST'), 'PART','F', 'F', 'F', 'TEST Board' 
   FROM dual 
   WHERE NOT EXISTS (select * from CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS WHERE DISPLAY_NAME = 'TEST Board');

INSERT INTO CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS (SUBDETECTOR_ID, EXTENSION_TABLE_NAME, IS_RECORD_DELETED, IS_DETECTOR_PART, IS_IMAGINARY_PART, DISPLAY_NAME)
   SELECT (SELECT SUBDETECTOR_ID FROM CMS_TST_CORE_CONSTRUCT.SUBDETECTORS where SUBDETECTOR_NAME = 'TEST'), 'PART','F', 'F', 'F', 'GEM Chamber'
   FROM dual
   WHERE NOT EXISTS (select * from CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS WHERE DISPLAY_NAME = 'GEM Chamber');

INSERT INTO CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS (SUBDETECTOR_ID, EXTENSION_TABLE_NAME, IS_RECORD_DELETED, IS_DETECTOR_PART, IS_IMAGINARY_PART, DISPLAY_NAME)
   SELECT (SELECT SUBDETECTOR_ID FROM CMS_TST_CORE_CONSTRUCT.SUBDETECTORS where SUBDETECTOR_NAME = 'TEST'), 'PART','F', 'F', 'F', 'GEM Readout PCB'
   FROM dual
   WHERE NOT EXISTS (select * from CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS WHERE DISPLAY_NAME = 'GEM Readout PCB');

INSERT INTO CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS (SUBDETECTOR_ID, EXTENSION_TABLE_NAME, IS_RECORD_DELETED, IS_DETECTOR_PART, IS_IMAGINARY_PART, DISPLAY_NAME)
   SELECT (SELECT SUBDETECTOR_ID FROM CMS_TST_CORE_CONSTRUCT.SUBDETECTORS where SUBDETECTOR_NAME = 'TEST'), 'PART','F', 'F', 'F', 'GEM Drift PCB'
   FROM dual
   WHERE NOT EXISTS (select * from CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS WHERE DISPLAY_NAME = 'GEM Drift PCB');

INSERT INTO CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS (SUBDETECTOR_ID, EXTENSION_TABLE_NAME, IS_RECORD_DELETED, IS_DETECTOR_PART, IS_IMAGINARY_PART, DISPLAY_NAME)
   SELECT (SELECT SUBDETECTOR_ID FROM CMS_TST_CORE_CONSTRUCT.SUBDETECTORS where SUBDETECTOR_NAME = 'TEST'), 'PART','F', 'F', 'F', 'GEM Foil'
   FROM dual
   WHERE NOT EXISTS (select * from CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS WHERE DISPLAY_NAME = 'GEM Foil');


INSERT INTO CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS (SUBDETECTOR_ID, EXTENSION_TABLE_NAME, IS_RECORD_DELETED, IS_DETECTOR_PART, IS_IMAGINARY_PART, DISPLAY_NAME)
   SELECT (SELECT SUBDETECTOR_ID FROM CMS_TST_CORE_CONSTRUCT.SUBDETECTORS where SUBDETECTOR_NAME = 'TEST'), 'PART','F', 'F', 'F', 'Hybrid'
   FROM dual
   WHERE NOT EXISTS (select * from CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS WHERE DISPLAY_NAME = 'Hybrid');


-- Add ROOT part and relation to itself

INSERT INTO CMS_TST_CORE_CONSTRUCT.PARTS (PART_ID, KIND_OF_PART_ID, IS_RECORD_DELETED, NAME_LABEL)
   SELECT 1000, KIND_OF_PART_ID, 'F', 'ROOT' 
   FROM CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS where DISPLAY_NAME = 'TEST ROOT';

INSERT INTO CMS_TST_CORE_CONSTRUCT.PART_TO_PART_RLTNSHPS (KIND_OF_PART_ID, KIND_OF_PART_ID_CHILD, IS_RECORD_DELETED, PRIORITY_NUMBER, DISPLAY_NAME)
    SELECT KIND_OF_PART_ID, KIND_OF_PART_ID, 'F', 0, 'ROOT-ROOT' 
    FROM CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS where DISPLAY_NAME = 'TEST ROOT';

INSERT INTO CMS_TST_CORE_CONSTRUCT.PARTS (PART_ID, KIND_OF_PART_ID, IS_RECORD_DELETED, NAME_LABEL, SERIAL_NUMBER)
   SELECT 1040, KIND_OF_PART_ID, 'F', 'Hybrid Part', 'Hybrid serial'
   FROM CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS where DISPLAY_NAME = 'Hybrid';

INSERT INTO CMS_TST_CORE_CONSTRUCT.PHYSICAL_PARTS_TREE (PART_ID, RELATIONSHIP_ID, PART_PARENT_ID, IS_RECORD_DELETED)
    SELECT PART_ID, RELATIONSHIP_ID, PART_ID, 'F'
    FROM 
      CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS KOP 
      JOIN CMS_TST_CORE_CONSTRUCT.PARTS P ON KOP.KIND_OF_PART_ID = P.KIND_OF_PART_ID 
      JOIN CMS_TST_CORE_CONSTRUCT.PART_TO_PART_RLTNSHPS R ON KOP.KIND_OF_PART_ID = R.KIND_OF_PART_ID and KOP.KIND_OF_PART_ID = R.KIND_OF_PART_ID_CHILD
    WHERE KOP.DISPLAY_NAME = 'TEST ROOT' AND P.NAME_LABEL = 'ROOT';

-- Add some attributes to TEST Board

INSERT INTO CMS_TST_CORE_CONSTRUCT.PART_TO_ATTR_RLTNSHPS (ATTR_CATALOG_ID, KIND_OF_PART_ID, DISPLAY_NAME)
   SELECT (SELECT ATTR_CATALOG_ID FROM CMS_TST_CORE_ATTRIBUTE.ATTR_CATALOGS WHERE DISPLAY_NAME = 'TEST Position' and IS_RECORD_DELETED = 'F'), 
          (SELECT KIND_OF_PART_ID FROM CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS WHERE DISPLAY_NAME = 'TEST Board'),
          'TEST Board Position'
   FROM dual
   WHERE NOT EXISTS (SELECT * FROM CMS_TST_CORE_CONSTRUCT.PART_TO_ATTR_RLTNSHPS WHERE DISPLAY_NAME = 'TEST Board Position');

INSERT INTO CMS_TST_CORE_CONSTRUCT.PART_TO_ATTR_RLTNSHPS (ATTR_CATALOG_ID, KIND_OF_PART_ID, DISPLAY_NAME)
   SELECT (SELECT ATTR_CATALOG_ID FROM CMS_TST_CORE_ATTRIBUTE.ATTR_CATALOGS WHERE DISPLAY_NAME = 'Foil Position' and IS_RECORD_DELETED = 'F'),
          (SELECT KIND_OF_PART_ID FROM CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS WHERE DISPLAY_NAME = 'GEM Foil'),
          'TEST Foil Position'
   FROM dual
   WHERE NOT EXISTS (SELECT * FROM CMS_TST_CORE_CONSTRUCT.PART_TO_ATTR_RLTNSHPS WHERE DISPLAY_NAME = 'TEST Foil Position');

-- Add some data into KOPS and PARTS for PART_EXTENSION tests

INSERT INTO CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS (SUBDETECTOR_ID, EXTENSION_TABLE_NAME, IS_RECORD_DELETED, IS_DETECTOR_PART, IS_IMAGINARY_PART, DISPLAY_NAME)
   SELECT (SELECT SUBDETECTOR_ID FROM CMS_TST_CORE_CONSTRUCT.SUBDETECTORS where SUBDETECTOR_NAME = 'TEST'), 'PART_DETAILS','F', 'F', 'F', 'PART DETAILS'
   FROM dual
   WHERE NOT EXISTS (select * from CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS WHERE DISPLAY_NAME = 'PART DETAILS');

INSERT INTO CMS_TST_CORE_CONSTRUCT.PARTS (PART_ID, KIND_OF_PART_ID, IS_RECORD_DELETED, NAME_LABEL)
  SELECT 1020, KIND_OF_PART_ID, 'F', 'Super name label'
  FROM CMS_TST_CORE_CONSTRUCT.KINDS_OF_PARTS where DISPLAY_NAME = 'PART DETAILS';
