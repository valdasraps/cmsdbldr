# Detector construction

- Database preparation (01_construct.sql)
-- Add SUBDETECTOR 'GEM'
-- Add some KIND_OF_PARTS
--- 'GEM ROOT'
--- 'GEM Tower'
--- 'GEM Pack'
--- 'GEM Board'
-- Add 'GEM ROOT' PART and relation to itself
-- Add some ATTRIBUTES
--- Add 'GEM Position' ATTRIBUTE_CATALOG
--- Add 'GEM Board Position' (relate KIND_OF_PART with ATTRIBUTE_CATALOG)
--- Add some attributes to 'GEM Position' CATALOG

- Data loading (02_construct.xml)
-- Loads parts hierarchy with attributes

# Conditions

- Database preparation (03_condition.sql)
-- Add GEM_CHANNELS, generate some channels
-- Add KIND_OF_CONDITION 'IV' (GEM_IV table)
-- Add KIND_OF_CONDITION 'FILES' (GEM_FILES table) with LOBs

- Data loading (04_condition.xml)
-- Loads data into GEM_IV table with RUN, CHANNEL, TAG and IOV

- Data loading (05_condition.zip)
-- Loads data into GEM_FILES table with LOBs
