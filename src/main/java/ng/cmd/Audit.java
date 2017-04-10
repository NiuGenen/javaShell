package ng.cmd;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class Audit {
	
	/*
	 * test AOP execution order
	 * @Pointcut to CmdShell.cmd_test_aop(String[] objs)
	 * @Around to get parameters , execution target function and print ret value
	 * @Before to print information about this function
	 * @After
	 * @AfterReturning to print the return value
	 */

	@Around("execution(** ng.cmd.CmdShell.cmd_test_aop(..))")
	public void around_shell_test_aop(ProceedingJoinPoint point) throws Throwable{
		FIleSystem.io_write_to_console_line("[Around: before function]");
		//access function parameters
		Object[] args = point.getArgs();
		for(int i=0; i<args.length; ++i){
			FIleSystem.io_write_to_console_line("args["+i+"]="+args[i].getClass());
		}
		//execution function
		Object ret = point.proceed(args);
		//after function exe
		FIleSystem.io_write_to_console_line("[Around: after function]");
		FIleSystem.io_write_to_console_line("ret=" + ret);
	}
	
	@Before("execution(** ng.cmd.CmdShell.cmd_test_aop(..))")
	public void before_shell_test_aop(JoinPoint point){
		FIleSystem.io_write_to_console_line("[Before: print function]" + 
                point.getSignature().getDeclaringTypeName() + 
                "." + point.getSignature().getName());
		FIleSystem.io_write_to_console_line("[Before: print parameter ]" + Arrays.toString(point.getArgs()));
	}
	
	@After("execution(** ng.cmd.CmdShell.cmd_test_aop(..))")
	public void after_shell_test_aop(JoinPoint point){
		FIleSystem.io_write_to_console_line("[After]");
	}

	
	/*
	 * 
	 */
	
	@Before( "execution(** ng.cmd.CmdShell.run(..))" )
	public void before_shell_run(){
		FIleSystem.io_write_to_console_line("Shell start running");
	}
	
	@Before( "execution(** ng.cmd.CmdShell.cmd_exit(..))" )
	public void before_shell_exit(){
		FIleSystem.io_write_to_console_line("Shell exiting");
	}
	
	@After( "execution(** ng.cmd.CmdShell.cmd_exit(..))" )
	public void after_shell_exit(){
		FIleSystem.io_write_to_console_line("Shell exited");
	}
}
