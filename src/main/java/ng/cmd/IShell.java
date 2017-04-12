package ng.cmd;

import java.io.IOException;

public interface IShell {
	/*
	 * basic command supported by Shell
	 */
	void cmd_pwd();
	void cmd_exit();
	void cmd_ls(String dir);
	void cmd_cd(String dir);
	void cmd_rm(String file);
	void cmd_cat(String file);
	/*
	 * necessary function supported
	 */
	String get_cwd();
	void set_cwd(String wd);
	
	String get_version();
	String get_usage();

	void io_write_to_shell(String str);
	void io_write_to_shell_line(String str);
	String io_read_from_shell_line();
}
