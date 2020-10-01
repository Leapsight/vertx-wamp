package leapsight.vertxwamp.exception;

/**
 * The generic error codes
 */
public interface GenericErrorCodes {

	public final static String MISSING_REQUIRED_PARAMETERS = "com.magenta.error.missing_required_parameters";
	public final static String UNEXPECTED_ERROR = "com.magenta.error.unexpected_error";
	public final static String INVALID_PARAMETERS = "com.magenta.error.invalid_parameters";
	public final static String NO_DATA_FOUND = "com.magenta.error.no_data_found";
	public final static String UNIMPLEMENTED_SERVICE = "com.magenta.error.unimplemented_service";
	public final static String CONNECTION_FAILURE = "com.magenta.error.connection_failure";
	public final static String BAD_REQUEST = "com.magenta.error.bad_request";
	public final static String PROCESSING_DATA_ERROR = "com.magenta.error.processing_data_error";
	public final static String INVALID_CREDENTIAL = "com.magenta.error.invalid_credential";

}