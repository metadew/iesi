{% include navigation.html %}

# Action Configuration

Certain actions allow for a global configuration. These configurations are defined under the Action level.

## socket.transmitMessage

When transmitting a message over a socket it is possible to wait for a reply. How long to wait for a reply is defined by the `actions.socket.transmitMessage.timeout.default` key. This parameter defines the duration threshold (in seconds) to wait for a reply.

```yaml
iesi:
  actions:
    ...
    socket.transmitMessage:
      timeout:
        default: 2
    ...
```
