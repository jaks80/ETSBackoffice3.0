package com.ets.fe.util;

import com.amadeus.air.AIR;
import com.ets.fe.APIConfig;
import com.ets.fe.Application;
import com.ets.fe.a_main.Main;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Yusuf
 */
public class RestClientUtil {

    private static final String domain = APIConfig.get("ws.domain");
    private static final String AUTHORIZATION_PROPERTY = "Authorization";

    public synchronized static <T> T getEntity(final Class<T> type, String destUrl, T entity) {

        String apiOutput = getXML(destUrl);
        if (apiOutput != null && !apiOutput.isEmpty()) {
            entity = xmlToObject(type, entity, apiOutput);
        }
        return entity;
    }

    public synchronized static String getXML(String destUrl) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        int status = 0;
        String apiOutput = "";
        try {
            HttpGet httpget = new HttpGet(buildURL(destUrl));
            httpget.addHeader("accept", "application/xml");
            httpget.addHeader(AUTHORIZATION_PROPERTY, Application.getUserPassowrdEncoded());
            HttpResponse response = httpClient.execute(httpget);
            status = response.getStatusLine().getStatusCode();
            System.out.println("HTTP Status:>>"+status);
            
            if(status == 401){
             Main.getDlgLogin().showLoginDialog();             
            }
            
            
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                apiOutput = EntityUtils.toString(httpEntity,"UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {}
        
        return apiOutput;
    }

    public synchronized static Integer postAIR(String destUrl, AIR air) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        int status = 0;

        try {
            HttpPost httppost = new HttpPost(buildURL(destUrl));
            httppost.setEntity(objectToXML(AIR.class, air));
            httppost.addHeader("content-type", "application/xml");
            httppost.addHeader(AUTHORIZATION_PROPERTY, Application.getUserPassowrdEncoded());
            HttpResponse response = httpClient.execute(httppost);
            status = response.getStatusLine().getStatusCode();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        return status;
    }

    public synchronized static Integer deleteById(String destUrl) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        Integer status = 0;
        try {
            HttpDelete httpDelete = new HttpDelete(buildURL(destUrl));
            httpDelete.addHeader("accept", "application/xml");
            httpDelete.addHeader(AUTHORIZATION_PROPERTY, Application.getUserPassowrdEncoded());
            HttpResponse response = httpClient.execute(httpDelete);
            status = response.getStatusLine().getStatusCode();
            return status;
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return status;
    }

    public synchronized static <T> T postEntity(final Class<T> type, String destUrl, T entity) {
        T persistentEntity = null;
        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpPost httppost = new HttpPost(buildURL(destUrl));
            httppost.setEntity(objectToXML(type, entity));
            httppost.addHeader("content-type", "application/xml");
            httppost.addHeader("accept", "application/xml");
            httppost.addHeader(AUTHORIZATION_PROPERTY, Application.getUserPassowrdEncoded());

            HttpResponse response = httpClient.execute(httppost);
            HttpEntity httpEntity = response.getEntity();

            if (httpEntity != null) {
                String apiOutput = EntityUtils.toString(httpEntity);
                persistentEntity = xmlToObject(type, entity, apiOutput);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }

        return persistentEntity;
    }

    public synchronized static <T> Integer postEntityReturnStatus(final Class<T> type, String destUrl, T entity) {
        
        Integer status = 0;
        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpPost httppost = new HttpPost(buildURL(destUrl));
            httppost.setEntity(objectToXML(type, entity));
            httppost.addHeader("content-type", "application/xml");
            httppost.addHeader("accept", "application/xml");
            httppost.addHeader(AUTHORIZATION_PROPERTY, Application.getUserPassowrdEncoded());

            HttpResponse response = httpClient.execute(httppost);
            
             status = response.getStatusLine().getStatusCode();
            
        } catch (IOException e) {
            e.printStackTrace();
        } 
        
        return status;
    }
    
    public synchronized static <T> T putEntity(final Class<T> type, String destUrl, T entity) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        T persistentEntity = null;

        try {
            HttpPut httpPut = new HttpPut(buildURL(destUrl));
            httpPut.setEntity(objectToXML(type, entity));
            httpPut.addHeader("content-type", "application/xml");
            httpPut.addHeader("accept", "application/xml");
            httpPut.addHeader(AUTHORIZATION_PROPERTY, Application.getUserPassowrdEncoded());

            HttpResponse response = httpClient.execute(httpPut);
            HttpEntity httpEntity = response.getEntity();

            if (httpEntity != null) {
                String apiOutput = EntityUtils.toString(httpEntity);
                persistentEntity = xmlToObject(type, entity, apiOutput);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }

        return persistentEntity;
    }

    public synchronized static String buildURL(String destUrl) {
        String url = domain + destUrl;
        System.out.println("URL: " + url);
        return url;
    }

    private static <T> T xmlToObject(Class<T> type, T entity, String xml) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(type);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            entity = (T) jaxbUnmarshaller.unmarshal(new StringReader(xml));

        } catch (JAXBException ex) {
            ex.printStackTrace();
        }
        return entity;
    }

    private static <T> StringEntity objectToXML(Class<T> type, T entity) {
        StringEntity stringEntity = null;

        try {
            StringWriter writer = new StringWriter();
            JAXBContext jaxbContext = JAXBContext.newInstance(type);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.marshal(entity, writer);

            stringEntity = new StringEntity(writer.getBuffer().toString(), "UTF-8");

        } catch (JAXBException ex) {
            ex.printStackTrace();
        }

        return stringEntity;
    }
}
