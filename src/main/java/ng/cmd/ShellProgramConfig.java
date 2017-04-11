package ng.cmd;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan
public class ShellProgramConfig {

	@Bean
	public CmdShell cmd_shell() {
		return new CmdShell();
	}
	
	@Bean
	public CmdShellFramework cmd_shell_fw(){
		return new CmdShellFramework();
	}
	
	@Bean
	public Audit audit(){
		return new Audit();
	}
}
