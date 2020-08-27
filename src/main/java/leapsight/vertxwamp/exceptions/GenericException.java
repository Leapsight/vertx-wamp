package leapsight.vertxwamp.exceptions;

import java.util.Map;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jawampa.ApplicationError;

public class GenericException {

	public <T extends Exception> ApplicationError createMsgFromException(T e, String message) {
		return createMsgFromException(GenericErrorCodes.UNEXPECTED_ERROR, message, e);
	}

	public <T extends Exception> ApplicationError createMsgFromException(String errorCode, String message, T e) {
		final ArrayNode args = new ArrayNode(JsonNodeFactory.instance);
		final ObjectNode kwArgs = new ObjectNode(JsonNodeFactory.instance);

		args.add(message);
		args.add(e.getMessage());
		kwArgs.put("exception", e.getLocalizedMessage());

		return new ApplicationError(errorCode, args, kwArgs);
	}

	public <T> ApplicationError createMsgException(String errorCode, String message, String description, Map<String, T> data) {
		final ArrayNode args = new ArrayNode(JsonNodeFactory.instance);
		final ObjectNode kwArgs = new ObjectNode(JsonNodeFactory.instance);
		
		args.add(message);
		args.add(description);
		if(data != null)
			data.entrySet().forEach(map -> kwArgs.put(map.getKey(), map.getValue().toString()));
		
		return new ApplicationError(errorCode, args, kwArgs);
	}

}