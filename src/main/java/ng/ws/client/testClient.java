package ng.ws.client;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.apache.activemq.artemis.utils.json.JSONArray;
import org.apache.activemq.artemis.utils.json.JSONException;
import org.apache.activemq.artemis.utils.json.JSONObject;
import org.junit.*;

public class testClient {
	private static final String APP_NAME = "shell_server";
    private static final String WSDL_PATH = "NewsWebService?wsdl";
    private static final String SERVER_URL_PROPERTY = "serverUrl";
    private static final String DEFAULT_SERVER_URL = "http://localhost:8080/";
    private static URL deploymentUrl;

    private NewsWebService client;
    
    @BeforeClass
    public static void before_class() throws MalformedURLException{

        String deploymentUrl = System.getProperty(SERVER_URL_PROPERTY);
        if (deploymentUrl == null || deploymentUrl.isEmpty()) {
            deploymentUrl = DEFAULT_SERVER_URL;
        }
        if (!deploymentUrl.endsWith("/")) {
            deploymentUrl += "/";
        }
        if (!deploymentUrl.matches(".*" + APP_NAME + ".*"))
        {
            deploymentUrl += APP_NAME + "/";
        }
        deploymentUrl += WSDL_PATH;

        System.out.println("WSDL Deployment URL: " + deploymentUrl);

        // Set the deployment url
        testClient.deploymentUrl = new URL(deploymentUrl);
    }
    
	@Before
	public void setup() {
		try {
            client = new Client(new URL(deploymentUrl, WSDL_PATH));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
		
		try{
			news_json_list = new LinkedList<>();
			for(int i = 0; i < 5; ++i){
				JSONObject n = new JSONObject();
				n.put("title", "title" + i);
				n.put("content", "content" + i);
				n.put("time", System.currentTimeMillis());
				news_json_list.add(n);
			}
			for(int i = 0; i < 5; ++i){
				JSONObject n = new JSONObject();
				n.put("title", "news" + i);
				n.put("content", "newsnews" + i);
				n.put("time", System.currentTimeMillis());
				news_json_list.add(n);
			}
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
	
	private List<JSONObject> news_json_list;
	
	@Test
	public void test_news() throws JSONException{
		System.out.println("[Client] Requesting the WebService to Publish news.");
		for(JSONObject n: news_json_list){
			System.out.println("[publish]" + n.toString());
			if( client.publish(n.toString()) ){
				System.out.println("[publish] success");
			}
			else{
				System.out.println("[publish] fail");
			}
		}
		System.out.println();
		
		System.out.println("[Client] Requesting the WebService to get news.");
		String json = client.getNews("title");
		JSONObject news = new JSONObject(json);
		System.out.println("[get] count = " + news.getInt("count") );
		JSONArray list = news.getJSONArray("news");
		for(int i = 0; i < list.length(); ++i){
			JSONObject n = list.getJSONObject(i);
			System.out.println("[get] news : " + n.toString());
		}
		System.out.println();
		
		System.out.println("[Client] Requesting the WebService to update news.");
		JSONObject n = list.getJSONObject(0);
		System.out.println("[update] before : " + n.toString());
		n.put("title", "modified_title");
		n.put("content","modified_content");
		client.modifyNews("update", n.getLong("id"), n.toString());
		String new_n = client.getNewsByid(n.getLong("id"));
		System.out.println("[update] after : " + new_n.toString());
		System.out.println();
	}
	
	@Test
	public void test_test(){

        System.out.println("[Client] Requesting the WebService to Test WS.");

        final String response = client.testWS();
        assertEquals(response, "Welcome visit web service");

        System.out.println("[WebService] " + response);
	}
}
