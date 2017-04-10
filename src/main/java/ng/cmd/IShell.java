package ng.cmd;

import java.io.IOException;

public interface IShell {
	/*
	 * basic function for shell
	 */
	void setup(IShell s);
	void run();
	String get_version();
	String get_usage();
	/*
	 * basic command supported by Shell
	 */
	void cmd_pwd();
	void cmd_exit();
	void cmd_ls(String dir);
	void cmd_cd(String dir);
	void cmd_cat(String file) throws IOException;
	boolean cmd_rm(String file);
}
