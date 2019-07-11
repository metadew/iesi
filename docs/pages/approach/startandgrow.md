{% include navigation.html %}

# Start as soon as possible and grow in steps

When automating it is important to start as soon as possible. 
Start by verifying how steps need to be executed automatically and define the commands / associated parameters that are needed to do so. 
So, when you need to run a command, why not do it from the framework immediately.

This requires the necessary discipline but will help a great deal:
* to build the automation gradually
* to confirm early constraints in the solution hindering the automation potential

But, it does not work if the automation framework does not execute. 
That is why there are a couple of constructs to support gradually building an automation design and executing only a specific part.

## Gradual script design

Gradual script design is assisted by dummy actions.
* make use of the `fwk.dummy` action type to define a placeholder for a step that you would like to automate later

## Partial script execution

Partial script execution is enabled by the `actions` option of the *script launcher*. 
* `-actions <arg>`: select actions to execute or not (see [here](/{{site.repository}}/pages/operate/operate.html) for more information)
