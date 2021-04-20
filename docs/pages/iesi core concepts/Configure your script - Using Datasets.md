{% include navigation.html %}
# Configure your script - Using Datasets
## What is a dataset?
When executing a script, the engine takes a set of input parameters and performs a single (set of) operation(s) which generates a technical outcome. 
This **set of input parameters** or data can be **stored in a datafile** and can be **called upon during the execution** of our script. This pre-defined data can come from **any data file** supported by the framework, and be referred to using its label during the script design
  
Next to feeding our intelligent engine with input data, the framework also offers to opportunity to **capture the outcome** or results and **store it back in an output dataset.**

## How to create a dataset?
A dataset consists out of:

* data: consists information on the the input parameters (e.g. key-value pairs)
* metadata: consists information on the config (e.g. environment, labels, etc.)

The dataset template can be found here (link TBD)
An exercise can be found here (link TBD)

## Action types - Using datasets
Action types related to the use of datasets:
*	[data.setDatasetConnection](https://github.com/metadew/iesi/blob/b66495be0076f1a8014bebf0bde0f649710a6008/docs/pages/iesi%20core%20concepts/Action%20Types/data.setDatasetConnection.md): Define a dataset connection that can be used in any next action
