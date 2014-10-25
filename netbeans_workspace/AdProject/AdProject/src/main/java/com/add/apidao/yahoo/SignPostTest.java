package com.add.apidao.yahoo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;

/**
 * Sample code to use Yahoo! Search BOSS
 *
 * Please include the following libraries 1. Apache Log4j 2. oAuth Signpost
 *
 * @author xyz
 */
public class SignPostTest {

    private static final Logger log = Logger.getLogger(SignPostTest.class);

    protected static String yahooServer = "http://yboss.yahooapis.com/ysearch/";
    private static String consumer_key = "dj0yJmk9Q2ZWU0lIOXhyZm1KJmQ9WVdrOVlVeFhja2t3TkhVbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD1kMA--";
    private static String consumer_secret = "a4d112b1a4a2b627303c59d89aa0e0700c26b09b";
    
    private static StHttpRequest httpRequest = new StHttpRequest();
    private static final String ENCODE_FORMAT = "UTF-8";
    private static final String callType = "related";
    private static final int HTTP_STATUS_OK = 200;

    /**
     *
     * @return
     */
    public int returnHttpData()
            throws UnsupportedEncodingException,
            Exception {

        if (this.isConsumerKeyExists() && this.isConsumerSecretExists()) {

// Start with call Type
            String params = callType;

// Add query
            params = params.concat("?q=");

// Encode Query string before concatenating
            params = params.concat(URLEncoder.encode(this.getSearchString(), "UTF-8"));

// Create final URL
            String url = yahooServer + params;

// Create oAuth Consumer
            OAuthConsumer consumer = new DefaultOAuthConsumer(consumer_key, consumer_secret);

// Set the HTTP request correctly
            httpRequest.setOAuthConsumer(consumer);

            try {
                log.info("sending get request to" + URLDecoder.decode(url, ENCODE_FORMAT));
                int responseCode = httpRequest.sendGetRequest(url);

// Send the request
                if (responseCode == HTTP_STATUS_OK) {
                    log.info("Response ");
                } else {
                    log.error("Error in response due to status code = " + responseCode);
                }
                log.info(httpRequest.getResponseBody());

            } catch (UnsupportedEncodingException e) {
                log.error("Encoding/Decording error");
            } catch (IOException e) {
                log.error("Error with HTTP IO", e);
            } catch (Exception e) {
                log.error(httpRequest.getResponseBody(), e);
                return 0;
            }

        } else {
            log.error("Key/Secret does not exist");
        }
        return 1;
    }

    private String getSearchString() {
        return "bangladesh+video";
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

    /**
     * @param args
     */
    public static void main(String[] args) {

        BasicConfigurator.configure();

        try {

            SignPostTest signPostTest = new SignPostTest();

            signPostTest.returnHttpData();

        } catch (Exception e) {
            log.info("Error", e);
        }
    }

}
