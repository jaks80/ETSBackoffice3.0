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

    public synchronized static <T> T getEntity(T entity, String destUrl) {

        Response response = getWebTarget(destUrl).request().get();
        entity = (T) response.getEntity();
        response.close();
        return entity;
    }
    
    public synchronized static <T> T postEntity(final Class<T> type, String path, T entity) {

        Response response = getWebTarget(path).request().post(Entity.entity(entity, "application/xml"));                        
        T persistentEntity =  response.readEntity(type);
        return persistentEntity;
    }
    
    public synchronized static <T> T putEntity(final Class<T> type, String path, T entity) {

        Response response = getWebTarget(path).request().put(Entity.entity(entity, "application/xml"));                        
        T persistentEntity =  response.readEntity(type);
        return persistentEntity;
    }
    
//    <T> List<T> getList(String path, List<String[]> params, final Class<T> clazz) {
//    //GenericType<List<T>> type = 
//    Response response = getWebTarget(path).request().get();
//    return entity.readEntity(type);
//}


}

