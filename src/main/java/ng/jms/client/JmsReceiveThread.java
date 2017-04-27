package ng.jms.client;

import java.util.Queue;

import javax.jms.JMSConsumer;

import ng.cmd.FileSystem;

public class JmsReceiveThread extends Thread{

	private Queue<String> buffer;
	private JMSConsumer consumer;
	
	public void setup(Queue<String> msg,JMSConsumer consumer){
		buffer = msg;
		this.consumer = consumer;
	}
	
	private boolean running = true;
	
	public void exit(){
		running = false;
	}
	
	@Override
	public void run() {
		FileSystem.io_write_to_console_line("jms listening start");
		try{
			while(running){
				String msg = consumer.receiveBody(String.class);
				synchronized(buffer){
					buffer.add(msg);
				}
			}
		}catch(Exception e){
			FileSystem.io_write_to_console_line("jms listening ended by unexpected failer");
		}
	}

}
