-- ============================================================
-- Migration: Fix material_supplier_invoice_details table schema
-- Adds missing columns required by MaterialSupplierInvoiceHeaderDetails entity
-- 
-- ERROR BEING FIXED: JDBC exception "Unknown column 'msihd1_0.quotation_id' in 'field list'"
-- 
-- Run this migration with: mysql -u dler -p mrmason < V20261002__fix_material_supplier_invoice_details_schema.sql
-- ============================================================

-- First, check current table structure
SELECT 'Current table structure check' AS step;
DESCRIBE material_supplier_invoice_details;

-- Add the missing primary key column (as nullable to handle existing data)
-- If column already exists, this will error - that's OK, just skip to next step
ALTER TABLE material_supplier_invoice_details ADD COLUMN quotation_id_line_id VARCHAR(255) NULL;

-- Populate existing rows with unique values for the new primary key
UPDATE material_supplier_invoice_details 
SET quotation_id_line_id = CONCAT('QTL-', COALESCE(supplier_id, 'unknown'), '-', COALESCE(material_line_id, 'unknown')) 
WHERE quotation_id_line_id IS NULL;

-- Make the column NOT NULL and set it as primary key
ALTER TABLE material_supplier_invoice_details 
MODIFY COLUMN quotation_id_line_id VARCHAR(255) NOT NULL;

-- Drop existing primary key (if any) and add new one
ALTER TABLE material_supplier_invoice_details DROP PRIMARY KEY;
ALTER TABLE material_supplier_invoice_details ADD PRIMARY KEY (quotation_id_line_id);

-- Add the missing quotation_id column
ALTER TABLE material_supplier_invoice_details ADD COLUMN quotation_id VARCHAR(255) NULL;

-- Populate existing rows for quotation_id
UPDATE material_supplier_invoice_details 
SET quotation_id = CONCAT('QT-', COALESCE(supplier_id, 'unknown')) 
WHERE quotation_id IS NULL;

-- Make the column NOT NULL
ALTER TABLE material_supplier_invoice_details 
MODIFY COLUMN quotation_id VARCHAR(255) NOT NULL;

-- Create supporting indexes for better query performance
CREATE INDEX idx_msid_cmat_request ON material_supplier_invoice_details (cmaterial_request_id);
CREATE INDEX idx_msid_supplier ON material_supplier_invoice_details (supplier_id);
CREATE INDEX idx_msid_quotation_id ON material_supplier_invoice_details (quotation_id);
CREATE INDEX idx_msid_material_line ON material_supplier_invoice_details (material_line_id);

-- Verify the fix
SELECT 'Migration completed successfully!' AS result;
DESCRIBE material_supplier_invoice_details;