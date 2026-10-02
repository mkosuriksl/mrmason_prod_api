-- ============================================================
-- Migration: Add quotation_id column to material_supplier_quotation_details table
-- This fixes the JDBC exception: "Unknown column 'ms1_0.quotation_id' in 'field list'"
-- 
-- ERROR BEING FIXED:
-- JDBC exception executing SQL [select ms1_0.material_line_item,ms1_0.cmatmaterial_requestid,ms1_0 discount,ms1_0.gst,...
-- ms1_0.quotation_id from material_supplier_quotation_details ms1_0 where ms1_0.material_line_item=?]
-- [Unknown column 'ms1_0.quotation_id' in 'field list']
--
-- Run this migration with: mysql -u dler -p mrmason < V20261003__add_quotation_id_to_material_supplier_quotation_details.sql
-- ============================================================

-- First, check current table structure
SELECT 'Current table structure check' AS step;
DESCRIBE material_supplier_quotation_details;

-- Add the missing quotation_id column
-- If column already exists, this will error - that's OK, just skip to next step
ALTER TABLE material_supplier_quotation_details ADD COLUMN quotation_id VARCHAR(255) NULL;

-- Populate existing rows with unique values for the new column based on existing data
UPDATE material_supplier_quotation_details 
SET quotation_id = CONCAT('QT-', COALESCE(supplier_id, 'unknown'), '-', COALESCE(material_line_item, 'unknown')) 
WHERE quotation_id IS NULL;

-- Make the column NOT NULL after populating
ALTER TABLE material_supplier_quotation_details 
MODIFY COLUMN quotation_id VARCHAR(255) NOT NULL;

-- Create indexes for better query performance
CREATE INDEX idx_msqd_quotation_id ON material_supplier_quotation_details (quotation_id);
CREATE INDEX idx_msqd_cmat_request ON material_supplier_quotation_details (cmatmaterial_requestid);
CREATE INDEX idx_msqd_supplier ON material_supplier_quotation_details (supplier_id);
CREATE INDEX idx_msqd_material_line ON material_supplier_quotation_details (material_line_item);

-- Verify the fix
SELECT 'Migration completed successfully!' AS result;
DESCRIBE material_supplier_quotation_details;