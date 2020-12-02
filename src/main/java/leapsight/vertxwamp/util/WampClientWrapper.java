package leapsight.vertxwamp.util;

import jawampa.WampClient;
import jawampa.WampClientBuilder;
import jawampa.auth.client.Ticket;
import jawampa.connection.IWampConnectorProvider;
import jawampa.transport.netty.NettyWampClientConnectorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class WampClientWrapper {

    private final String routerUri;

    private final String realm;

    private final String username;

    private final String password;

    private final int reconnectIntervalSeconds;

    private WampClient wampClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(WampClientWrapper.class);

    public WampClientWrapper(String routerUri, String realm, String username, String password, int reconnectIntervalSeconds) {
        this.realm = realm;
        this.username = username;
        this.password = password;
        this.routerUri = routerUri;
        this.reconnectIntervalSeconds = reconnectIntervalSeconds;
        LOGGER.info("Wamp Client Wrapper with connection {} and ticket {}/{} for realm {}", routerUri, username, password, realm);
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
                    .withReconnectInterval(reconnectIntervalSeconds, TimeUnit.SECONDS);

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
