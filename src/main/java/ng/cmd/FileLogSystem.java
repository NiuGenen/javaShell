package ng.cmd;

import java.io.File;
import java.io.IOException;

public class FileLogSystem {
	/*
	 * log supported
	 */
	private static String default_log_path = "D:\\";
	private static File sys_log = null;
	public static int log_write_count = 0;
	public static int log_flush_mask = 50;
	public static File creat_log_file(){
		File log_file = null;
		if(sys_log == null){
			log_file = new File( FileSystem.combine_path(default_log_path,"sys_log.txt") );
			if(!log_file.exists()){
				try {
					log_file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
			sys_log = log_file;
		}
		return sys_log;
	}
	public static boolean log_write_line(String str) throws IOException{
		if(sys_log == null) creat_log_file();
		if(sys_log == null) return false;
		
		FileSystem.io_append_to_file_line(sys_log, str);
		
		++log_write_count;
		if( log_write_count > log_flush_mask){
			log_flush();
			log_write_count = 0;
		}
		return true;
	}
	public static boolean log_flush(){
		try {
			FileSystem.io_flush_file( sys_log );
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
