/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ng.ws.client;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A Client stub to the HelloWorld JAX-WS Web Service.
 *
 * @author lnewson@redhat.com
 */
public class Client implements NewsWebService {
    private NewsWebService newsService;

    /**
     * Default constructor
     *
     * @param url The URL to the NewsWebService WSDL endpoint.
     */
    public Client(final URL wsdlUrl) {
        //QName serviceName = new QName("http://www.jboss.org/eap/quickstarts/wshelloworld/HelloWorld", "HelloWorldService");
    	//QName serviceName = new QName("http://www.wildfly.org/quickstarts/wshelloworld/HelloWorld", "HelloWorldService");
    	QName serviceName = new QName("http://www.shell.server/ng/ws/NewsWeb", "NewsWebService");
    	
        Service service = Service.create(wsdlUrl, serviceName);
        newsService = service.getPort(NewsWebService.class);
        assert (newsService != null);
    }

    /**
     * Default constructor
     *
     * @param url The URL to the Hello World WSDL endpoint.
     * @throws MalformedURLException if the WSDL url is malformed.
     */
    public Client(final String url) throws MalformedURLException {
        this(new URL(url));
    }

	@Override
	public String testWS() {
		return newsService.testWS();
	}

	@Override
	public boolean publish(String jsonNews_publish) {
		return newsService.publish(jsonNews_publish);
	}

	@Override
	public String getNews(String key) {
		return newsService.getNews(key);
	}

	@Override
	public boolean modifyNews(String op, long newsid, String n) {
		return newsService.modifyNews(op, newsid, n);
	}

	@Override
	public String getNewsByid(long newsid) {
		return newsService.getNewsByid(newsid);
	}
}
