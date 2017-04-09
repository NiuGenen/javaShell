package ng.cmd;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class Audit {

	@Pointcut("execution(** ng.cmd.CmdShell.run(..))")
	public void ptct_shell_run(){
	}
	
	@Before("ptct_shell_run()")
	public void before_shell_run(){
		FileManager.io_write_to_console_line("Shell start running");
	}
}
