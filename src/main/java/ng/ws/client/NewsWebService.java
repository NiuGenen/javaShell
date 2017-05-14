package ng.ws.client;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService(targetNamespace = "http://www.shell.server/ng/ws/NewsWeb")
public interface NewsWebService {
	
	/**
     * test ws as a response
     *
     * @return A simple ws message
     */
    @WebMethod
    public String testWS();

    /**
     * publish one news
     * 
     * @param jsonnews news in json format. Key:title,content,time
     * @return
     */
    @WebMethod
    public boolean publish(String jsonNews_publish);

    /**
     * get news according to key
     * 
     * @param key key word to get news
     * @return json format with news
     */
    @WebMethod
    public String getNews(String key);

    /**
     * modify one news
     * 
     * @param op ='delete' or ='update'
     * @param newsid get through getNews() method
     * @param n JSON news. Key:title,content,time. useless when ${op}=='delete'
     * @return true if succeed to ${op}
     */
    @WebMethod
    public boolean modifyNews(String op, long newsid, String n);

    /**
     * get one news according to newsid
     * 
     * @param newsid newsid obtained by other getNews()
     * @return news in json format. key:title,content,time
     */
    @WebMethod
    public String getNewsByid(long newsid);
}
