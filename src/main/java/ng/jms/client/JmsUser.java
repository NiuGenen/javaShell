package ng.jms.client;

import java.util.List;

public class JmsUser {
	
	private String username;
	private List<JmsNews> news_published;
	
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

}
