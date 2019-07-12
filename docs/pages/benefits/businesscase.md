{% include navigation.html %}

# The business case for automation

The business case for automation is typically made very quickly: it is faster to automate than to do it manually. 
This is true. However, what does it mean for our automation framework. 
We like to think that it takes about the same amount of time to automate while performing manual tasks. But it requires training and discipline. 
So this requires another approach to make the case.

*250 script executions per FTE*

> For every FTE working on automation, a balanced set of only 250 script executions is required to break even.

This is a simple formula to build and track the business case.

*Note that infrastructure costs are not taken into account here*

## Script development

Our experiences has learned that automation scripts have different levels of complexity and require a different amount of effort to create. 

|Complexity|Effort (in days)|
|---|---|
|Low|1|
|Medium|3|
|High|5|
|Very High|7|

There are however a couple of remarks to make:
* This an average and will also take care of the required effort for installation and configuration of the framework
* It does not take into account the development of new functionalities for the framework or automation environment
* The framework fosters reuse and as such scripts will be reused by others. Our assumption is that the first to develop a reusable script pays for it, while other will benefits from its reusability.

## Automation benefits

Similar to the development effort, the automation benefits per script execuction can also be categorized in the following categories:

|Benefits|Gain (in minutes)|
|---|---|
|Low|10|
|Medium|30|
|High|60|
|Very High|120|

Note that
* the efficiency gain is only earned by the script that is triggered. So, reusable scripts cannot earn benefits.
* To calculate the average execution number, we make assume the following benefits contribution percentage for the corresponding scripts: 40% low, 30% medium, 20% high and 10% very high

## Benefit measurement

We are currently working on a mechanism that allows you to measure and track automation benefits via the automation framework.

## Infrastructure costs

More information will follow shortly.