{% include navigation.html %}

# Introduction

Intelligent, Enhanced & Seamless Industrialization or IESI is bringing augmented automation to life. 
It primarily acts as a DevOps orchestrator focusing on activities related to test, release, operate and monitor. 
Yet, it augments user configuration with intelligent use of data which it caches internally so that it can be used during execution.

> The data science approach for automation

Our framework is not and does not have the ambition of becoming: a data platform, a data processing tool, a data transfer middleware, 
a decoupling layer, a robotics tool, just test automation...

Instead we are continuously working to improve our ease of use and internal intelligence by integrating artificial intelligence 
and machine learning.

## Approach

As an automation engineers can design automation scripts once and run them many times. 
You can leverage reusable constructs making it easier to configure, more manageable to scale and faster to grow. 
* **design** automation scripts and store them as configuration
* **execute** automation scripts immediately or **schedule** them
* the framework will run a script on a certain environment. It will get the configuration, run the actions and store the outcome as **result**
* **report** on results to get status and progress information
* **analyze** the results to get insights in trends and root causes
* **monitor** the activity and current status of the framework and systems under test
* get **alerts** when important and interesting events take place

![automation-approach](/{{site.repository}}/images/introduction/automation-approach.png)

## Configuration-driven

Our approach positions itself as a configuration-driven framework where automation scripts are configured rather than coded. 
Common actions and components are managed in libraries that are maintained centrally and that are reused to design scripts. 

It is a toolbelt rather than a one-stop solution:
 * quick to extend with new functions
 * effective in growing the coverage of automation

The framework decouples the configuration from the data aspects and manages it in a distributed manner. 
It is distributed by design allowing to be used on any size of project: low entry installation and onboarding (missing UI interactions for now).

An automation framework on its own is not a miraculous solution. 
You need to add solid data management, creative automation design and great engineering skills to the mix. 
With these ingredients, it will be possible to create innovative solutions. 
Our framework will help with its configuration-driven toolbelt approach.

![automation-success](/{{site.repository}}/images/introduction/automation-success.png)

Growing coverage of automation is achieved through intelligent use of parameterization and design skills. 
By making use of parameter files or metadata driven approaches, the number of executions for a common script can be scaled without requiring a new script to be written. 
The test approach for [ETL flow testing](/{{site.repository}}/pages/approach/etlflowtesting.html) illustrates this concept:
* Divide scripts into small reusable blocks of functionality (with a common purpose)
* Make use of parameterization from the start
* Infuse scripts with runtime variables, either coming from the system under test or from the any inventory that can be read by the framework

## Use cases

* Enable digital transformations
  * Maintain quality across digital and legacy solutions
* Accelerate software delivery time-to-market
  * Monitor regression impact continuously
* Complement existing automation tools
  * Add integration across automation tools

## Architecture

* Java based framework
