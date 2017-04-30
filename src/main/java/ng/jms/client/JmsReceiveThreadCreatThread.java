package ng.jms.client;

import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.naming.Context;
import javax.naming.NamingException;

import ng.cmd.FileSystem;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class JmsReceiveThreadCreatThread extends Thread{
	
	private List<String> topics_set = null;
	private Map<String, Queue<JmsNews>> topics_map_msg_rec = null;
	private Map<String, Destination> topics_map_dest = null;
	private Map<String, JmsReceiveThread> topics_map_thread = null;
	private JMSContext jms_context = null;
	private Context namingContext = null;
	
	public void setup(List<String> ts, 
			Map<String, Queue<JmsNews>> m,
			Map<String, Destination> d,
			Map<String, JmsReceiveThread> th,
			Context nc,
			JMSContext c){
		topics_set = ts;
		topics_map_msg_rec = m;
		topics_map_dest = d;
		topics_map_thread = th;
		jms_context = c;
		namingContext = nc;
	}
	
	@Override
	public void run() {
		for(String topicname: topics_set){
			FileSystem.io_write_to_console_line("processing topic [" + topicname + "] in JmsReceiveThreadCreatThread.");
			//get destination
			Destination dest = null;
			synchronized(topics_map_dest){
				dest = topics_map_dest.get(topicname);
				if(dest == null){
					try {
						dest = (Destination) namingContext.lookup("jms/topic/" + topicname );
					} catch (NamingException e) {
						FileSystem.io_write_to_console_line("Fail to create destination for topic :" + topicname + " in JmsReceiveThreadCreatThread.");
						e.printStackTrace();
						continue;
					}
					FileSystem.io_write_to_console_line("Create destination for topic :" + topicname + " in JmsReceiveThreadCreatThread.");
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
	}
}
