package leapsight.vertxwamp.verticlefactory;

import leapsight.vertxwamp.util.WampClientWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("leapsight.vertxwamp.verticlefactory")
public class SpringConfigurationLib {
    @Value("${wamp.router.uri}")
    protected String routerUri;

    @Value("${wamp.realm}")
    protected String realm;

    @Value("${wamp.username}")
    protected String username;

    @Value("${wamp.password}")
    protected String password;

    @Value("${wamp.reconnect.interval.seconds}")
    protected int reconnectIntervalSeconds;

    @Bean("wampClientFactory")
    public WampClientWrapper wampClientWrapper() {
        return new WampClientWrapper(routerUri, realm, username, password, reconnectIntervalSeconds);
    }
}
