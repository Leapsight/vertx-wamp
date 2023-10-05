package leapsight.vertxwamp.util;

public interface LoggingConstants {

	public final static String WAMP_PROCEDURE_KEY = "procedure";

	/*Some WAMP messages contain Options|dict or Details|dict elements.
   This allows for future extensibility and implementations that only
   provide subsets of functionality by ignoring unimplemented
   attributes.  Keys in Options and Details MUST be of type string and
   MUST match the regular expression [a-z][a-z0-9_]{2,} for WAMP
   predefined keys.  Implementations MAY use implementation-specific
   keys that MUST match the regular expression _[a-z0-9_]{3,}.
   Attributes unknown to an implementation MUST be ignored.*/
	public final static String WAMP_START_TIME_KEY = "_wamp_start_time";

	public final static String PROCEDURE_KEY = "procedure";
	
	public final static String START_DATE_KEY = "start_date";

	public final static String END_DATE_KEY = "end_date";

	public final static String TIME_ELAPSED_IN_MS_KEY = "time_elapsed_in_ms";

	public final static String REQUEST_ARGS_KEY = "request_args";

	public final static String REQUEST_KWARGS_KEY = "request_kwargs";

	public final static String RESPONSE_KEY = "response";

	public final static String LOG_TYPE_KEY = "log_type";

}
