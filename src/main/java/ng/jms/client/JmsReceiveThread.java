package ng.jms.client;

import java.util.Queue;

import javax.jms.JMSConsumer;

import org.apache.activemq.artemis.utils.json.JSONException;
import org.apache.activemq.artemis.utils.json.JSONObject;

import ng.cmd.FileSystem;

public class JmsReceiveThread extends Thread{

	private Queue<JmsNews> buffer;
	private JMSConsumer consumer;
	private String topicname;
	
	public void setup(String topicname,
			Queue<JmsNews> msg,
			JMSConsumer consumer){
		this.topicname = topicname;
		buffer = msg;
		this.consumer = consumer;
	}
	
	private boolean running = true;
	
	public void exit(){
		running = false;
	}
	
	@Override
	public void run() {
		FileSystem.io_write_to_console_line("jms listening on topic [" + topicname + "] start");
		try{
			while(running){
				String msg = consumer.receiveBody(String.class);
				JmsNews jms_news = null;
				try{
					//topic title author time content
					JSONObject obj = new JSONObject(msg);
					
					jms_news = new JmsNews(obj);
					
				} catch(JSONException e){
					e.printStackTrace();
					continue;
				}
				if(jms_news != null){
					synchronized(buffer){
						buffer.add(jms_news);
					}
				}
			}
		}catch(Exception e){
			FileSystem.io_write_to_console_line("jms listening on topic [" + topicname + "] ended by exception.");
			e.printStackTrace();
		}
	}

}
