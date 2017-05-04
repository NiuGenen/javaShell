package ng.jms.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
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

	private Context namingContext;					//create by me
	private Destination destination;				//create by naming context via JNDI
	private ConnectionFactory connectionFactory;	//create by naming context via factory name
	private JMSContext jms_context;						//create by connect factory via user name and password
	private JMSProducer jms_producer;
	private String userName;
	private String password;
	
    private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String DEFAULT_USERNAME = "jms1";
    private static final String DEFAULT_PASSWORD = "jms1pwd";
    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String PROVIDER_URL = "http-remoting://127.0.0.1:8080";
	
	@Override
	public void setup(Object o) {
		write_to_shell_line("setup jms client...");

		remote_get_topics();
		
		try{
			String userName = System.getProperty("username", DEFAULT_USERNAME);
        	String password = System.getProperty("password", DEFAULT_PASSWORD);
        	
        	 final Properties env = new Properties();
             env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
             env.put(Context.PROVIDER_URL, System.getProperty(Context.PROVIDER_URL, PROVIDER_URL));
             env.put(Context.SECURITY_PRINCIPAL, userName);
             env.put(Context.SECURITY_CREDENTIALS, password);
             namingContext = new InitialContext(env);
             

             String connectionFactoryString = System.getProperty("connection.factory", DEFAULT_CONNECTION_FACTORY);
             connectionFactory = (ConnectionFactory) namingContext.lookup(connectionFactoryString);

             //String destinationString = System.getProperty("destination", DEFAULT_DESTINATION);
             //Destination destination = (Destination) namingContext.lookup(destinationString);
             
             jms_context = connectionFactory.createContext(userName, password);
             jms_producer = jms_context.createProducer();
             //context.createProducer().send(destination, content);
             //consumer.receiveBody(String.class, 5000);
             
		} catch(NamingException e){
			write_to_shell_line("Fail to connect to jms server.");
			e.printStackTrace();
			jms_exit();
		}
	}
	
	/*
	 * show information user want to know
	 */
	private void jms_show(String[] cmds){
		if(cmds.length < 2){
		}
		
		switch(cmds[1]){
		case "topic":
			for(String topic: topics_map_id.keySet()){
				write_to_shell_line("Topic: " + topic);
			}
			break;
		case "publish":
			if (user != null) {
				if(user.getNews_published() != null){
					for (JmsNews news : user.getNews_published()) {
						Date time = new Date(news.getTimestramp());
						write_to_shell_line(
										"-----------------\n" + 
										"Time  = " + time.toString() + "\n" + 
										"Topic = " + news.getTopicname() + "\n" +
										"Title = " + news.getTitle());
					}
				}
				else{
					write_to_shell_line("You haven't published any news.");
				}
			}
			else{	//user == null
				write_to_shell_line("Please login first.");
			}
			break;
		case "subscribe":
			if(user != null){
				if(user.getTopic_subscribed() != null){
					for(String topic: user.getTopic_subscribed()){
						write_to_shell_line("Subscribed topic : " + topic);
					}
				}
				else{
					write_to_shell_line("You haven't subscribed any topic.");
				}
			}
			else{	//user == null
				write_to_shell_line("Please login first.");
			}
			break;
		}
	}
	
	private void jms_exit(){
		try {
			if(namingContext != null)
				namingContext.close();
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
	private Map<String, Queue<JmsNews>> topics_map_msg_rec = null;
	private Map<String, JmsReceiveThread> topics_map_thread = null;
	
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
			
			if(success == 1){	//login succeed
				online = true;
				write_to_shell_line("login succeed.");
				if(user == null) user = new JmsUser();
				//set user's name
				user.setUsername(username);
				//set user's topics 
				JSONArray topics = obj.getJSONArray("topics");
				if(user.getTopic_subscribed() == null)
					user.setTopic_subscribed(new LinkedList<String>());
				for(int i = 0; i < topics.length(); ++i){
					String topic_name = topics.getString(i);
					user.getTopic_subscribed().add( topic_name );
					write_to_shell_line("Topic subscribed: " + topic_name );
				}
				//set user's unique id
				int userid = obj.getInt("userid");
				user.setUserid(userid);
				//set user's published news
				JSONArray news = obj.getJSONArray("news");
				if(user.getNews_published() == null)
					user.setNews_published(new LinkedList<JmsNews>());
				for(int i = 0; i < news.length(); ++i){
					JSONObject json_news = news.getJSONObject(i);
					JmsNews jms_news = new JmsNews(json_news);
					user.getNews_published().add(jms_news);
				}
				//build up topic map message queue to receive and its thread
				JmsReceiveThreadCreatThread th = new JmsReceiveThreadCreatThread();
				if(topics_map_msg_rec == null)	//map: topic name -> queue to receive
					topics_map_msg_rec = new HashMap<String, Queue<JmsNews>>();
				if(topics_map_thread == null)//map: topic name -> receive thread
					topics_map_thread = new HashMap<String, JmsReceiveThread>();
				th.setup(user.getTopic_subscribed(),
						topics_map_msg_rec,	//sync
						topics_map_dest, 	//sync
						topics_map_thread,	//sync
						namingContext,
						jms_context);
				th.start();
			}
			else{	//login failed
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
			if(topics_map_thread != null){
				for(JmsReceiveThread th: topics_map_thread.values()){
					th.exit();
				}
			}
			topics_map_msg_rec = null;
			topics_map_thread = null;
		}
		else{
			write_to_shell_line("Please login first.");
		}
	}
	private void jms_reg(String[] cmds){
		if(online){
			write_to_shell_line("Please register new user when you are offline.");
			return;
		}
		
		String username = cmds[1];
		String passwd = cmds[2];
		
		String ret = Util_http.sendPost(
				"http://localhost:8080/shell_server/UserRegServlet",
				"username=" + username + "&" +
				"password=" + passwd );
		
		try{
			JSONObject obj = new JSONObject(ret);
			
			boolean success = obj.getBoolean("register");
			
			if(success){
				write_to_shell_line("Register succeed.");
			}
			else{
				write_to_shell_line("Register failed.");
			}
			
		}catch(JSONException e){
			write_to_shell_line("Failed to resolve JSON string in jms_reg.");
			write_to_shell_line("There is a posibility that you failed to register.");
			return;
		}
	}
	
	/*
	 * publish & subscribe & read
	 */
	private Map<String, Integer> topics_map_id = null;
	private Map<String, Destination> topics_map_dest = null;
	private void remote_get_topics(){
		String ret = Util_http.sendPost(
				"http://localhost:8080/shell_server/GetTopicsServlet",
				"posttime=" + System.currentTimeMillis() );
		//get returned json {'topics':[{'id':'','name':''},{...}]}
		write_to_shell_line("remote_get_topics : " + ret);
		
		try {
			JSONObject obj = new JSONObject(ret);
			
			JSONArray array = obj.getJSONArray("topics");//get topics array
			
			if(topics_map_id == null)	//map: topic name -> topic id
				topics_map_id = new HashMap<String, Integer>( );
			if(topics_map_dest == null)	//map: topic name -> destination to publish 
				topics_map_dest = new HashMap<String, Destination>();
			
			//build up topic map id
			for(int i = 0; i < array.length(); ++i){
				JSONObject t = array.getJSONObject(i);
				
				String topicname = t.getString("name");
				int topicid = t.getInt("id");
				
				topics_map_id.put(topicname, topicid);
			}
			
			//lazy topic map destination : build destination when publish or when receive thread is created
			
		} catch (JSONException e) {
			write_to_shell_line("Failed to resolve JSON string in remote_get_topics");
			e.printStackTrace();
			jms_exit();
		}
		
	}
	private void jms_publish(String[] cmds){
		if(cmds.length < 4){	//publish [topic] [title] [content]
			return;
		}
		String topicname = cmds[1];
		String title = cmds[2];
		String content = cmds[3];
		
		if(!topics_map_id.containsKey( topicname )){
			write_to_shell_line("This topic '" + topicname + "' not exist.");
			return;
		}
		
		//get destination
		Destination dest = null;
		synchronized(topics_map_dest){
			dest = topics_map_dest.get(topicname);
			if(dest == null){
				try {
					dest = (Destination) namingContext.lookup("jms/topic/" + topicname );
				} catch (NamingException e) {
					write_to_shell_line("Fail to create destination for this topic.");
					e.printStackTrace();
					return;
				}
				topics_map_dest.put(topicname, dest);
			}
		}
		
		//create msssage
		JmsNews jms_news = new JmsNews();
		jms_news.setTopicname( topicname );
		jms_news.setTopicid(topics_map_id.get(topicname) );
		jms_news.setTimestramp( System.currentTimeMillis() );
		jms_news.setUsername( user.getUsername() );
		jms_news.setUserid( user.getUserid() );
		jms_news.setTitle(title);
		jms_news.setContent(content);
		
		//send message
		try{
			JSONObject json_news = jms_news.toJSON();
			if(json_news != null)
				jms_producer.send(dest, json_news.toString() );
			else{
				write_to_shell_line("Fail to send message.");
				return;
			}
		}catch(Exception e){
			write_to_shell_line("Fail to send message.");
			e.printStackTrace();
			return;
		}
		
		//add to current user's published news
		if(user.getNews_published() == null){
			user.setNews_published(new LinkedList<JmsNews>());
		}
		user.getNews_published().add(jms_news);
	}
	private void jms_subscribe(String[] cmds){	//sub topic
		if(cmds.length < 2){
			return;
		}
		String topicname = cmds[1];
		
		if(user.getTopic_subscribed().contains(topicname)){
			write_to_shell_line("You already have subscribed topic [" + topicname +"].");
			return;
		}
		
		String ret = Util_http.sendPost(
				"http://localhost:8080/shell_server/UserSubscribeServlet",
				"userid=" + user.getUserid() +"&" +
				"topicid=" + topics_map_id.get(topicname) +"&" +
				"topicname=" + topicname + "&" +
				"subscribe=True" );
		
		try {
			JSONObject obj = new JSONObject( ret );
			if(Boolean.parseBoolean( obj.getString("subscribe"))){
				write_to_shell_line("Subscribe succeed.");
				user.getTopic_subscribed().add( topicname );
				
				write_to_shell_line("processing topic [" + topicname + "] in jms_subscribe.");
				//get destination
				Destination dest = null;
				synchronized(topics_map_dest){
					dest = topics_map_dest.get(topicname);
					if(dest == null){
						try {
							dest = (Destination) namingContext.lookup("jms/topic/" + topicname );
						} catch (NamingException e) {
							write_to_shell_line("Fail to create destination for topic :" + topicname + " in jms_subscribe.");
							e.printStackTrace();
						}
						write_to_shell_line("Create destination for topic :" + topicname + " in jms_subscribe.");
					}
					topics_map_dest.put(topicname, dest);
				}
				//create queue
				Queue<JmsNews> queue = new LinkedList<JmsNews>();
				//set queue
				synchronized(topics_map_msg_rec){
					topics_map_msg_rec.put(topicname, queue);
				}
				//create thread for this queue
				JmsReceiveThread th = new JmsReceiveThread();
				th.setup(topicname, queue, jms_context.createConsumer(dest));
				th.start();
				//save thread
				topics_map_thread.put(topicname, th);
			}
			else{
				write_to_shell_line("Subscribe maybe fail.");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	private void jms_unsubscribe(String[] cmds){
		if(cmds.length < 2){
			return;
		}
		String topicname = cmds[1];
		
		if(!user.getTopic_subscribed().contains(topicname)){
			write_to_shell_line("You haven't subscribed topic [" + topicname + "].");
			return ;
		}
		
		String ret = Util_http.sendPost(
				"http://localhost:8080/shell_server/UserSubscribeServlet",
				"userid=" + user.getUserid() +"&" +
				"topicid=" + topics_map_id.get(topicname) +"&" +
				"topicname=" + topicname + "&" +
				"subscribe=False" );
		
		try {
			JSONObject obj = new JSONObject( ret );
			if(Boolean.parseBoolean( obj.getString("subscribe"))){
				write_to_shell_line("Unsubscribe succeed.");
				user.getTopic_subscribed().remove( topicname );
				//receive thread for this topic shoule exit
				JmsReceiveThread th = topics_map_thread.get(topicname);
				if(th != null){
					th.exit();
					topics_map_thread.remove(topicname);
				}
				
			}
			else{
				write_to_shell_line("Unsubscribe maybe fail.");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	private void jms_read(String[] cmds){
		if(cmds.length < 2){
			return;
		}
		String topicname = cmds[1];
		
		if(!user.getTopic_subscribed().contains(topicname)){
			write_to_shell_line("You haven't subscribed this topic : " + topicname );
			return;
		}
		
		Queue<JmsNews> queue = topics_map_msg_rec.get(topicname);
		if(queue != null){
			JmsNews news = queue.poll();
			if(news == null){
				write_to_shell_line("There is no more news.");
				return;
			}
			write_to_shell( news.toString() );
		}
		else{	//queue == null
			write_to_shell_line("Fail to get queue for this topic.");
		}
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
			"	show publish" + "\n" +
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
