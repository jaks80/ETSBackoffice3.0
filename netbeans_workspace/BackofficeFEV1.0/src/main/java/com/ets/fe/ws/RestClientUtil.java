package com.ets.fe.ws;

import com.ets.util.APIConfig;
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
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(domain + destUrl);

        return target;
    }

    public synchronized static <T> T getEntity(final Class<T> type, String destUrl, T entity) {

        Response response = getWebTarget(destUrl).request().get();
        try{
        entity = (T) response.readEntity(type);
        }catch(Exception e){
            System.out.println("Exception: "+e);
        }
        
        response.close();
        return entity;
    }

    public synchronized static <T> T postEntity(final Class<T> type, String destUrl, T entity) {

        Response response = getWebTarget(destUrl).request().post(Entity.entity(entity, "application/xml"));
        T persistentEntity = response.readEntity(type);
        response.close();
        return persistentEntity;
    }

    public synchronized static <T> T putEntity(final Class<T> type, String path, T entity) {

        Response response = getWebTarget(path).request().put(Entity.entity(entity, "application/xml"));
        T persistentEntity = response.readEntity(type);
        response.close();
        return persistentEntity;
    }

//    public synchronized static <T> List<T> getList(final Class<T> type, String path, String... params) {
//    //GenericType<List<T>> type = 
//    Response response = getWebTarget(path).request().get();
//    return entity.readEntity(type);
//}
}
