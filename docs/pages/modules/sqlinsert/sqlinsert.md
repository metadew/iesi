{% include navigation.html %}

# SQL Insert

Generating test data for unit testing is sometimes a time consuming activity for which a developer or tester 
would like to use an excel template rather than writing pure SQL statements. 
So what usually happens is that the data is entered in excel and an SQL creation formula is defined to generate 
the needed SQL statement. 
Sometimes taking more time than what someone would like to, forgetting a comma or quote is easily done – 
especially for larger statements. 
And debugging can sometimes result in some frustration as well.

> The SQL Insert module offers database interaction functionalities allowing to insert and update data without needing deep technical knowledge

For that reason, a module has been developed as part of the automation framework which offers a functionality 
to define test data in an excel format. 
It allows a user to freely define the table format and content while an excel macro takes care of the 
generation of the SQL insert statements. In this way, a lot of time can be saved which is usually spent on 
getting the SQL syntax correct.

> Given that the module will prevent erroneous entry, quality improvements are embedded in the benefits generating even high ROI

Another advantage of this module is that you will be able to alter data easily and generate new test data for alternative cases. 
You do not even need to erase your old data, since each sheet can be copied in the same workbook allowing to keep a traceable path to all different data sets.

## Functionalities

The module is made up out of a single excel file containing an embedded macro (using VBA coding). 
In this way, is is very portable and can be used in a standalone manner on a single workstation. 
It can be stored, saved and reused freely without any dependencies. 

* Single script generation
* Combined script generation
* Use excel to define and store data (including all required functions and formulas)
  * Supporting basic as well as advanced scenarios (e.g. transformation / lookup value  scenarios)
  * Multi-scenario support via separate worksheets
* Multi-database support, both JDBC and ODBC connections are supported
  * MySQL, Netezza, Oracle, Postgresql, SQLite, SQLServer, Hive (beta, JDBC only)
* Easy integration with version control via flat file exports
* Leveraging the automation framework
  * Zero-touch install base – reusable across workstations
  * Database connectivity; getting table information and data from the database / execute SQL scripts in the database

![sqlinsert-overview](/{{site.repository}}/images/modules/sqlinsert/sqlinsert-overview.png)
  
## Use cases

|Activity|Designer|Developer|Tester|End user|
|---|:---:|:---:|:---:|:---:|
|ETL design|![green](/{{site.repository}}/images/icons/green-dot.png)|![green](/{{site.repository}}/images/icons/green-dot.png)|||
|Development and unit test||![green](/{{site.repository}}/images/icons/green-dot.png)|||
|Testing|![green](/{{site.repository}}/images/icons/green-dot.png)|![green](/{{site.repository}}/images/icons/green-dot.png)|![green](/{{site.repository}}/images/icons/green-dot.png)||
|Data migration<br>* Environment initialisation<br>* Referential data upload<br>* Data transform and load (1)|<br><br>![green](/{{site.repository}}/images/icons/green-dot.png)<br>![green](/{{site.repository}}/images/icons/green-dot.png)|<br>![green](/{{site.repository}}/images/icons/green-dot.png)<br>![green](/{{site.repository}}/images/icons/green-dot.png)<br>![green](/{{site.repository}}/images/icons/green-dot.png)|<br>![green](/{{site.repository}}/images/icons/green-dot.png)<br>![green](/{{site.repository}}/images/icons/green-dot.png)<br>|<br><br>![green](/{{site.repository}}/images/icons/green-dot.png)<br>|
|Database extract to excel|![green](/{{site.repository}}/images/icons/green-dot.png)|||![green](/{{site.repository}}/images/icons/green-dot.png)|

*(1) Simple to medium complexity cases are easily supported. Volumes are restricted to excel version and capabilities.*

## Prerequisites

* Microsoft Excel, enabled for running VBA macros
* If database interactions are needed, the automation framework needs to be installed on the workstation.

## Getting started with single scripts

### Create a new sheet

