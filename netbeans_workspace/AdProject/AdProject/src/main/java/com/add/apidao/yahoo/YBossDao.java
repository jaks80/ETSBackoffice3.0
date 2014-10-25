package com.add.apidao.yahoo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Yusuf
 */
public class YBossDao {

    private static final Logger log = Logger.getLogger(YBossDao.class);

    protected static String yahooServer = "http://yboss.yahooapis.com/ysearch/";
    private static String consumer_key = "dj0yJmk9MGFRbXpFU2ZUZmVxJmQ9WVdrOVVqWkRkbTFRTXpBbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD02Zg--";
    private static String consumer_secret = "49e686924e610487ee5253895a7b5d20535d7aac";

    private static StHttpRequest httpRequest = new StHttpRequest();
    private static final String ENCODE_FORMAT = "UTF-8";
    private static final String callType = "related";
    private static final int HTTP_STATUS_OK = 200;

    private String searchString = "";

    public List<String> getSuggestedKeywords(String jsonResult) {
        
        List<String> suggestions = new ArrayList<>();
        
        try {
            JSONObject jo = new JSONObject(jsonResult);
            jo = jo.getJSONObject("bossresponse");
            jo = jo.getJSONObject("related");
            JSONArray ja = jo.getJSONArray("results");

            
            for (int i = 0; i < ja.length(); i++) {
                JSONObject resultObject = ja.getJSONObject(i);
                suggestions.add(resultObject.getString("suggestion"));
            }
        } catch (Exception e) {
            log.info("Error", e);
        }
        
        return suggestions;
    }
    
    public String returnHttpData()
            throws UnsupportedEncodingException,
            Exception {

        if (this.isConsumerKeyExists() && this.isConsumerSecretExists()) {
            String params = callType;
            params = params.concat("?q=");
            params = params.concat(URLEncoder.encode(this.getSearchString(), "UTF-8"));
            String url = yahooServer + params;
            OAuthConsumer consumer = new DefaultOAuthConsumer(consumer_key, consumer_secret);
            httpRequest.setOAuthConsumer(consumer);

            try {                
                System.out.println("Sending request to yahoo for: "+searchString + URLDecoder.decode(url, ENCODE_FORMAT));
                int responseCode = httpRequest.sendGetRequest(url);

// Send the request
                if (responseCode == HTTP_STATUS_OK) {                    
                    System.out.println("Response ");
                } else {
                    System.out.println("Error in response due to status code = " + responseCode);                    
                }
                //httpRequest.getResponseBody();

            } catch (UnsupportedEncodingException e) {
                System.out.println("Encoding/Decording error");                
            } catch (IOException e) {                
                log.error("Error with HTTP IO", e);
            } catch (Exception e) {
                //log.error(httpRequest.getResponseBody(), e);
                System.out.println(httpRequest.getResponseBody()+ e);
                return "";
            }

        } else {
            log.error("Key/Secret does not exist");
        }
        return httpRequest.getResponseBody();
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getSearchString() {
        return this.searchString;
    }

    private boolean isConsumerKeyExists() {
        if (consumer_key.isEmpty()) {
            log.error("Consumer Key is missing. Please provide the key");
            return false;
        }
        return true;
    }

    private boolean isConsumerSecretExists() {
        if (consumer_secret.isEmpty()) {
            log.error("Consumer Secret is missing. Please provide the key");
            return false;
        }
        return true;
    }
}
