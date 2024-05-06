package leapsight.vertxwamp.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import jawampa.WampClient;
import leapsight.vertxwamp.codec.WampClientCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import rx.functions.Action0;
import rx.functions.Action1;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;


@Scope(SCOPE_PROTOTYPE)
public abstract class AbstractHealthVerticle extends AbstractVerticle {

  protected WampClient wampClient;

  private WampClient.State state;
  private static final Logger LOG = LoggerFactory.getLogger(AbstractHealthVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    WampClientCodec codec = new WampClientCodec();
    DeliveryOptions options = new DeliveryOptions().setCodecName(codec.name());
    vertx.eventBus().request("get.wamp.connection", wampClient, options, ar -> {
      if (ar.succeeded()) {
        wampClient = (WampClient) ar.result().body();
        registerProcedures(wampClient); // implementacion propia de cada microservicio

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        addRouterHandler(router);

        vertx.createHttpServer().requestHandler( router ).listen(9198, res -> {
          if (res.succeeded()) {
            LOG.info("HealthVerticle started: @" + this.hashCode());
            startPromise.complete();
          } else {
            LOG.info("HealthVerticle do not started: @" + this.hashCode());
            startPromise.fail(ar.cause());
          }
        });

      } else {
        LOG.error(ar.cause().getMessage());
        startPromise.fail("WampVerticle initialization failed");
      }
    });

  }

  public abstract void addRouterHandler(Router router);

  public void registerProcedures(WampClient client) {
    LOG.info("HEALTH CHECK: START");
    client.statusChanged().subscribe(new Action1<WampClient.State>() {
      @Override
      public void call(WampClient.State t1) {
        LOG.info("HealthSession status changed to " + t1);
        state = t1;
      }
    }, new Action1<Throwable>() {
      @Override
      public void call(Throwable t) {
        try {
          LOG.info("HEALTH CHECK ended with error " + t);
          stop();
          Promise<Void> startPromise = Promise.promise();
          start(startPromise);
        } catch (Exception e) {
          LOG.info("HEALTH CHECK can't reconnect");
        }
      }
    }, new Action0() {
      @Override
      public void call() {
        LOG.info("HEALTH CHECK ended normally");
        try {
          stop();
        } catch (Exception e) {
          LOG.info("HealthSession can't stop");
        }
      }
    });
  }

  public WampClient.State getState() {
    return state;
  }

  public void setState(WampClient.State state) {
    this.state = state;
  }
}
