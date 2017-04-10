package ng.cmd;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class CmdMain {
	
	public static void main(String[] args) {

		@SuppressWarnings("resource")
		ApplicationContext context = new AnnotationConfigApplicationContext(ShellProgramConfig.class);
		IShell shell = context.getBean(IShell.class);

		//Test_Bean t = context.getBean(Test_Bean.class);
		//t.test_bean("asd");
		
		//CmdShell cs = context.getBean(CmdShell.class);
		//cs.run();
		
		shell.setup(shell);
		shell.run();
	}

}
