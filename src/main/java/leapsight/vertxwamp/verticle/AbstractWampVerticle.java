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

    protected static Logger LOG = LoggerFactory.getLogger(AbstractWampVerticle.class);

    @Autowired
    private WampClient wampClient;

    /**
     * Template Method
     */
    protected abstract void registerProcedures(final WampClient client);

    protected abstract void callProcedures(final WampClient client);
    /**
     * If your verticle does a simple, synchronous start-up then override this method and put your start-up
     * code in here.
     * @throws Exception
     */
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        try {
        	LOG.info("==================================================================================================");
        	LOG.info("Starting WampVerticle ...");

            registerProcedures(wampClient); // implementacion propia de cada microservicio

            callProcedures(wampClient); // implementacion propia de cada microservicio

            LOG.info("WampVerticle started: @" + this.hashCode());
            
            startPromise.complete();
            
        } catch (Exception e) {
            LOG.error("WAMP client initialization failed", e);
            startPromise.fail("WAMP client initialization failed"); // avisa que falló, despues en el bootstrap hay que ver que hacer con este fallo (reintentar u otra cosa)
        }

    }

    /**
     * If your verticle has simple synchronous clean-up tasks to complete then override this method and put your clean-up
     * code in here.
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
    	closeWampClient();
    }
    
    protected void closeWampClient() {
    	if (wampClient != null)	{
    		LOG.info("Closing the WAMP client ...");	
    		wampClient.close().toBlocking().last();
    		LOG.info("WAMP client closed");
    	}
    }
}