* Copy or open the template
* In the distribution version, a sheet `script_template` is available that allows to start from a clean instance.

insert image

* Save you document with the appropriate name like you would do for any excel type document

### Creation of different sheets within a workbook

* It is possible to create multiple copies of the template sheet or any data sheet for that matter in the workbook. 
The template will base its logic on the input and thus also on the output with respect on the active sheet where you are located when running the `Generate Inserts` function.
* Different applications of this include:
  * Creation of different table inserts in 1 single excel file
  * Creation of different scenarios (e.g. for testing different cases, business scenarios, etc.)
* With the possibility to copy a worksheet, you can keep track of modifications without needing to lose the work that has been done previously. It also allows to quickly update and regenerate code if needed.
* You can rename the sheet as needed.

### Important notes

* The structure of the excel sheet `script_template` cannot be altered and needs to remain as defined in order for the template to work.

## Define the table structure

### General parameters

* Define the schema name and the table name of the applicable table and fill the fields out in cells:
  * Schema Name: B3
  * Table Name: B4

insert image

### Process parameters

* Define the commit type that will be used during the script generation. This commit type will define at which point in time a commit statement will be added to the script. Three options are available:
  * At the end of all statements: a commit statement will be added at the very end
  * After each statement: a commit statement will be added after each statement
  * Based on commit limit: a manual limit can be set (number) after how many statements a commit statement wil be added. If needed, a commit statement will be added automatically at the end.
* Define the commit limit only when selecting the based on commit limit type.

insert image
 
* When special characters are present in the values to be inserted, an escape character can be used. For example, the value “Trin.& Tobago Dol.” needs to be inserted. This required the & character to be escaped during the execution of the script. There are three steps required in order to achieve this:
  * Set the option Escape special characters to “yes”
  * Select the escape character to use in order to perform the escape function. For example, select “\”
  * In each applicable value, perform the necessary escape by adding the escape character in front of the special character. In our example, this will become: “Trin.\& Tobago Dol.”

*please refer the RDBMS documentation in order to have a definition of the special character notion.*

insert image

* Define any pre- or post- processing SQL statements that need to be included in the script. If the statement is not ended with a  semicolon ‘;’ (for Oracle statement compatibility) then this semicolon is added automatically.
  * This functionality is useful to allow adding common statements to be executed so that this does not need to be repeated when resetting a table in an environment. A common example is for instance the cleaning of a table: delete from SCHEMA.TABLE
  * Multiple sql statements can be separated using a semicolon ‘;’. For instance, delete from SCHEMA.TABLE where type = ‘A’; delete from SCHEMA.TABLE where type = ‘B’;

insert image

### Table structure

* List the fields that need to be inserted.
  * These fields can be defined in Row 1 as from field D.
  * All fields that are needed need to be filled out in a continuous manner, not having any column to be blank.

insert image

* For each field, indicate in Row 2 the appropriate data type. A drop down list is available, having the following possibilities. It is allowed to copy / paste the drop down list across if not available.
  * String: covering text type data formats (e.g. char, varchar, varchar2, etc.)
  * Number: covering number type data formats
  * Datetime: covering date and timestamp data formats
  * Custom: possible to use custom nested sql expressions to add more advanced functionalities

insert image

### Data entry

* Complete the data in the sheet as required.
* For each record that is added to the sheet, a record identifier needs to be added if it needs to be included in the generation.
  * The generation will start at row 3 and continue for all records having an identifier (without gaps) defined in column C.
  * The value which is defined in column C is not relevant and can be a number, a key word, etc.
  * This will allow to identify different criteria or sequences in a scenario as to define sequences in testing (e.g. step by step generation of events).

insert image

### Specific cases

#### Null values

* In order to add a NULL value, type null or NULL in the data field

#### Custom SQL statements

* In order to add a custom sql statement, the following structure is needed:
  * start with opening a bracket: (
  * type the sql statement needed: select column from table
  * end with a closing bracket: )
