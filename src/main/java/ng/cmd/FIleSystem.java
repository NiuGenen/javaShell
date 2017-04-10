package ng.cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;

@SuppressWarnings("unused")
public class FileSystem {
	
	public static FileLogSystem LogSystem = new FileLogSystem();
	
	private static BufferedReader strin = null;
	
	//input/ooutput control
	
	/**
	 * write string to console
	 * @param str
	 */
	public static void io_write_to_console(String str){
		System.out.print(str);
	}
	/**
	 * write string to console then '\n'
	 * @param str
	 */
	public static void io_write_to_console_line(String str){
		System.out.println(str);
	}
	/**
	 * read from console ( waiting for user's input )
	 * @return
	 */
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
	private static Map<File, FileOutputStream> map_file_to_fos = new HashMap<>();
	public static void io_write_to_file_line(File f, String str) throws IOException{
		//io_write_to_console_line(str);
		FileOutputStream fos = null;
		if( !map_file_to_fos.containsKey(f) ){
			fos = new FileOutputStream(f);
			map_file_to_fos.put(f, fos);
			fos.write(str.getBytes());
		}else{
			fos = map_file_to_fos.get(f);
		}
		fos.write( (str + "\r\n" ).getBytes());
	}
	public static String io_read_from_file_line(File f){
		return "asd";
	}
	public static String io_read_from_file_all(File f) throws IOException{
		FileInputStream fis = new FileInputStream(f);
		@SuppressWarnings("resource")
		BufferedReader fis_rdr = new BufferedReader(new InputStreamReader(fis));
		StringBuffer ret = new StringBuffer();
		while( fis_rdr.ready() ){
			ret.append( fis_rdr.readLine() );
		}
		return ret.toString();
	}	
	public static void io_close_file(File f) throws IOException{
		if(map_file_to_fos.containsKey(f)){
			FileOutputStream fos = map_file_to_fos.get(f);
			fos.close();
			map_file_to_fos.remove(f);
		}
	}
	public static void io_flush_file(File f) throws IOException{
		if(map_file_to_fos.containsKey(f)){
			FileOutputStream fos = map_file_to_fos.get(f);
			fos.flush();
		}
	}
	
	/*
	 * file control
	 */
	/**
	 * combine two path into an absolute path
	 * @param parent			an absolute path	start with "C:\\"
	 * @param relative_path		an relative path	start with "."
	 * @return
	 */
	public static String combine_path(String parent, String relative_path){
		while( parent.endsWith("\\") )
			parent = parent.substring( 0, parent.length() - 1);
		while( relative_path.startsWith(".") )
			relative_path = relative_path.substring( 0, relative_path.length() - 1);
		
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
		return combine_path(CmdShell.get_cwd(), dir);
	}

}
