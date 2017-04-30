package ng.jms.client;

import java.util.Date;

import org.apache.activemq.artemis.utils.json.JSONException;
import org.apache.activemq.artemis.utils.json.JSONObject;

public class JmsNews {

	private String title;
	private String content;
	private String username;
	private int userid;
	private String topicname;
	private int topicid;
	private long timestramp;
	
	public JmsNews(){}
	
	public JmsNews(JSONObject obj) throws JSONException{
		title 		= 	obj.getString(	"title"		);
		content 	= 	obj.getString(	"cntnt"		);
		username 	= 	obj.getString(	"username"	);
		userid 		= 	obj.getInt(		"userid"	);
		topicname 	= 	obj.getString(	"topicname"	);
		topicid 	= 	obj.getInt(		"topicid"	);
		timestramp 	= 	obj.getLong(	"time"		);
	}
	
	public JSONObject toJSON(){
		JSONObject json_news = new JSONObject();
		try{
			json_news.put("userid"		, 	this.getUserid() 		);
			json_news.put("username"	, 	this.getUsername() 		);
			json_news.put("title"		,	this.getTitle() 		);
			json_news.put("cntnt"		,	this.getContent() 		);
			json_news.put("time"		,	this.getTimestramp() 	);
			json_news.put("topicid"		, 	this.getTopicid() 		);
			json_news.put("topicname"	, 	this.getTopicname() 	);
		} catch(JSONException e){
			e.printStackTrace();
			return null;
		}
		return json_news;
	}
	
	public String toString(){
		Date time = new Date(timestramp);
		
		return 	"---------------------------" + "\n" +
				"News On Topic: " + topicname + "\n" +
				"Title   : " + title + "\n" +
				"Author  : " + username + "\n" +
				"Time    : " + time.toString() + "\n" +
				"---------------------------" + "\n" +
				content + "\n";
	}
	
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
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public int getTopicid() {
		return topicid;
	}
	public void setTopicid(int topicid) {
		this.topicid = topicid;
	}
}
