{% include navigation.html %}

# Introduction

Intelligent, Enhanced & Seamless Industrialization or IESI is bringing augmented automation to life. It primarily acts as a DevOps orchestrator focusing on activities related to test, release, operate and monitor. IESI is able to augment user configuration with intelligent use of data by caching it internally and using it during execution.

> "The data science approach for automation"

The framework does not serve the purpose of having a singular ambition. Therefore we are continuously working on improving our ease of use and internal intelligence by integrating artificial intelligence and machine learning. <br>

Let’s deep dive into the framework approach and concepts.

## Approach
> "Configure once, run many times"

Our key principle is for our automation engineers to design using a flexible configuration and reusable approach. This will allow you to leverage reusable constructs making it easier to configure, scale and grow. 

The core features of the framework can be visualized with the illustration below:
* **Design** automation scripts/constructs and store them as configuration
* **Execute** automation scripts on a dedicated environment by calling the defined configuration and store the outcome as results
* **Schedule** automation scripts or execute them on-demand
* **Report** the results and track status and progress information
* **Analyze** the results and retrieve insights in trends and root causes
* **Monitor** the activity and current status of the framework and systems under test
* **Alert** on important or unusual events and detect potential issues sooner

![automation-approach](/{{site.repository}}/images/introduction/automation-approach.png)

## Configuration-driven
> "Putting reusable configuration central

Our approach positions itself as a configuration-driven framework where automation scripts are configured rather than coded. Common actions and components are managed in libraries that are maintained centrally and that are reused to design scripts. In that way, the framework is designed to _configure once, run many times_.

It is a toolbelt rather than a one-stop solution:
[x] Quick to extend with new functions
[x] Effective in growing the coverage of automation

![iesi_concepts](/{{site.repository}}/images/introduction/iesi_concepts.png)

The framework decouples the configuration from the data aspects and manages it in a distributed manner. It is distributed by design allowing to be used on any size of project: low entry installation and onboarding. <br>

**A firm solution can only be achieved if you combine your automation framework with solid data management, creative automation design and engineering skills. 
A mixture of all these ingredients, will result in strong and innovative solutions.** <br>

## Parameterization

> "The number of executions can be scaled without requiring new script designs"

Growing coverage of automation is achieved through intelligent use of parameterization and design skills. By making use of parameter files or metadata driven approaches, the number of executions for a common script can be scaled without requiring a new script to be written. <br>
 
The test approach for ETL flow testing illustrates this concept:
* Divide scripts into small reusable blocks of functionality (with a common purpose)
* Make use of parameterization from the start
* Infuse scripts with runtime variables, either coming from the system under test or from the any inventory that can be read by the framework

Interested in more use cases? Have a look at the Use Cases page!

## Architecture

* Technology: Java
* User Interface: JavaScript
* Distributed by design and integrates with different types of repositories (code, data, configuration)

