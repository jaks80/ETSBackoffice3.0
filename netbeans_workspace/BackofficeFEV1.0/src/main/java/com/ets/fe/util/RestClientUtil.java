package com.ets.fe.util;

import com.amadeus.air.AIR;
import com.ets.fe.APIConfig;
import com.ets.fe.Application;
import com.ets.fe.a_main.Main;
import com.ets.fe.pnr.ws.PnrWSClient;
import com.ets.fe.security.Cryptography;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.xml.bind.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 * http://hc.apache.org/httpclient-3.x/sslguide.html
 *
 * @author Yusuf
 */
public class RestClientUtil {

    private static String domain = APIConfig.get("ws.domain")+"/"+Cryptography.decryptString(APIConfig.getConfProp("ws.id"))+"/"+APIConfig.get("ws.webservicepath");
    private static String AUTHORIZATION_PROPERTY = "Authorization";

    public static void showMessage(HttpResponse response, String title) {
        BufferedReader reader = null;
        String string = "";

        try {
            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            string = reader.readLine();

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PnrWSClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | IllegalStateException ex) {
            Logger.getLogger(PnrWSClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (response.getStatusLine().getStatusCode() == 200) {
            JOptionPane.showMessageDialog(null, string, title, JOptionPane.INFORMATION_MESSAGE);
        } else {
            RestClientUtil.displayErrorMessage(string);
        }
    }

    public static void displayErrorMessage(String message) {

        JTextArea jta = new JTextArea(message);
        JScrollPane jsp = new JScrollPane(jta) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(480, 320);
            }
        };
        JOptionPane.showMessageDialog(null, jsp, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public synchronized static <T> T getEntity(final Class<T> type, String destUrl, T entity) {

        String apiOutput = getXML(destUrl);
        if (apiOutput != null && !apiOutput.isEmpty()) {
            entity = xmlToObject(type, entity, apiOutput);
        }
        return entity;
    }

    public synchronized static String getXML(String destUrl) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        System.out.println("Making web service call1..." + destUrl);
        int status = 0;
        String apiOutput = "";
        try {
            HttpGet httpget = new HttpGet(buildURL(destUrl));
            httpget.addHeader("accept", "application/xml");
            httpget.addHeader(AUTHORIZATION_PROPERTY, Application.getUserPassowrdEncoded());
            HttpResponse response = httpClient.execute(httpget);
            status = response.getStatusLine().getStatusCode();
            System.out.println("HTTP Status:>>" + status);

            if (status == 401) {
                Main.getDlgLogin().showLoginDialog();
            }

            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                apiOutput = EntityUtils.toString(httpEntity, "UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }

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

    public synchronized static HttpResponse deleteByIdGetResponse(String destUrl) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = null;
        try {
            HttpDelete httpDelete = new HttpDelete(buildURL(destUrl));
            httpDelete.addHeader("accept", "application/xml");
            httpDelete.addHeader(AUTHORIZATION_PROPERTY, Application.getUserPassowrdEncoded());
            response = httpClient.execute(httpDelete);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return response;
    }

    public synchronized static <T> T postEntity(Class<T> type, String destUrl, T entity) {
        System.out.println("in static method");
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
        destUrl = replaceSpaceToPlus(destUrl);
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
            System.out.println("Exception: xmlToObject");
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

    public static String replaceSpaceToPlus(String url) {
        url = url.replaceAll("\\s+", " ").trim();
        return url.replaceAll(" ", "+");
    }
}
