package ng.cmd;

import java.io.IOException;

public class CmdShellFramework implements IShellFramework{
	
	private ICmdShell shell = null;
	
	/**
	 * build up shell with its IShell interface 
	 * for the AOP doesn't support nested call
	 * cmd function need to access by IShell
	 */
	public void setup(Object shell){
		this.shell = (ICmdShell)shell;
	}
	

	private boolean shell_running = true;

	/**
	 * according to the command which must be cmsds[0] 
	 * send parameters to its execution function 
	 * the validity check and logging will be executed by AOP Audit
	 * @throws IOException 
	 */
	public void dispatch_cmd(String[] cmds) throws IOException{
		
		if(cmds == null || cmds.length == 0) return ;
		
		String command = cmds[0];
		switch( command ){
		case "jms":	shell.jms_client();				break;
		case "jmstest": shell.jms_client_test();	break;
		case "pwd":	shell.cmd_pwd();				break;
		case "cd":	shell.cmd_cd(cmds[1]);			break;
		case "ls":	if(cmds.length >= 2)
						shell.cmd_ls(cmds[1]);
					else
						shell.cmd_ls( shell.get_cwd() );	break;
		case "cat": shell.cmd_cat(cmds[1]); 		break;
		case "mkdir":
			break;
		case "touch":
			break;
		case "rmdir":
			break;
		case "rm":	if(cmds.length >= 2)
						shell.cmd_rm(cmds[1]);		break;
		case "exit":	shell.cmd_exit();			break;
		default:
			break;
		}
	}
	
	private String[] get_cmds_from_input(String input){
		String[] cmds = input.split(" ");
		return cmds;
	}
	private String read_from_shell(){
		String str = FileSystem.io_read_from_console_line();
		return str;
	}
	private void write_to_shell(String str){
		FileSystem.io_write_to_console(str);
	}
	private void write_to_shell_line(String str){
		FileSystem.io_write_to_console_line(str);
	}
	
	@Override
	public void loop_start() {
		shell_running = true;
		while(shell_running){
			write_to_shell( shell.get_cwd() + " javaShell > " );
			String input  = read_from_shell();
			String[] cmds = get_cmds_from_input( input );
			try {
				dispatch_cmd( cmds) ;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String get_version() {
		if(shell == null) return null;
		return shell.get_version();
	}

	@Override
	public String get_usage() {
		if(shell == null) return null;
		return shell.get_usage();
	}

}
