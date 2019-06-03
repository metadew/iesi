{% include navigation.html %}

# Stop on error

For each action it is possible to indicate if the script needs to stop when the status of the action is evaluated as an error.
* In order to indicate a script to stop on error, set the flag Stop on Error to `Y`
* Warnings are not considered for the stop on error functionality

*Note: the stop on error flag keeps in mind [expected errors](/{{site.repository}}/pages/design/expectederrors.html), an approach which is commonly used for negative testing.*

Below are some examples where the stop on flag has been set to `Y`

|Number|Expected error|Stop on error|Run 1|Run 2|Run 3|Run 4|Run 5|
|---|---|---|---|---|---|---|---|
|1|N|N|SUCCESS|SUCCESS|SUCCESS|SUCCESS|SUCCESS|
|2|N|Y|SUCCESS|WARNING|ERROR|SUCCESS|SUCCESS|
|3|N|N|SUCCESS|SUCCESS|![red](/{{site.repository}}/images/icons/red-dot.png)|ERROR|SUCCESS|
|4|N|N|SUCCESS|SUCCESS||ERROR|SUCCESS|
|5|N|Y|SUCCESS|SUCCESS||ERROR|SUCCESS|
|6|Y|Y|SUCCESS|SUCCESS||![red](/{{site.repository}}/images/icons/red-dot.png)|ERROR|
|END|||||||![red](/{{site.repository}}/images/icons/red-dot.png)|