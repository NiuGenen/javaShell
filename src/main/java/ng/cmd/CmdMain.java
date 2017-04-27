package ng.cmd;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class CmdMain {
	
	public static void main(String[] args) {

		@SuppressWarnings("resource")
		ApplicationContext context = new AnnotationConfigApplicationContext(ShellProgramConfig.class);
		
		IAopTest aop_test = context.getBean(IAopTest.class);
		String[] test_args = {"test","AOP","1"};
		aop_test.test_aop(test_args);
		
		/*
		 * if CmdShell implements IShell
		 * then get CmdShell will fail
		 * else succeed
		 */
		//CmdShell cs = context.getBean(CmdShell.class);
		//cs.run();

		//shell start
		ICmdShell shell = context.getBean(ICmdShell.class);
		IShellFramework shell_fw = context.getBean(IShellFramework.class);
		shell_fw.setup(shell);
		shell_fw.loop_start();
	}

}
