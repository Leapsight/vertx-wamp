# vertx-wamp

A library for Vertx-Wamp micro-services

This library requires the following environment variables to be defined:

**metrics.port**: This port is for manage metrics.\
**wamp.router.uri**: Defines the URI of the router.\
**wamp.realm**: Defines the name of the realm to get the client.\
**wamp.username**: Username to get the connection to the router.\
**wamp.auth.method**: The authentication method. There are 3 possible values: `password`, `ticket` or `cryptosign`. The default is `password`.\
**wamp.reconnect.interval.seconds**: Defines the reconnection interval in seconds towards the router.\
**wamp.max.frame.payload.length**: Defines the maximum length allowed for the payload. By default it is 65535.

Related with the authentication method:

**wamp.password**: Password to get the connection to the router when the **wamp.auth.method** is `password` or `ticket`.\
**wamp.pubkey**: Public key to get the connection to the router when the **wamp.auth.method** is `cryptosign`.\
**wamp.privkey**: Private key to get the connection to the router when the **wamp.auth.method** is `cryptosign`.\

### For example:

**metrics.port** = *${METRICS_PORT}*\
**wamp.router.uri** = *${WAMP_URI}*\
**wamp.realm** = *${WAMP_REALM}*\
**wamp.auth.method** = *${WAMP_AUTH_METHOD}*\
**wamp.username** = *${WAMP_USERNAME}*\
**wamp.password** = *${WAMP_PASSWORD}*\
**wamp.reconnect.interval.seconds** = *${WAMP_RECONNECT_INTERVAL}*\
**wamp.max.frame.payload.length** = *${WAMP_MAX_FRAME_PAYLOAD_LENGTH}*