package ng.cmd;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class CmdMain {
	
	public static void main(String[] args) {

		@SuppressWarnings("resource")
		ApplicationContext context = new AnnotationConfigApplicationContext(ShellProgramConfig.class);
		IShell shell = context.getBean(IShell.class);
		
		shell.run();
	}

}
