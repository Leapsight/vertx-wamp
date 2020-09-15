package leapsight.vertxwamp.util;

import static net.logstash.logback.argument.StructuredArguments.v;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jawampa.ApplicationError;
import jawampa.Request;

public final class ReplyHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReplyHelper.class);

	private static final String LOG_TYPE_VALUE = "jsonLog";

	private ReplyHelper() {
	}

	public static void replyError(Request request, ApplicationError error) {
		try {
			request.replyError(error);
		} catch (ApplicationError ae) {
			LOGGER.error("{} Error replying with application error: ", ae);
		} finally {
			logJsonInfo(request, error);
		}
	}

	public static void reply(Request request, Object... args) {
		try {
			request.reply(args);
		} finally {
			logJsonInfo(request, args);
		}
	}

	private static void logJsonInfo(Request request, Object response) {
		try {
			final String procedure = request.details().get(LoggingConstants.WAMP_PROCEDURE_KEY) != null
					? request.details().get(LoggingConstants.WAMP_PROCEDURE_KEY).asText()
					: "";
			// pick the start time of operation (request received)
			Instant start = Instant
					.ofEpochMilli(request.keywordArguments().get(LoggingConstants.WAMP_START_TIME_KEY).asLong());
			if (start == null) {
				start = Instant.now();
			}
			final Instant end = Instant.now();

			LOGGER.info("json", v(LoggingConstants.PROCEDURE_KEY, procedure), v(LoggingConstants.START_DATE_KEY, start),
					v(LoggingConstants.END_DATE_KEY, end),
					v(LoggingConstants.TIME_ELAPSED_IN_MS_KEY, ChronoUnit.MILLIS.between(start, end)),
					v(LoggingConstants.REQUEST_ARGS_KEY, request.arguments()),
					v(LoggingConstants.REQUEST_KWARGS_KEY, request.keywordArguments()),
					v(LoggingConstants.RESPONSE_KEY, response), v(LoggingConstants.LOG_TYPE_KEY, LOG_TYPE_VALUE));

		} catch (Exception e) {
			LOGGER.error("{} Error generating json log", e);
		}
	}
}
