package ng.cmd;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LogRecord {

	private final SimpleDateFormat log_data_time_format = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss:SSS]");

	/**
	 * constructor with String log_type_str
	 * @param time
	 * @param tpye_str
	 * @param info
	 */
	public LogRecord(Calendar time, String tpye_str, String info){
		this.log_time = time;
		this.log_type = LogType.getLogType( tpye_str );
		this.log_info = info;
	}
	/**
	 * constructor with LogType log_type
	 * @param time
	 * @param tpye
	 * @param info
	 */
	public LogRecord(Calendar time, LogType tpye, String info){
		this.log_time = time;
		this.log_type = tpye;
		this.log_info = info;
	}
	
	/**
	 * get log_time formated as [yyyy-MM-dd HH:mm:ss:SSS]
	 * @return
	 */
	public String get_log_time_str(){
		String log_time_str = log_data_time_format.format( log_time.getTime() );
		return log_time_str;
	}

	private Calendar log_time;
	private LogType log_type;
	private String log_info;
	
	public Calendar get_log_time_C()	{	return log_time;}
	public Date get_log_time_D()		{	return log_time.getTime();}
	public LogType get_log_type()		{	return log_type;}
	public String get_log_info()		{	return log_info;}
	
	public void set_log_time(Calendar c)	{ log_time = c;}
	public void set_log_type(LogType lt)	{ log_type = lt;}
	public void set_log_inof(String i)		{ log_info = i;}
	
	/**
	 * log information format
	 * [yyyy-mm-dd-hh:mm:ss]	operation time
	 * [log type]				CMD / ERROR / DEBUG
	 * [operation/command]		command executed	when type is CMD
	 * [information]			infomation about	when type is ERROR/DEBUG
	 */
	public String toString(){
		String log_time_str = get_log_time_str();
		String log_type_str = LogType.getLogTypeString( log_type );
		String log_info_str = log_info;

		return String.format("%26s  %5s  %s", log_time_str, log_type_str, log_info_str);
	}
}
