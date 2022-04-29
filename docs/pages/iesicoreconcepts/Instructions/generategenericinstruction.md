{% include navigation.html %}

# Generate - Generic Instructions
## Overview
This page contains all information on generating and formatting of person detail values

|Instruction Syntax| Instruction Name| Instruction Description|
|------------------|-----------------|------------------------|
|* |belgium.nationalRegisterNumber|Get a Belgian national register number conform to the standard format|
|* |person.email|Generates a random email address|
|* |person.firstname|Generates a random firstname|
|* |person.lastname|Generates a random lastname|
|* |person.phonenumber|Generates a random phonenumber conform to custom format|

Inventory location: a default inventory of example data is available in app/metadata/gen-directory within your sandbox. Custom libraries for specific data generation can be created, edited and consulted in this file location. This can be used if the user would like to use the function person.first - person.lastname - person.email - etc.

### belgium.nationalRegisterNumber
```
Description: Get a Belgian national register number conform to the standard format
Subroutine Syntax: {{*belgium.nationalRegisterNumber(X,Y)}}
  <X>: date of birth in ddMMYYYY format
  <Y>: gender (1 = male - 2= female)

Example:
{{*belgium.nationalRegisterNumber(30111995,1)}}

Example Output:
95113058307
```
### person.email
```
Description: Generates a random email address
Subroutine Syntax: {{*person.email()}}
Inventory Location: app/metadata/gen

Example:
{{*person.email()}}

Example Output:
test@tester.com
```
### person.firstname
```
Description: Generates a random firstname
Subroutine Syntax: {{*person.firstname()}}
Inventory Location: app/metadata/gen

Example:
{{*person.firstname()}}

Example Output:
Jane
```
### person.lastname
```
Description: Generates a random lastname
Subroutine Syntax: {{*person.lastname()}}
Inventory Location: app/metadata/gen

Example:
{{*person.lastname()}}

Example Output:
Doe
```
### person.phonenumber
```
Description: Generates a random phonenumber
Subroutine Syntax: {{*person.phonenumber()}}
Inventory Location: app/metadata/gen

Example:
{{*person.phonenumber()}}

Example Output:
712345684
```
