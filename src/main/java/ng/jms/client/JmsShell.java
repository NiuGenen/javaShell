package ng.jms.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.utils.json.JSONArray;
import org.apache.activemq.artemis.utils.json.JSONException;
import org.apache.activemq.artemis.utils.json.JSONObject;

import ng.cmd.FileSystem;
import ng.cmd.IShellFramework;
import ng.com.util.Util_http;

@SuppressWarnings("unused")
public class JmsShell implements IShellFramework{

	
	private Context namingContext;
	private Destination destination;
	private ConnectionFactory connectionFactory;
	private JMSContext context;
	private String userName;
	private String password;
	
    private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String DEFAULT_DESTINATION = "jms/topic/test";
    private static final String DEFAULT_USERNAME = "jms1";
    private static final String DEFAULT_PASSWORD = "jms1pwd";
    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String PROVIDER_URL = "http-remoting://127.0.0.1:8080";
	
	private Queue<String> msg_received = null;
	private JmsReceiveThreadTest jmsRT = null;
	@Override
	public void setup(Object o) {
		msg_received = new LinkedList<>();

		remote_get_topics();
	}
	
	private List<String> topics = null;
	private void remote_get_topics(){
		String ret = Util_http.sendPost("http://localhost:8080/shell_server/GetTopicsServlet",
				"posttime=" + System.currentTimeMillis() );
		
		write_to_shell_line(ret);
		
		try {
			JSONObject obj = new JSONObject(ret);
			
			JSONArray array = obj.getJSONArray("topics");
			topics = new LinkedList<String>( );
			for(int i = 0; i < array.length(); ++i){
				JSONObject t = array.getJSONObject(i);
				topics.add( t.getString("name") );
			}
			
		} catch (JSONException e) {
			write_to_shell_line("Failed to resolve JSON string in remote_get_topics");
			e.printStackTrace();
			jms_exit();
		}
		
	}
	
	private void jms_topics(){
		for(String topic: topics){
			write_to_shell_line("Topic: " + topic);
		}
	}
	
	private void jms_send(String[] cmds){
		if(cmds.length < 2) return;
		
		int count = cmds.length;
		for (int i = 1; i < count; i++) {
			String content = cmds[i];
            context.createProducer().send(destination, content);
        }
		
	}
	
	private void jms_read(String[] cmds){
		String msg = null;
		synchronized(msg_received){
			 msg = msg_received.poll();
		}
		if(msg != null) write_to_shell_line("msg:" + msg);
		else write_to_shell_line("no message now.");
	}
	
	private void jms_exit(){
		try {
			if(namingContext != null)
				namingContext.close();
			if(jmsRT != null){
				jmsRT.exit();
			}
			jms_running = false;
		} catch (NamingException e) {
			e.printStackTrace();
			write_to_shell("failed to close naming context");
		}
	}
	
	public void dispatch_cmd(String[] cmds) throws IOException{
		
		if(cmds == null || cmds.length == 0) return ;
		
		String command = cmds[0];
		switch( command ){
		case "topics":	jms_topics();		break;
		case "send":	jms_send(cmds);		break;
		case "read":	jms_read(cmds);		break;
		case "exit":	jms_exit();			break;
		default:
			break;
		}
	}
	
	private String[] get_cmds_from_input(String input){
		String[] cmds = input.split(" ");
		return cmds;
	}
	private String read_from_shell(){
		String str = FileSystem.io_read_from_console_line();
		return str;
	}
	private void write_to_shell(String str){
		FileSystem.io_write_to_console(str);
	}
	private void write_to_shell_line(String str){
		FileSystem.io_write_to_console_line(str);
	}
	
	private boolean jms_running = true;
	
	@Override
	public void loop_start() {
		while(jms_running){
			write_to_shell( "jms client > " );
			String input  = read_from_shell();
			String[] cmds = get_cmds_from_input( input );
			try {
				dispatch_cmd( cmds) ;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String get_version() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String get_usage() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
