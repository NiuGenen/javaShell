package ng.jms.client;

public class JmsNews {

	private String title;
	private String content;
	private String username;
	private String topicname;
	private long timestramp;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getTopicname() {
		return topicname;
	}
	public void setTopicname(String topicname) {
		this.topicname = topicname;
	}
	public long getTimestramp() {
		return timestramp;
	}
	public void setTimestramp(long timestramp) {
		this.timestramp = timestramp;
	}
}
