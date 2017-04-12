package ng.cmd;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class CmdShellAspect {
	
	/*
	 * This class is abandoned
	 * its methods are moved to Audit
	 * because I found collision when two
	 * different aspect processing the same
	 * function.
	 * better to processing one function 
	 * in only one aspect class to avoid
	 * collision.
	 * 
	 * As for the problem occurred by it
	 * here is my experience and I abstract it:
	 * suppose this is one Bean and its function A()
	 * there are two aspect class AS1 and AS2
	 * in AS1, it use @Around to process Bean.A()
	 * in AS2, it use @Before to process Bean.A()
	 * then the @Around in AS1 will be execute two times
	 * the process order will be:
	 * @Before in AS2 -> @Around in AS1 -> @Around in AS1 -> Bean.A()
	 * which I found by debug it
	 */
}
