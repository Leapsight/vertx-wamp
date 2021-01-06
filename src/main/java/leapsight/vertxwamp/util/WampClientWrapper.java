package leapsight.vertxwamp.util;

import jawampa.WampClient;
import jawampa.WampClientBuilder;
import jawampa.auth.client.Ticket;
import jawampa.connection.IWampConnectorProvider;
import jawampa.transport.netty.NettyWampClientConnectorProvider;
import jawampa.transport.netty.NettyWampConnectionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class WampClientWrapper {

    private final String routerUri;

    private final String realm;

    private final String username;

    private final String password;

    private final int reconnectIntervalSeconds;

    private final int maxFramePayloadLength;

    private WampClient wampClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(WampClientWrapper.class);

    public WampClientWrapper(String routerUri, String realm, String username, String password, int reconnectIntervalSeconds, int maxFramePayloadLength) {
        this.realm = realm;
        this.username = username;
        this.password = password;
        this.routerUri = routerUri;
        this.reconnectIntervalSeconds = reconnectIntervalSeconds;
        this.maxFramePayloadLength = maxFramePayloadLength;
        LOGGER.info("Wamp Client Wrapper with connection {} and ticket {}/{} for realm {}, maxFramePayloadLength {}", routerUri, username, password, realm, maxFramePayloadLength);
    }

    public WampClient createWampClient() throws Exception {
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
            return wampClient;
        } catch (Exception ex) {
            LOGGER.error("Error creating connection: ", ex);
            throw ex;
        }
    }

    public void closeConnection() {
        if (wampClient != null) {
            wampClient.close().toBlocking().last();
        }
    }
}
