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
			FileSystem.io_write_to_console_line("args[" + i +"]= " + args[i]);
		}
		args[1] = "changed by @Around";
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
		Object[] point_args = point.getArgs();
		String[] args = (String[])point_args[0];
		for(int i=0; i<args.length; ++i){
			FileSystem.io_write_to_console_line("args[" + i +"]= " + args[i]);
		}
		
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
	@Before("execution(** ng.cmd.ICmdShell.cmd_*(..))")
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
	
	/*
	 * origin CmdShellAspect
	 */

	@Around("execution(** ng.cmd.ICmdShell.cmd_rm(..))")
	public void around_shell_cmd_rm(ProceedingJoinPoint point){
		//get arguments
		Object[] args = point.getArgs();
		//ask user if yes or not
		FileSystem.io_write_to_console("Are you sure to remove ? [y/n] :");
		String answer = FileSystem.io_read_from_console_line();
		while( !answer.toLowerCase().equals("y") && !answer.toLowerCase().equals("n") ){
			FileSystem.io_write_to_console("Please enter 'y' or 'n' : ");
			answer = FileSystem.io_read_from_console_line();
		}
		if(answer.toLowerCase().equals("n")){
			args[0] = null;
		}
		//verify arguments' validity
		String filename = (String)args[0];
		
		String filepath = null;
		if(FileSystem.if_path_absolute(filename)){
			if(!(FileSystem.if_file_exist(filename) && !FileSystem.if_file_is_dir(filename))){
				FileSystem.io_write_to_stderror_line("cannot resolve file : " + filename + ".");
				FileSystem.io_write_to_stderror_line("It maybe not exist or is a directory.");
				filepath = null;
			}
			else{
				filepath = filename;
			}
		}
		else if(FileSystem.if_path_relative(filename)){
			String ab_dir = FileSystem.combine_path(CmdShell.get_cwd_static(), filename);
			if(!(FileSystem.if_file_exist(ab_dir) && !FileSystem.if_file_is_dir(ab_dir))){
				FileSystem.io_write_to_stderror_line("cannot resolve file : " + filename + ".");
				FileSystem.io_write_to_stderror_line("It maybe not exist or is a directory.");
				filepath = null;
			}else{
				filepath = ab_dir;
			}
		}
		else if(filename != null){
			FileSystem.io_write_to_stderror_line("wrong file input : " + filename);
			filepath = null;
		}
		//run cmd_rm() function
		if(args[0] != null) args[0] = filepath;
		try {
			point.proceed( args );
		} catch (Throwable e) {
			e.printStackTrace();
			LogRecord log = new LogRecord(
					Util_time.get_current_Calendar(),
					LogType.LOG_TYPE_ERROR,
					"Unexpected Throwable occured when running function in \"CmdShell.Aspect.around_shell_cmd_rm\""
					);
			FileLogSystem.log_write_line(log.toString());
		}
	}
	
	@Around("execution(** ng.cmd.ICmdShell.cmd_cat(..))")
	public void around_shell_cmd_cat(ProceedingJoinPoint point){
		//verify arguments' validity
		Object[] args = point.getArgs();
		String filename = (String)args[0];
		
		String filepath = null;
		if(FileSystem.if_path_absolute(filename)){
			if(!(FileSystem.if_file_exist(filename) && !FileSystem.if_file_is_dir(filename))){
				FileSystem.io_write_to_stderror_line("cannot resolve file : " + filename + ".");
				FileSystem.io_write_to_stderror_line("It maybe not exist or is a directory.");
				filepath = null;
			}
			else{
				filepath = filename;
			}
		}
		else if(FileSystem.if_path_relative(filename)){
			String ab_dir = FileSystem.combine_path(CmdShell.get_cwd_static(), filename);
			if(!(FileSystem.if_file_exist(ab_dir) && !FileSystem.if_file_is_dir(ab_dir))){
				FileSystem.io_write_to_stderror_line("cannot resolve file : " + filename + ".");
				FileSystem.io_write_to_stderror_line("It maybe not exist or is a directory.");
				filepath = null;
			}else{
				filepath = ab_dir;
			}
		}
		else{
			FileSystem.io_write_to_stderror_line("wrong file input : " + filename);
			filepath = null;
		}
		// read the content
		if(filepath != null){
			args[0] = FileSystem.io_read_from_file_all( filepath );
			if( ((String)args[0]).contains( new String("JAVA") ) ){
				FileSystem.io_write_to_console_line("JAVA found in this file");
			}
		}
		else{
			args[0] = null;
		}
		//run cmd_cat() function
		try {
			point.proceed( args );
		} catch (Throwable e) {
			e.printStackTrace();
			LogRecord log = new LogRecord(
					Util_time.get_current_Calendar(),
					LogType.LOG_TYPE_ERROR,
					"Unexpected Throwable occured when running function \"CmdShell.Aspect.around_shell_cmd_cat\""
					);
			FileLogSystem.log_write_line(log.toString());
		}
		
	}

	@Around("execution(** ng.cmd.ICmdShell.cmd_cd(..))")
	public void around_shell_cmd_cd(ProceedingJoinPoint point){
		//verify arguments' validity
		Object[] args = point.getArgs();
		String dir = (String)args[0];
		if(FileSystem.if_path_absolute(dir)){
			if(!(FileSystem.if_file_exist(dir) && FileSystem.if_file_is_dir(dir))){
				FileSystem.io_write_to_stderror_line("cannot resolve dir : " + dir);
				args[0] = null;
			}
		}
		else if(FileSystem.if_path_relative(dir)){
			String ab_dir = FileSystem.combine_path(CmdShell.get_cwd_static(), dir);
			if(!(FileSystem.if_file_exist(ab_dir) && FileSystem.if_file_is_dir(ab_dir))){
				FileSystem.io_write_to_stderror_line("cannot resolve dir : " + ab_dir);
				args[0] = null;
			}
		}
		else{
			FileSystem.io_write_to_stderror_line("wrong path input : " + dir);
			args[0] = null;
		}
		//run cmd_cd() function
		try {
			point.proceed( args );
		} catch (Throwable e) {
			e.printStackTrace();
			LogRecord log = new LogRecord(
					Util_time.get_current_Calendar(),
					LogType.LOG_TYPE_ERROR,
					"Unexpected Throwable occured when running function \"CmdShell.Aspect.around_shell_cmd_cd\""
					);
			FileLogSystem.log_write_line(log.toString());
		}
		//end around
	}
	
	@Before("execution(** ng.cmd.CmdShell.jms_client(..))")
	public void before_jms_client(){
		LogRecord log_rcd = new LogRecord(Util_time.get_current_Calendar(),
				LogType.LOG_TYPE_EXE,
				"jms client start");
		
		FileLogSystem.log_write_line(log_rcd.toString());
	}
}
