package leapsight.vertxwamp.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;


@Scope(SCOPE_PROTOTYPE)
public abstract class AbstractHealthVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractHealthVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    addRouterHandler(router);

    vertx.createHttpServer().requestHandler( router ).listen(9198, ar -> {
      if (ar.succeeded()) {
        LOG.info("HealthVerticle started: @" + this.hashCode());
        startPromise.complete();
      } else {
        LOG.info("HealthVerticle do not started: @" + this.hashCode());
        startPromise.fail(ar.cause());
      }
    });
  }

  public abstract void addRouterHandler(Router router);

}
