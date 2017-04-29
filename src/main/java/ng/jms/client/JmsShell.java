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
	
	/*
	 * show information user want to know
	 */
	private void jms_show(String[] cmds){
		if(cmds.length < 2){
		}
		
		switch(cmds[1]){
		case "topic":
			for(String topic: topics){
				write_to_shell_line("Topic: " + topic);
			}
			break;
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
	
	private void jms_help(){
		write_to_shell_line( get_usage() );
	}
	private void jms_version(){
		write_to_shell_line( get_version() );
	}
	
	/*
	 * user management
	 */
	private boolean online = false;
	private JmsUser user = null;
	
	private void jms_login(String[] cmds){
		String username = null;
		String passwd = null;
		
		if(cmds.length < 3){	//login username passwd
			return;
		}else{
			username = cmds[1];
			passwd = cmds[2];
		}
		
		String ret = Util_http.sendPost(
				"http://localhost:8080/shell_server/UserLoginServlet", 
				"username=" + username +"&passwd="+passwd);
		
		write_to_shell_line("login : " + ret);
		
		try {
			JSONObject obj = new JSONObject(ret);
			
			int success = obj.getInt("login");
			
			if(success == 1){
				online = true;
				write_to_shell_line("login success.");
				if(user == null){
					user = new JmsUser();
				}
				user.setUsername(username);
			}
			else{
				write_to_shell_line("login failed. please try again.");
			}
			
		} catch (JSONException e) {
			write_to_shell_line("Failed to resolve JSON string in jms_login");
			e.printStackTrace();
		}
	}
	private void jms_logout(){
		if(online){
			online = false;
			user = null;
		}
		else{
			write_to_shell_line("Please login first.");
		}
	}
	private void jms_reg(String[] cmds){
		
	}
	
	/*
	 * publish & subscribe & read
	 */
	private List<String> topics = null;
	private void remote_get_topics(){
		String ret = Util_http.sendPost(
				"http://localhost:8080/shell_server/GetTopicsServlet",
				"posttime=" + System.currentTimeMillis() );
		
		write_to_shell_line("setup : " + ret);
		
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
	private void jms_publish(String[] cmds){	//pub topic title content
		if(cmds.length < 4){
			return;
		}
		String topicname = cmds[1];
		String title = cmds[2];
		String content = cmds[3];
	}
	private void jms_subscribe(String[] cmds){	//sub topic
		if(cmds.length < 2){
			return;
		}
		String topicname = cmds[1];
	}
	private void jms_unsubscribe(String[] cmds){
		if(cmds.length < 2){
			return;
		}
		String topicname = cmds[1];
	}
	private void jms_read(String[] cmds){
		if(cmds.length < 2){
			return;
		}
		String topicname = cmds[1];
	}

	public void dispatch_cmd(String[] cmds) throws IOException{
		
		if(cmds == null || cmds.length == 0) return ;
		
		String command = cmds[0];
		switch( command ){
		case "help":
		case "usage":		jms_help();				break;
		case "version":		jms_version();			break;
		case "show":		jms_show(cmds);			break;
		case "login":		jms_login(cmds);		break;
		case "logout":		jms_logout();			break;
		case "reg":			jms_reg(cmds);			break;
		case "pub":
		case "publish":		jms_publish(cmds);		break;
		case "sub":
		case "subscribe":	jms_subscribe(cmds);	break;
		case "ubsubscribe":
		case "ubsub":		jms_unsubscribe(cmds);	break;
		case "read":		jms_read(cmds);			break;
		case "exit":
		case "quit":		jms_exit();				break;
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
			write_to_shell( "jms client " + (online?user.getUsername():"") + " > " );
			String input  = read_from_shell();
			String[] cmds = get_cmds_from_input( input );
			try {
				dispatch_cmd( cmds) ;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private String version = "jms shell 0.1";
	private String usage = 	
			"usage for " + version + "\n" +
			"command [parameter][...]" + "\n" +
			"	show topic" + "\n" +
			"		get the topics existed" + "\n" +
			"	show published" + "\n" +
			"		get the news that you have published" + "\n" +
			"		only works at inline state" + "\n" +
			"	version" + "\n" +
			"		get the version of jms shell" + "\n" +
			"	login [username] [password]" + "\n" +
			"		login via username and password" + "\n" +
			"		only works at offline state" + "\n" +
			"	logout" + "\n" +
			"		only works at inline state" + "\n" +
			"	reg [username] [passwd]" + "\n" +
			"		register a new user" + "\n" +
			"		only works at offline state" + "\n" +
			"	publish [topicname] [title] [content]" + "\n" +
			"		publish one news on this topic" + "\n" +
			"	subscribe [topicname]" + "\n" +
			"		subscribe this topic" + "\n" +
			"	ubsubscribe [topicname]" + "\n" +
			"		ubsubscribe this topic" + "\n" +
			"	read [topicname]" + "\n" +
			"		if newer news is published on this topic, read it" + "\n" +
			"		only when this news is published while you are online" + "\n" +
			"	help (or 'usage')" + "\n" +
			"		get what you are reading" + "\n" +
			"	exit (or 'quit')" + "\n" +
			"		exit from jms shell"
								;

	@Override
	public String get_version() {
		return version;
	}

	@Override
	public String get_usage() {
		return usage;
	}

	
	
}
