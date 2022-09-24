#!/bin/bash
#

filename="expense_types.json" #data file
dbFileName='starting.cblite2' #database file

length=1 # only get 20 items to put in the database

#loop through child array to get values out and save as seperate documents
for ((count=0;count<$length;count++))
do
	itemIndex=".[$count]" #get the json index for the current element in the array 
	idIndex=".documentId" #get the field that we want to use as the document id 

	
	json=$(cat $filename | jq $itemIndex) #get the full json of an item
	id=$(echo $json | jq $idIndex) #get the value of the documentId field

	# add to the database (if you are on a different platform 
	# change the folder location of cblite)
	./mac/cblite put --create $dbFileName $id "$json"
done

# you can check by listing the files in the database
# $cblite ls -l --limit 10 $dbFileName