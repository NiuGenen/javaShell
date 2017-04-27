package ng.cmd;

import java.io.File;
import java.io.IOException;

import ng.jms.client.JmsShell;

/**
 * shell program
 * 1.read from user input
 * 2.get command , options and parameters
 * 3.dispatch command to its check parameter validity function
 * 4.execution function
 */
public class CmdShell implements ICmdShell , IAopTest{
	
	/**
	 * command: test [string] [integer]
	 * test if AOP is working
	 */
	public Integer test_aop(String[] objs){
		FileSystem.io_write_to_console_line("[Test Aop start]");
		FileSystem.io_write_to_console_line("This is a test command for aop.");
		assert(objs.length >= 3);
		String test = objs[0];
		String aop = objs[1];
		Integer number = Integer.parseInt(objs[2]);
		
		io_write_to_shell_line("Input string: " + test);
		io_write_to_shell_line("Input string:  " + aop);
		io_write_to_shell_line("Input integer: " + number);
		
		return number + 1;
	}
	
	private String info_version = "My cmd shell 0.1";
	private String info_usage = "command [parameter] [option]\n" +
								"\n" +
								"cmd supported:\n" + 
								"\n" +
								"    pwd\n" + 
								"        print current working directory\n" +
								"    cd [dir]\n" +
								"        change current working directory" +
								"    ls [dir]\n" +
								"        list dir's files and directories\n" +
								"        options supported:\n" +
								"            -h : human readable\n" +
								"            -l : list detail information\n" +
								"    cat [filename]\n" +
								"        print file's content\n" +
								"        options supported:\n" +
								"            -l : with line number\n" +
								"            -s [num]: start at this line\n" +
								"            -e [num]: end at this line\n" +
								"    mkdir [dir]\n" +
								"        create directory\n" +
								"    touch [filename]\n" +
								"        create an empty file\n" +
								"    rmdir [dir]\n" +
								"        delete one directory\n" +
								"    rm [filename]\n" +
								"        delete one file\n" +
								"	jms" +
								"		enter jms shell" +
								"    exit\n" +
								"        exit shell";
	
	/*
	 * maintain current working directory
	 */
	private static String current_working_directory = "C:\\\\";
	public String get_cwd(){
		return current_working_directory;
	}
	public void set_cwd(String pwd){
		if(pwd != null)
			current_working_directory = pwd;
	}
	public static String get_cwd_static(){
		return current_working_directory;
	}
	
	/**
	 * command: pwd
	 * print current working directory
	 */
	public void cmd_pwd(){
		io_write_to_shell_line( "CWD: " + get_cwd() );
	}
	
	/**
	 * command: cd [directory]
	 * change current working directory
	 * let Audit to check the validity of input string
	 * @throws IOException 
	 * @dir need one valid directory
	 */
	public void cmd_cd(String dir){
		//AOP promise dir is a valid,exist directory or is null
		set_cwd( dir );
	}
	
	/**
	 * command: ls [directory...]
	 * @param dir
	 * @throws IOException 
	 */
	public void cmd_ls(String dir){
		
		File directory = FileSystem.get_file_by_path(get_cwd(), dir);
		if(directory == null){
			FileSystem.io_write_to_stderror_line("cannot resolve path : " + dir);
			return;
		}
		
		File[] subs = directory.listFiles();
		for(File f : subs){
			if(f.isFile()){
				io_write_to_shell_line("[F] " + f.getName());
			}
			else if(f.isDirectory()){
				io_write_to_shell_line("[D] " + f.getName());
			}
		}
	}
	
	/**
	 * print one file's content
	 */
	public void cmd_cat(String file){
		
		/*
		File f = FileSystem.get_file_by_path(get_cwd(), file);
		if( f == null ){
			FileSystem.io_write_to_stderror_line("cannot resolve file : " + file);
			return;
		}
		
		String content = FileSystem.io_read_from_file_all(f);
		*/
		
		//input filename will be changed to its content by AOP
		String content = file;
		
		io_write_to_shell_line(content);
	}
	
	/**
	 * remove a file not a directory
	 */
	public void cmd_rm(String file){
		//ask user by AOP, if no then file will be set to null
		//String file verified by AOP
		if(file != null){
			File f = FileSystem.get_file_by_path( get_cwd(), file);
			if( !f.delete() ){
				FileSystem.io_write_to_stderror_line("[ERROR] cannot delete file : " + f.getAbsolutePath() );
			}
		}
	}
	
	/**
	 * exit the shell program. 
	 * using system.exit(0)
	 */
	public void cmd_exit(){
		System.exit(0);
	}
	
	public String get_version() {
		return info_version;
	}
	public String get_usage() {
		return info_usage;
	}
	
	@Override
	public void io_write_to_shell(String str) {
		FileSystem.io_write_to_console( str);
	}
	@Override
	public void io_write_to_shell_line(String str) {
		FileSystem.io_write_to_console_line( str);
	}
	@Override
	public String io_read_from_shell_line() {
		return FileSystem.io_read_from_console_line();
	}
	
	/**
	 * start jms client
	 */
	@Override
	public void jms_client() {
		IShellFramework jms = new JmsShell();
		jms.setup(null);
		jms.loop_start();
	}
}
