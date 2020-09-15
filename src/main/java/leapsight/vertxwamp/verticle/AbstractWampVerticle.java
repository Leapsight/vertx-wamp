package leapsight.vertxwamp.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import jawampa.WampClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;


/**
 * Our own AbstractWampVerticle needs to extends from this class
 */

@Scope(SCOPE_PROTOTYPE)
public abstract class AbstractWampVerticle extends AbstractVerticle {

    protected static Logger LOGGER = LoggerFactory.getLogger(AbstractWampVerticle.class);

    @Autowired
    private WampClient wampClient;

    /**
     * Template Method
     */
    protected abstract void registerProcedures(final WampClient client);
    /**
     * If your verticle does a simple, synchronous start-up then override this method and put your start-up
     * code in here.
     * @throws Exception
     */
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        try {
        	LOGGER.info("==================================================================================================");
        	LOGGER.info("Starting WampVerticle ...");

            registerProcedures(wampClient); // implementacion propia de cada microservicio

            LOGGER.info("WampVerticle started: @" + this.hashCode());
            startPromise.complete();
            
        } catch (Exception e) {
        	LOGGER.error("WampVerticle initialization failed", e);
            startPromise.fail("WampVerticle initialization failed"); // avisa que falló, despues en el bootstrap hay que ver que hacer con este fallo (reintentar u otra cosa)
        }

    }

    /**
     * If your verticle has simple synchronous clean-up tasks to complete then override this method and put your clean-up
     * code in here.
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
    	LOGGER.info("==================================================================================================");
    	LOGGER.info("Stopping WampVerticle ...");
    	closeWampClient();
    	LOGGER.info("WampVerticle stopped: @" + this.hashCode());
    }
    
    protected void closeWampClient() {
    	if (wampClient != null)	{
    		LOGGER.info("Closing the WAMP client ...");	
    		wampClient.close().toBlocking().last();
    		LOGGER.info("WAMP client closed");
    	}
    }
}