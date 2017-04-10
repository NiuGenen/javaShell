package ng.cmd;

import java.io.File;

/**
 * shell program
 * 1.read from user input
 * 2.get command , options and parameters
 * 3.dispatch command to its check parameter validity function
 * 4.execution function
 */
public class CmdShell implements IShell{
	
	private IShell shell;
	
	/**
	 * command: test [string] [integer]
	 * test if AOP is working
	 */
	public void cmd_test_aop(String[] objs){
		write_to_shell("This is a test command for aop.\n");
		String aop = objs[1];
		Integer number = Integer.parseInt(objs[2]);
		
		write_to_shell("Input string:  " + aop + "\n");
		write_to_shell("Input integer: " + number + "\n");
		
	}
	
	private String info_version = "My cmd shell 0.1";
	private String info_usage = "command [parameter] [option]\n" +
								"\n" +
								"cmd supported:\n" + 
								"\n" +
								"    test aop [num]\n" +
								"        test AOP\n" +
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
								"    append [filename] [string]\n" +
								"        append content into file\n" +
								"    delete [filename] [num]\n" +
								"        delete file's this line\n" +
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
	private String current_working_directory = "C:\\\\";
	private String get_cwd(){
		return current_working_directory;
	}
	private void set_cwd(String pwd){
		current_working_directory = pwd;
	}
	
	/**
	 * build up shell
	 */
	public void setup(IShell shell){
		this.shell = shell;
		//read_from_shell();
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
		set_cwd( FIleSystem.get_absolute_path(dir) );
	}
	
	/**
	 * command: ls [directory...]
	 * @param dir
	 */
	public void cmd_ls(String[] dirs){
		String dir = dirs[0];
		
		File directory = new File(dir);

		if( !directory.exists() ){
			write_to_shell_line( dir+ " not exist ");
			return ;
		}
		if( !directory.isDirectory() ){
			write_to_shell_line( dir+ " not directory ");
			return ;
		}
		
		
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
	 */
	public void dispatch_cmd(String[] cmds){
		
		if(cmds == null || cmds.length == 0){
			return ;
		}
		
		String command = cmds[0];
		switch( command ){
		case "test":	shell.cmd_test_aop(cmds);	break;
		case "pwd":	cmd_pwd();	break;
		case "cd":	cmd_cd(cmds[1]);	break;
		case "ls":	cmd_ls(cmds);	break;
		case "cat":
			break;
		case "append":
			break;
		case "delete":
			break;
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
			dispatch_cmd( cmds) ;
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
		String str = FIleSystem.io_read_from_console_line();
		return str;
	}
	
	public static void write_to_shell(String str){
		FIleSystem.io_write_to_console(str);
	}
	
	public static void write_to_shell_line(String str){
		FIleSystem.io_write_to_console_line(str);
	}
}
