package leapsight.vertxwamp.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import jawampa.WampClient;
import jawampa.WampClientBuilder;
import jawampa.auth.client.ClientSideAuthentication;
import jawampa.auth.client.Password;
import jawampa.auth.client.Ticket;
import jawampa.auth.client.CryptosignAuth;
import jawampa.connection.IWampConnectorProvider;
import jawampa.transport.netty.NettyWampClientConnectorProvider;
import jawampa.transport.netty.NettyWampConnectionConfig;
import leapsight.vertxwamp.codec.WampClientCodec;
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

    @Value("${wamp.auth.method:password}")
    private String authMethod;

    @Value("${wamp.username}")
    private String username;

    @Value("${wamp.password:null}")
    private String password;

    @Value("${wamp.pubkey:null}")
    private String pubkey;

    @Value("${wamp.privkey:null}")
    private String privkey;

    @Value("${wamp.reconnect.interval.seconds}")
    private int reconnectIntervalSeconds;

    @Value("${wamp.max.frame.payload.length:65535}")
    private int maxFramePayloadLength;

    private WampClient wampClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(WampVerticle.class);

    public void start() throws Exception {
        LOGGER.info("Starting WampVerticle...");

        final ClientSideAuthentication authMethodObj;
        switch (authMethod) {
            case "password":
                authMethodObj = new Password(password);
                break;
            case "ticket":
                authMethodObj = new Ticket(password);
                break;
            case "cryptosign":
                authMethodObj = new CryptosignAuth(privkey, pubkey);
                break;
            default:
                authMethodObj = null;
        }

        IWampConnectorProvider connectorProvider = new NettyWampClientConnectorProvider();
        WampClientBuilder builder = new WampClientBuilder();
        try {
            // Build wamp client
            builder.withConnectorProvider(connectorProvider)
                    .withUri(routerUri)
                    .withRealm(realm)
                    .withAuthId(username)
                    .withAuthMethod(authMethodObj)
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

        DeliveryOptions options = new DeliveryOptions().setCodecName(new WampClientCodec().name());
        vertx.eventBus().consumer("get.wamp.connection", message -> {
            message.reply(wampClient, options);
        });

        vertx.eventBus().consumer("close.wamp.connection", message -> {
            vertx.close(ar -> {
                System.exit(-1);
            });
        });
    }
}
