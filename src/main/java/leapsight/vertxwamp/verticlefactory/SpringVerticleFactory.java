package leapsight.vertxwamp.verticlefactory;

import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.spi.VerticleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

@Component
public class SpringVerticleFactory implements VerticleFactory, ApplicationContextAware {

  private ApplicationContext applicationContext;
  private static final Logger LOGGER = LoggerFactory.getLogger(SpringVerticleFactory.class);

  @Override
  public String prefix() {
    return "ls-vertxwamp";
  }

  @Override
  public void createVerticle(String verticleName, ClassLoader classLoader, Promise<Callable<Verticle>> promise) {
    String clazz = VerticleFactory.removePrefix(verticleName);
    try {
      LOGGER.info("load {}", clazz);
      promise.complete(() -> (Verticle) applicationContext.getBean(Class.forName(clazz)));
    } catch (Exception e) {
      promise.fail(e);
      LOGGER.error("fail {}", clazz, e.fillInStackTrace());
    }
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
