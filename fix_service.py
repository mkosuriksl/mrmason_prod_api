import re

with open('D:/mrmason_prod_api/mrmason/src/main/java/com/application/mrmason/service/materialSupplierService.java', 'r') as f:
    content = f.read()

# Fix 1: item.setQuotationStatus(quotationStatus) -> item.setQuotationStatus(quotationStatus != null ? quotationStatus : Status.QUOTED)
old_pattern1 = r"\t\t\titem\.setQuotationStatus\(quotationStatus\);"
new_pattern1 = r"\t\t\titem.setQuotationStatus(quotationStatus != null ? quotationStatus : Status.QUOTED);"
content = re.sub(old_pattern1, new_pattern1, content)

# Fix 2: quotationHeader.setQuotationStatus(quotationStatus) -> quotationHeader.setQuotationStatus(quotationStatus != null ? quotationStatus : Status.QUOTED)
old_pattern2 = r"\t\tquotationHeader\.setQuotationStatus\(quotationStatus\);"
new_pattern2 = r"\t\tquotationHeader.setQuotationStatus(quotationStatus != null ? quotationStatus : Status.QUOTED);"
content = re.sub(old_pattern2, new_pattern2, content)

with open('D:/mrmason_prod_api/mrmason/src/main/java/com/application/mrmason/service/materialSupplierService.java', 'w') as f:
    f.write(content)

print('Fixes applied')