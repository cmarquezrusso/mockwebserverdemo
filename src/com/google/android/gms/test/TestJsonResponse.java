/**
 * 
 */
package com.google.android.gms.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.MockWebServer;
import com.google.mockwebserver.RecordedRequest;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author Cristian Marquez <cristian04@gmail.com>
 *
 */
public class TestJsonResponse  extends TestCase{
	
	public void testWithoutMock() throws ClientProtocolException, IOException, JSONException
	{
        sendRequest("http://validate.jsontest.com/?json=%7B%22key%22:%22value%22");
	}
	
	public void testWithMock() throws IOException, JSONException, InterruptedException
	{
		MockWebServer server = new MockWebServer();
		MockResponse correoArgentinoResponse = new MockResponse();
		correoArgentinoResponse.addHeader("Content-Type", "application/json; charset=ISO-8859-1");
		correoArgentinoResponse.setBody("{\"error\":\"Expected a ',' or '}' at 15 [character 16 line 1]\",\"object_or_array\":\"object\",\"error_info\":\"This error came from the org.json reference parser.\",\"validate\":false}");
		server.enqueue(correoArgentinoResponse);
        server.play();

        sendRequest(server.getUrl("/").toString());
        
        //Verify if the request was OK
        RecordedRequest request = server.takeRequest();
        assertEquals("GET", request.getMethod());
        assertTrue(request.getHeaders().contains("Content-Type: text/plain; charset=utf-8"));
        server.shutdown();
        
	}
	
	/**
	 * A simple method that GET  a given url and make some assertions
	 * @param url
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 */
	private void sendRequest(String url) throws ClientProtocolException, IOException, JSONException
	{
	    DefaultHttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        get.addHeader("Content-Type", "text/plain; charset=utf-8");
        HttpResponse response = client.execute(get);
        String json = EntityUtils.toString(response.getEntity());
        assertEquals(HttpURLConnection.HTTP_OK, response.getStatusLine().getStatusCode());
        JSONObject responseJson = new JSONObject(json);
        Assert.assertEquals("Expected a ',' or '}' at 15 [character 16 line 1]", responseJson.get("error"));
        Assert.assertEquals("object", responseJson.get("object_or_array"));
        Assert.assertEquals("This error came from the org.json reference parser.", responseJson.get("error_info"));
        Assert.assertEquals(false, responseJson.get("validate"));
	}
	

}
