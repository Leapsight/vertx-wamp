package leapsight.vertxwamp;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.spi.VerticleFactory;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxPrometheusOptions;
import io.vertx.micrometer.backends.BackendRegistries;
import leapsight.vertxwamp.codec.WampClientCodec;
import leapsight.vertxwamp.verticle.AbstractWampVerticle;
import leapsight.vertxwamp.verticle.WampVerticle;
import leapsight.vertxwamp.verticlefactory.SpringConfigurationLib;
import leapsight.vertxwamp.verticlefactory.SpringVerticleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * El ExampleBootstrapVerticle de cada Microservicio, deberá extender esta clase que se encarga de inicializar Vertx
 */
public abstract class AbstractBootstrapVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBootstrapVerticle.class);

    protected Set<String> setWampDeplomentId;

    protected Map<String, DeploymentOptions> wampVerticleOptionsMap;

    public void start() {
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfigurationLib.class, theSpringConfigClass());

        Environment env = context.getBean(Environment.class);
        Integer metricsPort = Integer.valueOf(env.getProperty("metrics.port"));
        LOGGER.info("metrics.port: {}", metricsPort);

        Vertx vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(
                new MicrometerMetricsOptions()
                        .setPrometheusOptions(new VertxPrometheusOptions().setEnabled(true)
                                .setStartEmbeddedServer(true)
                                .setEmbeddedServerOptions(new HttpServerOptions().setPort(metricsPort))
                                .setEmbeddedServerEndpoint("/metrics")
                        ).setRegistryName("jvm-metrics").setEnabled(true)));

        WampClientCodec codec = new WampClientCodec();
        vertx.eventBus().registerCodec(codec);

        MeterRegistry registry = BackendRegistries.getNow("jvm-metrics");
        new JvmMemoryMetrics().bindTo(registry);
        new ProcessorMetrics().bindTo(registry);
        new JvmThreadMetrics().bindTo(registry);

        VerticleFactory verticleFactory = context.getBean(SpringVerticleFactory.class);

        preInitialize(vertx, context);
        vertx.registerVerticleFactory(verticleFactory); // The verticle factory is registered manually because it is created by the Spring container
        vertx.deployVerticle(verticleFactory.prefix() + ":" + WampVerticle.class.getName(), new DeploymentOptions().setInstances(1), deploymentId -> {
            preInitialize(vertx, context);

            setWampDeplomentId = new HashSet<>();
            wampVerticleOptionsMap = new HashMap<>();
            Map<String, DeploymentOptions> verticleOptionsMap = new HashMap<String, DeploymentOptions>();
            setYourVerticleDeploymentOptions(verticleOptionsMap);
            verticleOptionsMap.forEach((verticleName, option) -> {
                vertx.deployVerticle(verticleFactory.prefix() + ":" + verticleName, option, deplomentId -> {
                    try {
                        if (Class.forName(verticleName).getSuperclass() == AbstractWampVerticle.class) {
                            setWampDeplomentId.add(deplomentId.result());
                            wampVerticleOptionsMap.put(verticleName, option);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });
            });
        });

        postInitialize(vertx, context);
    }

    /**
     * Create an Map<String, DeploymentOptions> with the classname of your Verticles and deployment options. Example:
     * myVerticleMap.put(ExampleWampVerticle.class.getName(), new DeploymentOptions().setInstances(1));
     *
     * @return Map with the names of the Verticles and the deployment option what you want to initialize with
     * the microservice.
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