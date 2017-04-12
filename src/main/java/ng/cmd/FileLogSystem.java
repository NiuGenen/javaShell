package ng.cmd;

import java.io.File;
import java.io.IOException;

public class FileLogSystem {
	/*
	 * log supported
	 */
	private static final String default_log_path = "D:\\";
	private static File sys_log = null;
	public static int log_write_count = 0;
	public static int log_flush_mask = 50;
	public static void log_creat_file(){
		File log_file = null;
		if(sys_log == null){
			log_file = new File( FileSystem.combine_path(default_log_path,"sys_log.txt") );
			if(!log_file.exists()){
				try {
					log_file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			sys_log = log_file;
		}
	}
	public static void log_write_line(String str){
		if(sys_log == null) log_creat_file();
		
		FileSystem.io_append_to_file_line(sys_log, str);
		
		++log_write_count;
		if( log_write_count > log_flush_mask){
			log_flush();
			log_write_count = 0;
		}
	}
	public static void log_flush(){
		FileSystem.io_flush_file( sys_log );
	}
}
