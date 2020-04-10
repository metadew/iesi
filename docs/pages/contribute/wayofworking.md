{% include navigation.html %}

# Way of working for contributions

## Branching strategy

The way of working is organised according to the the git branching strategy described [here](https://nvie.com/posts/a-successful-git-branching-model/).

![branching-strategy](https://nvie.com/img/git-model@2x.png)

## Pre-requisites

* Install Hub - command line tool for git > [link](https://github.com/github/hub)

## Register a new feature or bug

New features or bugs are submitted as issues in the [Github](https://github.com/metadew/iesi) repository [here](https://github.com/metadew/iesi/issues). Each issues is detailed as needed and provided with an ID at the end of the description. This ID can be generated from [sha1-online.com](http://www.sha1-online.com/) and added to the descripion.

> ID: the first 7 digits of the sha1 hash generated from the issue title

## Get the code to start working

To start working, always branch of `develop`. Make sure to checkout and refresh the branch!

Next, create a branch for the issue - feature or bug using the following naming convention:
* feature: feature/[id]/[short-title]
* bug: bug/[id]/[short-title]

*Do avoid blank spaces in the short title!*


## Do your magic

This is where you be the best you can be and do your magic.

## Create a pull request

Once finished, create a pull request from the feature/bug branch into the develop branch using Hub.

```
hub pull-request -b develop -i [issue-number]
```

The `-b develop` option ensures that the pull request is performed against the `develop` branch instead of the default `master` branch.

Finally, follow-up on the review of the pull request and provide any feedback / updates as needed.
