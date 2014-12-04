package com.ets.otherservice.service;

import com.ets.otherservice.dao.OtherServiceDAO;
import com.ets.otherservice.domain.OtherService;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("otherServiceService")
public class OtherServiceService {

    @Resource(name = "otherServiceDAO")
    private OtherServiceDAO dao;

    public List<OtherService> findAll() {
        
        String hql="from OtherService as o left join fetch o.category";
        
        return dao.findMany(hql);
    } 

    public OtherService saveorUpdate(OtherService otherService) {
        dao.save(otherService);
        return otherService;
    }

    public void delete(OtherService otherService) {
        dao.delete(otherService);
    }
}
