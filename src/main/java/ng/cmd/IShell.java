package ng.cmd;

public interface IShell {
	void setup(IShell s);
	void run();
	String get_version();
	String get_usage();
	
	void cmd_exit();
	void cmd_test_aop(String[] strs);
}
