{% include navigation.html %}

# Reporting

After running an automation script, the framework keeps track of the outcome in different ways:
* *log files*: detailed verbose logging on the server.
* *log tables*: outcome of the script and its actions stored in a database (success, warning, error)
* *trace tables*: details of the script execution with all values used (after parameter substitution)

|Type|Use|User|
|---|---|---|
|Log files|detailed debugging||
|Status follow-up and notification||
|Detailed analysis of outcome||

# Monitoring

* The automation framework allows realtime monitoring of what is happening or has happened. 
Labelling the scripts with additional dimensions allows to extend filtering: e.g. application, server, type (regression, connections, …) 
and make queries more context aware.

(insert images)