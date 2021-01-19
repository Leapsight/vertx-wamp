# vertx-wamp

A library for Vertx-Wamp micro-services

This library requires the following environment variables to be defined:  
**metrics.port**: This port is for manage metrics.\
**wamp.router.uri**: Defines the URI of the router.\
**wamp.realm**: Defines the name of the realm to get the client.\
**wamp.username**: Username to get the connection to the router.\
**wamp.password**: Password to get the connection to the router.\
**wamp.reconnect.interval.seconds**: Defines the reconnection interval in seconds towards the router.\
**wamp.max.frame.payload.length**: Defines the maximum length allowed for the payload. By default it is 65535.

### For example:
**metrics.port** = *${METRICS_PORT}*\
**wamp.router.uri** = *${WAMP_URI}*\
**wamp.realm** = *${WAMP_REALM}*\
**wamp.username** = *${WAMP_USERNAME}*\
**wamp.password** = *${WAMP_PASSWORD}*\
**wamp.reconnect.interval.seconds** = *${WAMP_RECONNECT_INTERVAL}*\
**wamp.max.frame.payload.length** = *${WAMP_MAX_FRAME_PAYLOAD_LENGTH}*