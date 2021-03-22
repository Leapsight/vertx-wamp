package leapsight.vertxwamp.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import jawampa.WampClient;
import leapsight.vertxwamp.codec.WampClientCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;


/**
 * Our own AbstractWampVerticle needs to extends from this class
 */

@Scope(SCOPE_PROTOTYPE)
public abstract class AbstractWampVerticle extends AbstractVerticle {

    protected static Logger LOGGER = LoggerFactory.getLogger(AbstractWampVerticle.class);

    protected WampClient wampClient;

    /**
     * Template Method
     */
    protected abstract void registerProcedures(final WampClient client);

    /**
     * If your verticle does a simple, synchronous start-up then override this method and put your start-up
     * code in here.
     */
    @Override
    public void start(Promise<Void> startPromise) {
        LOGGER.info("Starting {}...", this.getClass().getSimpleName());

        WampClientCodec codec = new WampClientCodec();
        DeliveryOptions options = new DeliveryOptions().setCodecName(codec.name());
        vertx.eventBus().request("get.wamp.connection", wampClient, options, ar -> {
            if (ar.succeeded()) {
                wampClient = (WampClient) ar.result().body();
                registerProcedures(wampClient); // implementacion propia de cada microservicio

                startPromise.complete();
            } else {
                LOGGER.error(ar.cause().getMessage());
                startPromise.fail("WampVerticle initialization failed");
            }
        });
    }

    /**
     * If your verticle has simple synchronous clean-up tasks to complete then override this method and put your clean-up
     * code in here.
     */
    @Override
    public void stop() {
        LOGGER.info("{} stopped: {}", this.getClass().getSimpleName(), this.hashCode());
    }
}