package ng.cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
//import java.nio.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ng.com.util.Util_time;

import java.text.SimpleDateFormat;

@SuppressWarnings("unused")
public class FileSystem {
	
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
	public static void io_write_to_stderror_line(String info){
		LogRecord log_rcd = new LogRecord(
				Util_time.get_current_Calendar(),
				LogType.LOG_TYPE_ERROR,
				info);
		FileLogSystem.log_write_line( log_rcd.toString() );
		System.out.println( info );
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
	/**
	 * write one line to the File. 
	 * cover the origin content in File.
	 * FileOutputStream will be cached. 
	 * make sure to CLOSE or FLUSH.
	 * @param f
	 * @param str
	 * @throws IOException
	 */
	public static void io_write_to_file_line(File f, String str){
		//io_write_to_console_line(str);
		try{
			FileOutputStream fos = null;
			if( !map_file_to_fos.containsKey(f) ){
				fos = new FileOutputStream(f);
				map_file_to_fos.put(f, fos);
				fos.write(str.getBytes());
			}else{
				fos = map_file_to_fos.get(f);
			}
			fos.write( (str + "\r\n" ).getBytes());
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	/**
	 * append one line to the File. 
	 * NOT cover the origin content in File.
	 * FileOutputStream will be cached. 
	 * make sure to CLOSE or FLUSH.
	 * @param f
	 * @param str
	 * @throws IOException
	 */
	public static void io_append_to_file_line(File f, String str){
		try {
			FileOutputStream fos = null;
			if( !map_file_to_fos.containsKey(f) ){
				fos = new FileOutputStream(f, true);
				map_file_to_fos.put(f, fos);
				fos.write(str.getBytes());
			}else{
				fos = map_file_to_fos.get(f);
			}
			fos.write( (str + "\r\n" ).getBytes());
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * using BufferReader to read an text file. 
	 * will NOT verify if this is a text file
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static String io_read_from_file_all(String filepath) {
		File f = new File(filepath);
		StringBuffer ret = new StringBuffer();
		try {
			FileInputStream fis = new FileInputStream(f);
			@SuppressWarnings("resource")
			BufferedReader fis_rdr = new BufferedReader(new InputStreamReader(fis));
			while( fis_rdr.ready() ){
				ret.append( fis_rdr.readLine() + "\n");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret.toString();
	}
	/**
	 * close the FileOutputStream bounded with the given file. 
	 * do nothing if no FileOutputStream was opened before
	 * @param f
	 * @throws IOException
	 */
	public static void io_close_file(File f){
		try {
			if(map_file_to_fos.containsKey(f)){
				FileOutputStream fos = map_file_to_fos.get(f);
				fos.close();
				map_file_to_fos.remove(f);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * flush the FileOutputStream bounded with the given file. 
	 * do nothing if no FileOutputStream was opened before
	 * @param f
	 * @throws IOException
	 */
	public static void io_flush_file(File f){
		try {
			if(map_file_to_fos.containsKey(f)){
				FileOutputStream fos = map_file_to_fos.get(f);
				fos.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * file control
	 */
	/** 
	 * \ needed by java  \" \\ 
	 * \ needed by regex \? \* \+ \. \- \$ \\ \( \) \[ \] \| \^
	 * so the \ needed by regex need double \ "\?" -> "\\?"
	 * valid filename on windows "[^\" ></\\\\?\\*\\|][^\"></\\\\?\\*\\|]*"
	 * @param parent			an absolute path	start with "C:\\" or "C:\"
	 * @param relative_path		an relative path	could be "." or ".." or start with ".\" or "..\" or filename
	 * @return
	 */
	public static File get_file_by_path(String root, String path){
		File ret_file = null;
		if( if_path_absolute(path) ){
			ret_file = new File( path );
		}
		else if( if_path_relative(path) ){
			ret_file = new File( combine_path(root, path) );
		}
		return ret_file;
	}
	public static boolean if_path_absolute(String path){
		if(path == null) return false;
		return path.matches( regex_absolute_path_windows );
	}
	public static boolean if_path_relative(String path){
		if(path == null) return false;
		return path.matches( regex_relative_path_windows );
	}
	public static boolean if_path_root(String path){
		if(path == null) return false;
		return path.matches(regex_root_path_windows);
	}
	private static final String regex_root_path_windows = 
			"[c-zC-Z]:\\\\{1,2}";
	private static final String regex_absolute_path_windows = 
			"[c-zC-Z]:\\\\{1,2}([^\" ></\\\\\\?\\*\\|][^\"></\\\\\\?\\*\\|]*(\\\\[^\" ></\\\\\\?\\*\\|][^\"></\\\\\\?\\*\\|]*)*\\\\?)?";
	private static final String regex_relative_path_windows = 
			"[^\" ></\\\\\\?\\*\\|][^\"></\\\\\\?\\*\\|]*(\\\\[^\" ></\\\\\\?\\*\\|][^\"></\\\\\\?\\*\\|]*)*\\\\?";
	
	public static String combine_path(String root_pth, String rlt_pth){
		if(!if_path_absolute(root_pth)){
			return null;
		}
		if(!if_path_relative(rlt_pth)){
			return null;
		}

		char[] root = root_pth.toCharArray();
		char[] rlt = rlt_pth.toCharArray();
		int root_s = 0, root_e = root.length - 1;
		int rlt_s = 0, rlt_e = rlt.length - 1;
		
		if( root[root_e] == '\\' ) root_e -= 1;
		while(rlt[rlt_s] == '.'){
			if(rlt_e - rlt_s == 0){		//.
				rlt[rlt_s] = '\0';
				rlt_e = rlt_s - 1;
				break;
			}
			if(rlt_e - rlt_s == 1){		//.?
				if(rlt[rlt_e] == '.'){	// ..
					while(root[root_e] != '\\' && root_e >= 2) root_e -= 1;
					if(root_e >= 2) root_e -= 1;
					rlt[rlt_s] = '\0';
					rlt_e = rlt_s - 1;
					break;
				}
				if(rlt[rlt_e] == '\\'){	// .\
					rlt[rlt_s] = '\0';
					rlt_e = rlt_s - 1;
					break;
				}
			}
			if(rlt_e - rlt_s == 2){
				if(rlt[rlt_e -1] == '.' && rlt[rlt_e] == '\\'){ // ..\
					while(root[root_e] != '\\' && root_e >= 2) root_e -= 1;
					if(root_e >= 2) root_e -= 1;
					rlt[rlt_s] = '\0';
					rlt_e = rlt_s - 1;
					break;
				}
			}
			if(rlt_e - rlt_s >= 3){
				if(rlt[rlt_s + 1] == '\\'){	// .\other
					rlt_s += 2;
				}
				else if(rlt[rlt_s + 1] == '.' && rlt[rlt_s + 2] == '\\'){	// ..\other
					while(root[root_e] != '\\' && root_e >= 2) root_e -= 1;
					if(root_e >= 2) root_e -= 1;
					rlt_s += 3;
				}
			}
		}
		
		String path = String.format("%s\\%s",
				new String(root, root_s, root_e - root_s + 1),
				new String(rlt, rlt_s, rlt_e - rlt_s + 1));
		
		FileSystem.io_write_to_console_line(root_pth + " + " + rlt_pth + " = " + path);
		
		return path;
	}
	public static boolean if_file_exist(String file){
		return new File(file).exists();
	}
	public static boolean if_file_is_dir(String dir){
		File file = new File(dir);
		return file.isDirectory();
	}

}
