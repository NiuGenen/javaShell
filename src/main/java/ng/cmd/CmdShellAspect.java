package ng.cmd;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import ng.com.util.Util_time;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Component
@Aspect
public class CmdShellAspect {
	
	@Around("execution(** ng.cmd.IShell.cmd_rm(..))")
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
	
	@Around("execution(** ng.cmd.IShell.cmd_cat(..))")
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

	@Around("execution(** ng.cmd.IShell.cmd_cd(..))")
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
	
}
