package ng.cmd;

import java.io.IOException;
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

import ng.com.util.Util_time;

/**
 * for log and test
 * @author 60213
 *
 */
@SuppressWarnings("unused")
@Component
@Aspect
public class Audit {
	
	/*
	 * test AOP execution order
	 * @Pointcut to CmdShell.test_aop(String[] objs)
	 * @Around to get parameters , execution target function and print ret value
	 * @Before to print information about this function
	 * @After
	 * @AfterReturning to print the return value
	 */
	@Pointcut("execution(** ng.cmd.IAopTest.test_aop(..))")
	public void test_aop(){}
	@Around("test_aop()")
	public Integer around_shell_test_aop(ProceedingJoinPoint point){
		FileSystem.io_write_to_console_line("[Around: before function]");
		//access function parameters
		Object[] point_args = point.getArgs();
		String[] args = (String[])point_args[0];
		FileSystem.io_write_to_console_line("args.len=" + args.length);
		for(int i=0; i<args.length; ++i){
			FileSystem.io_write_to_console_line("args[" + i + "]=" + args[i].getClass());
		}
		//execution function
		Object ret = new Integer(-2);
		try {
			ret = point.proceed(point_args);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		//after function exe
		FileSystem.io_write_to_console_line("[Around: after function]");
		FileSystem.io_write_to_console_line("ret=" + ret);
		//return to @AfterReturning
		return (Integer)ret + 1;
	}
	@Before("test_aop()")
	public void before_shell_test_aop(JoinPoint point){
		FileSystem.io_write_to_console_line("[Before: print function]" + 
                point.getSignature().getDeclaringTypeName() + 
                "." + point.getSignature().getName());
		FileSystem.io_write_to_console_line("[Before: print parameter ]" + Arrays.toString(point.getArgs()));
	}
	@After("test_aop()")
	public void after_shell_test_aop(JoinPoint point){
		FileSystem.io_write_to_console_line("[After]");
		FileSystem.io_write_to_console_line("Did nothing");
	}
	@AfterReturning(pointcut="test_aop()",returning="ret")
	public void after_return_shell_test_aop(JoinPoint point, Object ret){
		FileSystem.io_write_to_console_line("[After Returning]");
		FileSystem.io_write_to_console_line("ret=" + (Integer)ret);
	}
	
	/*
	 * log command execution
	 */
	@Before("execution(** ng.cmd.IShell.cmd_*(..))")
	public void before_all_command(JoinPoint point){
		Object[] args = point.getArgs();
		
		LogRecord log_rcd = new LogRecord(Util_time.get_current_Calendar(),
											LogType.LOG_TYPE_CMD,
											"command execution");
		
		StringBuffer log_info = new StringBuffer();
		boolean has_args = false;
		
		switch( point.getSignature().getName() )
		{
		case "cmd_pwd":   	log_info.append("pwd   ");    
														break;
		case "cmd_ls":    	log_info.append("ls    ");	has_args = true;
														break;
		case "cmd_cd":    	log_info.append("cd    ");	has_args = true;
														break;
		case "cmd_exit":  	log_info.append("exit  ");    
														break;
		case "cmd_cat":   	log_info.append("cat   ");	has_args = true;
														break;
		case "cmd_rm":		log_info.append("rm    ");  has_args = true;
														break;
		}

		if(	has_args )
			for(Object arg: args)
				log_info.append( arg + " ");
		
		log_rcd.set_log_inof( log_info.toString() );
		
		FileLogSystem.log_write_line(log_rcd.toString());
	}
	
	/*
	 * shell start
	 */
	@Before( "execution(** ng.cmd.IShellFramework.loop_start(..))" )
	public void before_shell_run(){
		FileSystem.io_write_to_console_line("[Before] Shell start running");
		
		LogRecord log_rcd = new LogRecord( 
				Util_time.get_current_Calendar(),
				LogType.LOG_TYPE_START,
				"Shell started"
				);
		
		FileLogSystem.log_write_line( log_rcd.toString() );
		
	}
	
	/*
	 * shell exit
	 */
	@Before( "execution(** ng.cmd.CmdShell.cmd_exit(..))" )
	public void before_shell_exit(){
		FileSystem.io_write_to_console_line("[Before] Shell exiting");
		
		FileSystem.io_write_to_console_line("[Before] exiting process: flush log file");
		FileLogSystem.log_flush();
	}
}
