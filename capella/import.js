function(doc) {
	if (
		doc.documentType == 'report' || 
		doc.documentType == 'expense' || 
		doc.documentType == 'user' || 
		doc.documentType == 'manager') {
		return true;
	}
	return false;
} 