* If the custom sql statement need to select based on previous inserts, it is needed to ensure that the process parameter for commit is set to ‘after each statement’

### Important notes

* There is no check that is performed on the data entered with respect to the indicated data types.

## Generate SQL script

* Generate the SQL script by clicking the button ‘Generate Inserts’.
* A .sql file will be created in the same directory as where the excel file is located. The file name will contain:
  * The schema name and file name
  * The current date and timestamp
* At the end of the generation, it will be suggested to save the file path to the clipboard.

insert image

# Getting started with combined scripts

## Create a new sheet

* Copy or open the template
* In the distribution version, a sheet `generator_template` is available that allows to start from a clean instance.

insert image
 
* Save you document with the appropriate name like you would do for any excel type document

### Creation of different sheets within a workbook

* It is possible to create multiple copies of the template sheet or any data sheet for that matter in the workbook. The template will base its logic on the input and thus also on the output with respect on the active sheet where you are located when running the `Generate Script` function.
* The combined script will allow to include multiple single script worksheets into a single file that will be generated (see above). The template will allow to enter the order in which the worksheets need to be added to the generated script.
* Different applications of this include:
  * Creation of a single file with all scripts included
  * Creation of different scenarios (e.g. for testing different cases, business scenarios, etc.)
* With the possibility to copy a worksheet, you can keep track of modifications without needing to lose the work that has been done previously. It also allows to quickly update and regenerate code if needed.
* You can rename the sheet as needed.

### Important notes

* The structure of the excel sheet `generator_template` cannot be altered and needs to remain as defined in order for the template to work.

## Define the table structure

### General parameters

* Define the script name to be included in the generated combined script name of the applicable generator definition:
  * Script Name: B3

insert image

### Process parameters

* Not applicable

### Table structure

* List the configuration of the generator definition that needs to be used.
  * These values can be defined as of row 2
  * All fields that are needed need to be filled out in a continuous manner, not having any column to be blank.

insert image

### Data entry

* Complete the configuration definitions in the sheet as required.
* For each record that is added to the sheet, an order number needs to be added if it needs to be included in the generation.
  * The generation will start at row 2 and continue for all records having an identifier (without gaps) defined in column C.
  * The value which is defined in column C is not relevant and can be a number, a key word, etc. It represents the order by each of the rows and is best kept as the applicable number in this case.
  * This will allow to identify different criteria or sequences in a scenario as to define sequences in testing (e.g. step by step generation of events).

insert image

* Enter the type of generation to perform.
  * Worksheet: generate the insert statements from the worksheet defined in the column `Configuration`.
  * Script: not supported in version 1.4
  * Enter the configuration value associated with the selected type.

### Specific cases

* Not applicable

### Important notes

* There is no check that is performed on the data entered with respect to the indicated types and configuration. If the value of the worksheets is not correct, the generator will cause a fatal error.

## Generate SQL script

* Generate the SQL script by clicking the button `Generate Script`.
* A .sql file will be created in the same directory as where the excel file is located. The file name will contain:
  * The script name – if the script name is empty, the name of the worksheet will be used instead
  * The current date and timestamp
* At the end of the generation, it will be suggested to save the file path to the clipboard.

insert image

# Examples

* An example of a configured excel worksheet has been added in sheet ‘example’.

# Frequently asked questions

## Data types

|Question|Response|
|---|---|
|The date data type is missing|To insert data with the date data type, the setting ‘datetime’ can be used. The database will correctly accept the insert statement|
|The decimal data type is missing|To insert data with the decimal data type, the setting ‘number’ can be used. The database will correctly accept the insert statement. The data will be rounded/accepted depending on the size and precision by the database. Be careful since no warning message is available.|

## Special character escape

|Question|Response|
|---|---|
|The SQL script does not run while special characters are escaped|Please verify the following:<br>* Do you have the Escape special characters set to “Yes”?<br>* Are you using the correct escape character as indicated in the options?|

