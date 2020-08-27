package leapsight.vertxwamp;

import java.util.Map;

import leapsight.vertxwamp.verticlefactory.SpringConfigurationLib;
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

/**
 * El ExampleBootstrapVerticle de cada Microservicio, deberá extender esta clase que se encarga de inicializar Vertx
 */
public abstract class AbstractBootstrapVerticle {

	/**
	 * Lista donde se guardan los verticles del microservicio que se desea implementar
	 */
	//List<String> verticleList;
	Map<String, DeploymentOptions> verticleDeploymentOptions;
	Vertx vertx;
	
	private static final Integer PORT = 9194;

	public void start() {

		vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(
				new MicrometerMetricsOptions().setPrometheusOptions(new VertxPrometheusOptions().setEnabled(true)
						.setStartEmbeddedServer(true).setEmbeddedServerOptions(new HttpServerOptions().setPort(PORT))
						.setEmbeddedServerEndpoint("/metrics")).setEnabled(true)));
	
		ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfigurationLib.class,theSpringConfigClass());
		VerticleFactory verticleFactory = context.getBean(SpringVerticleFactory.class);

		// The verticle factory is registered manually because it is created by the Spring container
		vertx.registerVerticleFactory(verticleFactory);

		setVerticleDeploymentOptions(setYourVerticleDeploymentOptions());
		verticleDeploymentOptions.forEach((verticleName,option) -> {
			vertx.deployVerticle(verticleFactory.prefix() + ":" + verticleName, option);});
			}

	/**
	 * Create an Map<String, DeploymentOptions> with the classname of your Verticles and deployment options. Example:
	 * myVerticleMap.put(ExampleWampVerticle.class.getName(), new DeploymentOptions().setInstances(1));
	 *
	 * @return Map with the names of the Verticles and the deployment option what you want to initialize with
	 *         the microservice.
	 */
	protected abstract Map<String, DeploymentOptions> setYourVerticleDeploymentOptions();

	/**
	 * Example: return ExampleSpringConfiguration.class;
	 * 
	 * @return the .class of you own implementation of SpringConfiguration
	 */
	protected abstract Class<?> theSpringConfigClass();


	public Map<String, DeploymentOptions> getVerticleDeploymentOptions() {
		return verticleDeploymentOptions;
	}

	public void setVerticleDeploymentOptions(Map<String, DeploymentOptions> verticleDeploymentOptions) {
		this.verticleDeploymentOptions = verticleDeploymentOptions;
	}
}