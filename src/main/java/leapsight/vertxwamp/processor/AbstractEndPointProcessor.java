package leapsight.vertxwamp.processor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import jawampa.Request;
import leapsight.vertxwamp.util.LoggingConstants;
import rx.functions.Action2;

/**
 * Our Processors needs to extends from this class
 */
public abstract class AbstractEndPointProcessor implements Action2<Request, Vertx> {

	protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractEndPointProcessor.class);

	@Override
	public void call(Request request, Vertx vertx) {
		final Instant start = Instant.now();
		final String procedure = request.details().get(LoggingConstants.WAMP_PROCEDURE_KEY) != null
				? request.details().get(LoggingConstants.WAMP_PROCEDURE_KEY).asText()
				: "undefined";
		request.keywordArguments().put(LoggingConstants.WAMP_START_TIME_KEY, start.toEpochMilli());

		try {
			LOGGER.info("{} START! Request Arguments: {} - KwArguments: {}", procedure, request.arguments(),
					request.keywordArguments());
			process(request, vertx);
		} catch (Exception e) {
			LOGGER.error("{} ERROR! Exception ocurred", procedure);
		} finally {
			LOGGER.info("{} END! elapsed time: {}ms", procedure, ChronoUnit.MILLIS.between(start, Instant.now()));
		}
	}

	/**
	 * Template Method for function registration
	 */
	protected abstract void process(Request request, Vertx vertx);

}
