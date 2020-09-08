package leapsight.vertxwamp.verticlefactory;

import jawampa.WampClient;
import jawampa.WampClientBuilder;
import jawampa.auth.client.Ticket;
import jawampa.connection.IWampConnectorProvider;
import jawampa.transport.netty.NettyWampClientConnectorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@ComponentScan("leapsight.vertxwamp.verticlefactory")
public class SpringConfigurationLib {
    @Value("${wamp.router.uri}")
    protected String routerUri;

    @Value("${wamp.realm}")
    protected String realm;

    @Value("${wamp.username}")
    protected String username;

    @Value("${wamp.password}")
    protected String password;

    @Value("${wamp.reconnect.interval.seconds}")
    protected int reconnectIntervalSeconds;

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringConfigurationLib.class);

    @Bean(name = "wampClient")
    public WampClient wampClient() throws Exception {

        IWampConnectorProvider connectorProvider = new NettyWampClientConnectorProvider();
        WampClientBuilder builder = new WampClientBuilder();
        try {
            LOGGER.info("Build Wamp Client with connection {} and ticket {}/{} for realm {}", routerUri, username, password, realm);
            // Build wamp client
            builder.withConnectorProvider(connectorProvider)
                    .withUri(routerUri)
                    .withRealm(realm)
                    .withAuthId(username)
                    .withAuthMethod(new Ticket(password))
                    .withInfiniteReconnects()
                    .withReconnectInterval(reconnectIntervalSeconds, TimeUnit.SECONDS);
            WampClient wampClient = builder.build();
            // Open wamp connection
            wampClient.open();
            return wampClient;
        } catch (Exception e) {
            LOGGER.error("Can not build wamp client", e.fillInStackTrace());
            throw e;
        }
    }
}
