package ng.cmd;

import java.io.File;

/*
 * shell program
 * 1.read from user input
 * 2.get command , options and parameters
 * 3.check command validity
 * 4.dispatch command to its execution function
 */
public class CmdShell implements IShell{
	
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
	
	private String current_working_directory = "C:\\\\";
	private String get_cwd(){
		return current_working_directory;
	}
	private void set_cwd(String pwd){
		current_working_directory = pwd;
	}
	
	private void setup(){
		new FileManager();
	}
	
	private void cmd_pwd(){
		write_to_shell( get_cwd() + "\n");
	}
	
	private void cmd_cd(String dir){
		set_cwd( FileManager.get_absolute_path(dir) );
	}
	
	private void cmd_ls(String dir){
		File directory = new File(dir);

		if( !directory.exists() ){
			write_to_shell( dir+ " not exist " + "\n");
			return ;
		}
		if( !directory.isDirectory() ){
			write_to_shell( dir+ " not directory " + "\n");
			return ;
		}
		
		
	}
	
	private void cmd_exit(){
		shell_running = false;
	}
	
	private void dispatch_cmd(String[] cmds){
		
		if(cmds == null || cmds.length == 0){
			return ;
		}
		
		String command = cmds[0];
		switch( command ){
		case "pwd":	cmd_pwd();	break;
		case "cd":	cmd_cd(cmds[1]);	break;
		case "ls":	for(int i = 0; i < cmds.length-1; ++i) cmd_ls(cmds[ i+1 ]);	break;
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
		case "exit":cmd_exit();	break;
		default:
			break;
		}
	}
	
	private boolean shell_running = true;
	
	@Override
	public void run()  {
		setup();
		shell_running = true;
		while(shell_running){
			write_to_shell( get_cwd() + " > " );
			String input  = read_from_shell();
			String[] cmds = get_cmds_from_input( input );
			dispatch_cmd( cmds) ;
		}
	}

	@Override
	public String get_version() {
		return info_version;
	}

	@Override
	public String get_usage() {
		return info_usage;
	}
	
	private String[] get_cmds_from_input(String input){
		String[] cmds = input.split(" ");
		return cmds;
	}
	
	private String read_from_shell(){
		String str = FileManager.io_read_from_console_line();
		return str;
	}
	
	public static void write_to_shell(String str){
		FileManager.io_write_to_console(str);
	}
}
