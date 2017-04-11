package ng.cmd;

public interface IShellFramework {
	/*
	 * basic function for shell
	 */
	void setup(Object o);
	void loop_start();
	String get_version();
	String get_usage();
}
