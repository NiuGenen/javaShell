package ng.cmd;

import java.io.IOException;

public interface IShell {
	/*
	 * basic command supported by Shell
	 */
	void cmd_pwd();
	void cmd_exit();
	void cmd_ls(String dir) throws IOException;
	void cmd_cd(String dir) throws IOException;
	void cmd_cat(String file) throws IOException;
	boolean cmd_rm(String file);
	/*
	 * necessary function supported
	 */
	String get_cwd();
	void set_cwd(String wd);
	String get_version();
	String get_usage();
}
