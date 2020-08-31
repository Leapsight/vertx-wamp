package leapsight.vertxwamp.processor;
import static net.logstash.logback.argument.StructuredArguments.v;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.netty.handler.codec.http.HttpResponseStatus;
import jawampa.ApplicationError;
import jawampa.Request;
import jawampa.WampMessages;
import jawampa.WampMessages.ErrorMessage;
import leapsight.vertxwamp.util.LoggingConstants;
import rx.functions.Action1;

/**
 * Our Processors needs to extends from this class
 */
public abstract class AbstractEndPointProcessor  implements Action1<Request> {

    protected static final Logger LOG = LoggerFactory.getLogger(AbstractEndPointProcessor.class);

    protected static final String PROCEDURE_KEY = "procedure";
    protected static final String METHOD_KEY = "method";
    protected static final String PATH_KEY = "path";
    protected static final String ORIGIN_KEY = "origin";

    @Override
    public void call(Request request) {
        final Instant start = Instant.now();

        final String operation = request.details().get(PROCEDURE_KEY).asText();
        final String traceId = request.keywordArguments().get(LoggingConstants.TRACE_ID_KEY).textValue();

        request.keywordArguments().put("startTime", start.toEpochMilli());

        try {
            MDC.put(LoggingConstants.TRACE_ID_KEY, traceId);
            // LOG.info("{} START! Request Arguments: {} - KwArguments: {}", operation, request.arguments(), request.keywordArguments());
            process(request);

        } catch (Exception e) {
            LOG.error("{} ERROR! Exception ocurred", operation);
        } finally {
            // LOG.info("{} END! elapsed time: {}ms", operation, ChronoUnit.MILLIS.between(start, Instant.now()));
            MDC.remove(LoggingConstants.TRACE_ID_KEY);
        }
    }

    private void logJsonInfo(Request request, Object response, Integer statusCode, String reasonPhrase, Object stackTrace) {
        try {
            ObjectNode kwArgs = request.keywordArguments();
            String method = kwArgs.get(METHOD_KEY) != null ? kwArgs.get(METHOD_KEY).textValue() : "";
            String path = kwArgs.get(PATH_KEY) != null ? kwArgs.get(PATH_KEY).textValue() : "";
            String origen = kwArgs.get(ORIGIN_KEY) != null ? kwArgs.get(ORIGIN_KEY).textValue() : "";
            String traceId = kwArgs.get(LoggingConstants.TRACE_ID_KEY) != null ? kwArgs.get(LoggingConstants.TRACE_ID_KEY).textValue() : "";
            String procedure = request.details().get(PROCEDURE_KEY) != null ? request.details().get(PROCEDURE_KEY).asText() : "";

            Instant start = Instant.ofEpochMilli(request.keywordArguments().get("startTime").asLong());
            if(start == null) {
                start = Instant.now();
            }
            Instant end = Instant.now();

            MDC.put(LoggingConstants.TRACE_ID_KEY, traceId);
            LOG.info("json",
                    v(LoggingConstants.METHOD, method),
                    v(LoggingConstants.ORIGIN, origen),
                    v(LoggingConstants.PROCEDURE, procedure),
                    v(LoggingConstants.PATH, path),
                    v(LoggingConstants.STATUS_CODE, statusCode),
                    v(LoggingConstants.REASON_PHRASE, reasonPhrase),
                    v(LoggingConstants.START_DATE, start.atZone(ZoneId.of("America/Buenos_Aires")).toString().replaceAll("[TZ]", " ").substring(0, 22)),
                    v(LoggingConstants.END_DATE, end.atZone(ZoneId.of("America/Buenos_Aires")).toString().replaceAll("[TZ]", " ").substring(0, 22)),
                    v(LoggingConstants.TIME_ELAPSED_IN_MS, ChronoUnit.MILLIS.between(start, end)),
                    v(LoggingConstants.REQUEST, request.arguments()),
                    v(LoggingConstants.RESPONSE, response),
                    v(LoggingConstants.STACK_TRACE, stackTrace),
                    v(LoggingConstants.LOG_TYPE, "jsonLog")
            );
            MDC.remove(LoggingConstants.TRACE_ID_KEY);
        } catch (Exception e) {
            LOG.error("{} Error generating full log: ", e);
        }
    }

    /**
     * Template Method for function registration
     */
    protected abstract void process(Request request);

    protected void replyError(Request request, ApplicationError error) {
        try {
            MDC.put(LoggingConstants.TRACE_ID_KEY, request.keywordArguments().get(LoggingConstants.TRACE_ID_KEY).textValue());
            // LOG.info("{} END! Response Application Error: {}", operation, error.toString());
            request.replyError(error);
        } catch (ApplicationError ae) {
            // LOG.error("{} END! Error replying with application error: ", ae);
        } finally {
            MDC.remove(LoggingConstants.TRACE_ID_KEY);

            ErrorMessage msg = new ErrorMessage(WampMessages.InvocationMessage.ID,
                    request.hashCode(), null, error.uri(), error.arguments(), error.keywordArguments());

            logJsonInfo(request, msg, HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase(), error.getStackTrace());
        }
    }

    protected void reply(Request request, Object... args) {
        try {
            MDC.put(LoggingConstants.TRACE_ID_KEY, request.keywordArguments().get(LoggingConstants.TRACE_ID_KEY).textValue());
            // LOG.debug("{} END! Response Data: {}", operation, args);
            request.reply(args);
        } finally {
            MDC.remove(LoggingConstants.TRACE_ID_KEY);
            logJsonInfo(request, args, HttpResponseStatus.OK.code(), HttpResponseStatus.OK.reasonPhrase(), null);
        }
    }

}
