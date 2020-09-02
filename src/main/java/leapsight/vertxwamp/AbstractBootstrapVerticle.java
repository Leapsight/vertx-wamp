package leapsight.vertxwamp;

import java.util.HashMap;
import java.util.Map;

import leapsight.vertxwamp.verticlefactory.SpringConfigurationLib;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.spi.VerticleFactory;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxPrometheusOptions;
import leapsight.vertxwamp.verticlefactory.SpringVerticleFactory;
import org.springframework.core.env.Environment;

/**
 * El ExampleBootstrapVerticle de cada Microservicio, deberá extender esta clase que se encarga de inicializar Vertx
 */
public abstract class AbstractBootstrapVerticle {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBootstrapVerticle.class);
	/**
	 * Lista donde se guardan los verticles del microservicio que se desea implementar
	 */
	//List<String> verticleList;

	public void start() {

		ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfigurationLib.class,theSpringConfigClass());

		Environment env = context.getBean(Environment.class);
		Integer metricsPort = Integer.valueOf(env.getProperty("metrics.port"));
		LOGGER.info("metrics.port: {}", metricsPort);

		Vertx vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(
				new MicrometerMetricsOptions().setPrometheusOptions(new VertxPrometheusOptions().setEnabled(true)
						.setStartEmbeddedServer(true).setEmbeddedServerOptions(new HttpServerOptions().setPort(metricsPort))
						.setEmbeddedServerEndpoint("/metrics")).setEnabled(true)));

		VerticleFactory verticleFactory = context.getBean(SpringVerticleFactory.class);

		preInitialize (vertx, context);
		// The verticle factory is registered manually because it is created by the Spring container
		vertx.registerVerticleFactory(verticleFactory);

		Map<String, DeploymentOptions> verticleOptionsMap = new HashMap<String, DeploymentOptions>();
		setYourVerticleDeploymentOptions(verticleOptionsMap);
		verticleOptionsMap.forEach((verticleName,option) -> {
			vertx.deployVerticle(verticleFactory.prefix() + ":" + verticleName, option);});

		postInitialize(vertx, context);
	}

	/**
	 * Create an Map<String, DeploymentOptions> with the classname of your Verticles and deployment options. Example:
	 * myVerticleMap.put(ExampleWampVerticle.class.getName(), new DeploymentOptions().setInstances(1));
	 *
	 * @return Map with the names of the Verticles and the deployment option what you want to initialize with
	 *         the microservice.
	 */
	protected abstract void setYourVerticleDeploymentOptions(Map<String, DeploymentOptions> verticleOptionsMap);

	protected abstract void preInitialize(Vertx vertx, ApplicationContext context);

	protected abstract void postInitialize(Vertx vertx, ApplicationContext context);

	/**
	 * Example: return ExampleSpringConfiguration.class;
	 * 
	 * @return the .class of you own implementation of SpringConfiguration
	 */
	protected abstract Class<?> theSpringConfigClass();

}