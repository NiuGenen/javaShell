package ng.jms.client;

import java.util.List;

public class JmsUser {
	
	private int userid;
	private String username;
	private List<JmsNews> news_published;
	private List<String> topic_subscribed;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public List<JmsNews> getNews_published() {
		return news_published;
	}
	public void setNews_published(List<JmsNews> news_published) {
		this.news_published = news_published;
	}
	public List<String> getTopic_subscribed() {
		return topic_subscribed;
	}
	public void setTopic_subscribed(List<String> topic_subscribed) {
		this.topic_subscribed = topic_subscribed;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}

}
