package leapsight.vertxwamp.verticle;

import io.vertx.core.AbstractVerticle;
import jawampa.WampClient;
import jawampa.WampClientBuilder;
import jawampa.auth.client.Ticket;
import jawampa.connection.IWampConnectorProvider;
import jawampa.transport.netty.NettyWampClientConnectorProvider;
import jawampa.transport.netty.NettyWampConnectionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class WampVerticle extends AbstractVerticle {

    @Value("${wamp.router.uri}")
    private String routerUri;

    @Value("${wamp.realm}")
    private String realm;

    @Value("${wamp.username}")
    private String username;

    @Value("${wamp.password}")
    private String password;

    @Value("${wamp.reconnect.interval.seconds}")
    private int reconnectIntervalSeconds;

    @Value("${wamp.max.frame.payload.length:65535}")
    private int maxFramePayloadLength;

    private WampClient wampClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(WampVerticle.class);

    public void start() throws Exception {
        LOGGER.info("Starting WampVerticle...");

        IWampConnectorProvider connectorProvider = new NettyWampClientConnectorProvider();
        WampClientBuilder builder = new WampClientBuilder();
        try {
            // Build wamp client
            builder.withConnectorProvider(connectorProvider)
                    .withUri(routerUri)
                    .withRealm(realm)
                    .withAuthId(username)
                    .withAuthMethod(new Ticket(password))
                    .withInfiniteReconnects()
                    .withReconnectInterval(reconnectIntervalSeconds, TimeUnit.SECONDS)
                    .withConnectionConfiguration((new NettyWampConnectionConfig.Builder()).withMaxFramePayloadLength(maxFramePayloadLength).build());
            wampClient = builder.build();
            wampClient.open();
            LOGGER.info("Connection established... WampClient: {}", wampClient.hashCode());
        } catch (Exception ex) {
            LOGGER.error("Error creating connection: ", ex);
            throw ex;
        }

        vertx.eventBus().consumer("get.wamp.connection", message -> {
            message.reply(wampClient);
        });


    }

}
