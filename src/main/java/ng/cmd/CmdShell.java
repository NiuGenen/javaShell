package ng.cmd;

import java.io.File;
import java.io.IOException;

/**
 * shell program
 * 1.read from user input
 * 2.get command , options and parameters
 * 3.dispatch command to its check parameter validity function
 * 4.execution function
 */
public class CmdShell implements IShell , IAopTest{
	
	private IShell shell;
	
	/**
	 * command: test [string] [integer]
	 * test if AOP is working
	 */
	public Integer test_aop(String[] objs){
		write_to_shell("This is a test command for aop.\n");
		assert(objs.length >= 3);
		String test = objs[0];
		String aop = objs[1];
		Integer number = Integer.parseInt(objs[2]);
		
		write_to_shell_line("Input string: " + test);
		write_to_shell_line("Input string:  " + aop);
		write_to_shell_line("Input integer: " + number);
		
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
								"    exit\n" +
								"        exit shell";
	
	/*
	 * maintain current working directory
	 */
	private static String current_working_directory = "C:\\\\";
	public static String get_cwd(){
		return current_working_directory;
	}
	public static void set_cwd(String pwd){
		current_working_directory = pwd;
	}
	
	/**
	 * build up shell with its IShell interface 
	 * for the AOP doesn't support nested call
	 * cmd function need to access by IShell
	 */
	public void setup(IShell shell){
		this.shell = shell;
	}
	
	/**
	 * command: pwd
	 * print current working directory
	 */
	public void cmd_pwd(){
		write_to_shell_line( "CWD: " + get_cwd());
	}
	
	/**
	 * command: cd [directory]
	 * change current working directory
	 * let Audit to check the validity of input string
	 * @dir need one valid directory
	 */
	public void cmd_cd(String dir){
		set_cwd( FileSystem.get_absolute_path(dir) );
	}
	
	/**
	 * command: ls [directory...]
	 * @param dir
	 */
	public void cmd_ls(String dir){
		
		File directory = new File(dir);
		
		File[] subs = directory.listFiles();
		for(File f : subs){
			if(f.isFile()){
				FileSystem.io_write_to_console_line("[F] " + f.getName());
			}
			else if(f.isDirectory()){
				FileSystem.io_write_to_console_line("[D] " + f.getName());
			}
		}
	}
	
	/**
	 * print one file's content
	 */
	public void cmd_cat(String file) throws IOException{
		
		File f = new File(file);
		
		String content = FileSystem.io_read_from_file_all(f);
		
		FileSystem.io_write_to_console_line(content);
	}
	
	/**
	 * remove a file not a directory
	 */
	public boolean cmd_rm(String file){
		File f = new File(file);
		return f.delete();
	}
	
	/**
	 * exit the shell program. 
	 * just set 'shell_running" to false
	 */
	public void cmd_exit(){
		shell_running = false;
	}
	
	/**
	 * according to the command which must be cmsds[0] 
	 * send parameters to its execution function 
	 * the validity check and logging will be executed by AOP Audit
	 * @throws IOException 
	 */
	public void dispatch_cmd(String[] cmds) throws IOException{
		
		if(cmds == null || cmds.length == 0){
			return ;
		}
		
		String command = cmds[0];
		switch( command ){
		case "pwd":	shell.cmd_pwd();			break;
		case "cd":	shell.cmd_cd(cmds[1]);		break;
		case "ls":	if(cmds.length >= 2)
						shell.cmd_ls(cmds[1]);
					else
						shell.cmd_ls(null);		break;
		case "cat": shell.cmd_cat(cmds[1]); 	break;
		case "mkdir":
			break;
		case "touch":
			break;
		case "rmdir":
			break;
		case "rm":
			break;
		case "exit":	shell.cmd_exit();	break;
		default:
			break;
		}
	}
	
	private boolean shell_running = true;
	
	public void run()  {
		shell_running = true;
		while(shell_running){
			write_to_shell( get_cwd() + " javaShell > " );
			String input  = read_from_shell();
			String[] cmds = get_cmds_from_input( input );
			try {
				dispatch_cmd( cmds) ;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String get_version() {
		return info_version;
	}

	public String get_usage() {
		return info_usage;
	}
	
	private String[] get_cmds_from_input(String input){
		String[] cmds = input.split(" ");
		return cmds;
	}
	
	private String read_from_shell(){
		String str = FileSystem.io_read_from_console_line();
		return str;
	}
	
	public static void write_to_shell(String str){
		FileSystem.io_write_to_console(str);
	}
	
	public static void write_to_shell_line(String str){
		FileSystem.io_write_to_console_line(str);
	}
}
