package leapsight.vertxwamp.verticlefactory;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("leapsight.vertxwamp.verticle")
@ComponentScan("leapsight.vertxwamp.verticlefactory")
public class SpringConfigurationLib {

   @Bean
   public MeterRegistry prometheusMeter() {
      return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
   }

}
