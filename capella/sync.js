function sync(doc, oldDoc) {
	/* 
		Data Validation - all documents require documentType field/attribute 
	*/
	validateNotEmpty("documentType", doc.documentType);

	if (doc.documentType == 'manager') {
		console.log("********Processing - setting manager to global/public");
		channel('!');

	} else if (doc.documentType == 'user') {
		if (!isDelete()) {
			console.log("********Processing User Profile document");

			//validate email is not empty before adding
			validateNotEmpty("email", doc.email);
			var channelId = "channel." + doc.email;

			//if update - validate email is read-only
			if (!isCreate()){
				validateReadOnly("email", doc.email, oldDoc.email);
			} 

			//provide access since all validation passed
			channel(channelId);	
			access(doc.email, channelId);
		}
	}
	else {
		if(!isDelete()){
			console.log("********Processing Reports and Expenses");
			//validate createdBy is not empty before adding as we use this for security to add it to the user channel
			validateNotEmpty("createdBy", doc.createdBy);
			var channelId = "channel." + doc.createdBy;

			//if update - validate createdBy is read-only
			if (!isCreate()){
				validateReadOnly("createdBy", doc.createdBy, oldDoc.createdBy);
			} 
			channel(channelId);
			access(doc.createdBy, channelId); 
		}
	}

	// helper function - get documentType property
	function getType() {
		return (isDelete() ? oldDoc.documentType : doc.documentType);
	}

	// helper function - Check if document is being created/added for first time
	function isCreate() {
		return ((oldDoc == false) || (oldDoc == null || oldDoc._deleted) && !isDelete());
	}

	// Check if this is a document delete
	function isDelete() {
		return (doc._deleted == true);
	}

	// Verify that specified property exists
	function validateNotEmpty(key, value) {
		if (!value) {
			throw ({ forbidden: key + " is not provided." });
		}
	}

	 // Verify that specified property value has not changed during update
	 function validateReadOnly(name, value, oldValue) {
		if (value != oldValue) {
		  throw({forbidden: name + " is read-only."});
		}
	  }
}