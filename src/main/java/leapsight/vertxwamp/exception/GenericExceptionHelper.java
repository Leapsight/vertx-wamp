package leapsight.vertxwamp.exception;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jawampa.ApplicationError;

public final class GenericExceptionHelper {

    private GenericExceptionHelper() {
    }

    public static <T extends Exception> ApplicationError createMsgFromException(T e, String message) {
        return createMsgFromException(GenericErrorCodes.UNEXPECTED_ERROR, message, e);
    }

    public static <T> ApplicationError createMsgException(String errorCode, String message, String description) {
        final ArrayNode args = new ArrayNode(JsonNodeFactory.instance);
        final ObjectNode kwArgs = new ObjectNode(JsonNodeFactory.instance);

        kwArgs.put("message", message);
        kwArgs.put("description", description);

        return new ApplicationError(errorCode, args, kwArgs);
    }

    public static <T extends Exception> ApplicationError createMsgFromException(String errorCode, String message, T e) {
        final ArrayNode args = new ArrayNode(JsonNodeFactory.instance);
        final ObjectNode kwArgs = new ObjectNode(JsonNodeFactory.instance);

        kwArgs.put("message", message);
        kwArgs.put("description", e.getMessage());
        kwArgs.put("exception", e.getLocalizedMessage());

        return new ApplicationError(errorCode, args, kwArgs);
    }

    public static <T> ApplicationError createMsgException(String errorCode, String message, String description,
                                                          Map<String, T> data) {
        final ArrayNode args = new ArrayNode(JsonNodeFactory.instance);
        final ObjectNode kwArgs = new ObjectNode(JsonNodeFactory.instance);

        kwArgs.put("message", message);
        kwArgs.put("description", description);
        if (data != null)
            data.forEach((key, value) -> {
                ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
                obj.put(key, value.toString());
                args.add(obj);
            });

        return new ApplicationError(errorCode, args, kwArgs);
    }

    public static <T> ApplicationError createMsgException(String errorCode, String message, List<Map<T, T>> data) {
        final ArrayNode args = new ArrayNode(JsonNodeFactory.instance);
        final ObjectNode kwArgs = new ObjectNode(JsonNodeFactory.instance);

        kwArgs.put("message", message);
        if (data != null && data.size() > 0) {
            data.forEach((Map<T, T> map) -> {
                ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
                map.forEach((t, t2) -> {
                    if (t2 != null) {
                        obj.put(t.toString(), t2.toString());
                    }
                });
                args.add(obj);
            });
        }

        return new ApplicationError(errorCode, args, kwArgs);
    }

}