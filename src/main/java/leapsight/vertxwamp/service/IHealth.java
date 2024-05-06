package leapsight.vertxwamp.service;

import io.vertx.ext.web.RoutingContext;

/**
 * To know if the microservice is alive or not
 */
public interface IHealth {

    public void ready(RoutingContext routingContext);
    public void live(RoutingContext routingContext);
    
}