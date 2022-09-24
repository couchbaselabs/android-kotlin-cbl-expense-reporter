# How to create pre-built database for CB Lite

- Must create the database first using the cblite tool provided in each folder for which ever platform you are using (Windows, Mac, or Linux). 

```sh
./mac/cblite --create starting.cblite2
```

- Once in the cblite shell, hit Control-C or break character to exit the shell. 

- Create a script to import the images.  An example for Linux and Mac named import_managers.sh is provided as an example. 
- Run the script.

```sh
./import_managers.sh
```

- Create a script to import the expense types.  An example for Linux and Mac named import_expense_types.sh is provided as an example. 
- Run the script.
```sh
./import_expense_types.sh
```