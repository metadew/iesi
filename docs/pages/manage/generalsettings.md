{% include navigation.html %}

# General settings

The metadata repository is configured in the `application-repository.yml` file. This file defines the database(s) to use to store all the metadata

## Concepts
* home: 
* guard: 
* server:

## Home


```yaml
iesi:
  home: C:/Users/robbe.berrevoets/IESISandbox/0.4.0/b1
  ...
```

## Guard


```yaml
iesi:
  ...
  guard:
    authenticate: N
  ...
```
## Server

```yaml
iesi:
  ...
  server:
    mode: standalone
    threads:
      timeout: 30
  ...
```
