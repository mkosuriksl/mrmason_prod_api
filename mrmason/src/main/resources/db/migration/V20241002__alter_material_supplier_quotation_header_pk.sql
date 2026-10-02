-- Migration script to change primary key of material_supplier_quotation_header table
-- This script changes the primary key from cmatmaterial_requestid to qutotation_id

-- Step 1: Add the new primary key column (if not exists)
-- Note: qutotation_id column should already exist with unique values

-- Step 2: Drop the existing primary key constraint
ALTER TABLE material_supplier_quotation_header DROP PRIMARY KEY;

-- Step 3: Add new primary key constraint on qutotation_id
ALTER TABLE material_supplier_quotation_header ADD PRIMARY KEY (qutotation_id);

-- Step 4: Create unique index on cmatmaterial_requestid + supplier_id for efficient lookups
CREATE UNIQUE INDEX idx_header_cmat_supplier ON material_supplier_quotation_header (cmatmaterial_requestid, supplier_id);

-- Step 5: (Optional) Add unique constraint to prevent duplicate supplier entries per order
-- This ensures each supplier can only have one quotation per order
-- ALTER TABLE material_supplier_quotation_header ADD CONSTRAINT uk_header_cmat_supplier UNIQUE (cmatmaterial_requestid, supplier_id);