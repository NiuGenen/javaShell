package ng.cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class FIleSystem {
	
	private static BufferedReader strin = null;
	
	public static void io_write_to_console(String str){
		System.out.print(str);
	}
	
	public static void io_write_to_console_line(String str){
		System.out.println(str);
	}
	
	public static String io_read_from_console_line(){
		strin = new BufferedReader(new InputStreamReader(System.in));
		String input = "";
		try {
			input = strin.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return input;
	}
	
	public static String combine_path(String parent, String relative_path){
		String path = String.format("%s\\%s", parent,relative_path);
		return path;
	}
	
	public static boolean if_file_exist(File file){
		return file.exists();
	}
	
	public static boolean if_file_is_dir(String dir){
		File file = new File(dir);
		return file.isDirectory();
	}
	
	public static String get_absolute_path(String dir){

		File directory = new File(dir);
		
		if( !directory.exists() ){
			CmdShell.write_to_shell( dir+ " not exist " + "\n");
			return null;
		}
		if( !directory.isDirectory() ){
			CmdShell.write_to_shell( dir+ " not directory " + "\n");
			return null;
		}
		
		return directory.getAbsolutePath();
	}
}
