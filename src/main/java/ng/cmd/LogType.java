package ng.cmd;

public enum LogType {
	LOG_TYPE_CMD,
	LOG_TYPE_ERROR,
	LOG_TYPE_DEBUG,
	LOG_TYPE_START,
	LOG_TYPE_EXE,
	LOG_TYPE_NONE;

	private static final String log_cmd_str		= "CMD";
	private static final String log_error_str	= "ERROR";
	private static final String log_debug_str	= "DEBUG";
	private static final String log_start_str	= "START";
	private static final String log_exe_str		= "EXE";
	private static final String log_default_str	= "NONE_THIS_TYPE";
	
	public static LogType getLogType(String str){
		switch(str.toUpperCase()){
		case log_cmd_str:	return LOG_TYPE_CMD;
		case log_error_str:	return LOG_TYPE_ERROR;
		case log_debug_str:	return LOG_TYPE_DEBUG;
		case log_start_str:	return LOG_TYPE_START;
		case log_exe_str:	return LOG_TYPE_EXE;
			default:		return LOG_TYPE_NONE;
		}
	}
	
	public static String getLogTypeString(LogType t){
		switch(t){
		case LOG_TYPE_CMD:		return log_cmd_str;
		case LOG_TYPE_ERROR:	return log_error_str;
		case LOG_TYPE_DEBUG:	return log_debug_str;
		case LOG_TYPE_START:	return log_start_str;
		case LOG_TYPE_EXE:		return log_exe_str;
			default: 			return log_default_str;
		}
	}
	
	public static String getDefaultString(){
		return log_default_str;
	}
}
