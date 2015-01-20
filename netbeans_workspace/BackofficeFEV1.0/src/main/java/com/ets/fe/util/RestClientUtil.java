package com.ets.fe.util;

import com.amadeus.air.AIR;
import com.ets.fe.APIConfig;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

/**
 *
 * @author Yusuf
 */
public class RestClientUtil {

    private static final String domain = APIConfig.get("ws.domain");

    public synchronized static ResteasyWebTarget getWebTarget(String destUrl) {
        String url = domain + destUrl;
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(url);
        System.out.println("URL: " + url);
        return target;
    }

    public synchronized static <T> T getEntity(final Class<T> type, String destUrl, T entity) {

        Response response = getWebTarget(destUrl).request().get();
        try {
            entity = (T) response.readEntity(type);
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.close();
        return entity;
    }

    public synchronized static Integer postAIR(String destUrl, AIR air) {

        Response response = getWebTarget(destUrl).request().post(Entity.entity(air, "application/xml"));
        response.close();
        return response.getStatus();
    }

    public synchronized static Integer deleteById(String destUrl) {

        Response response = getWebTarget(destUrl).request().delete();
        response.close();
        return response.getStatus();
    }

    public synchronized static <T> T postEntity(final Class<T> type, String destUrl, T entity) {
        T persistentEntity = null;
        try {
            Response response = getWebTarget(destUrl).request().post(Entity.entity(entity, "application/xml"));
            persistentEntity = response.readEntity(type);            
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return persistentEntity;
    }

    public synchronized static <T> T putEntity(final Class<T> type, String path, T entity) {

        Response response = getWebTarget(path).request().put(Entity.entity(entity, "application/xml"));
        T persistentEntity = response.readEntity(type);
        response.close();
        return persistentEntity;
    }
}
