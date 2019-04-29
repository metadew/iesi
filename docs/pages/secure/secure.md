{% include navigation.html %}

# Secure the framework

Security is important to us, we are working hard on getting it to the necessary level.

## User authentication

The framework allows user authentication when execution automation scripts.

It is possible to activate or deactivate user authentication in the `conf/iesi-default.conf` configuration file. 
The option `iesi.guard.authenticate` is used to control the user authentication:
* set the option to `Y` to enable or to `N` to disable user authentication
* the user authentication requires a valid user to be available when execution a script

## User management