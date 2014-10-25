package etsbackoffice.businesslogic;

import etsbackoffice.datalogic.CareerDao;
import etsbackoffice.domain.Career;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class CareerBo {

    private CareerDao careerDao;
    private Career career;
    private List<Career> careers;

    public CareerBo() {
    }

    public void store() {
        careerDao.store(this.career);
    }

    public void storeAll() {
        careerDao.storeAll(this.careers);
    }

    public void find(String code) {
        this.career = careerDao.findByCode(code);
    }

    public List<Career> findAll() {
        return careerDao.findAll();
    }

    public List<Object> findAllCodes() {
        List<Career> tempCareers = new ArrayList();
        tempCareers = careerDao.findAll();
        List codes = new ArrayList();

        for(Career c:tempCareers){
        codes.add(c.getCode());
        }
        Collections.sort(codes);
        return codes;
    }

    public void setCareerDao(CareerDao careerDao) {
        this.careerDao = careerDao;
    }

    public Career getCareer() {
        return career;
    }

    public void setCareer(Career career) {
        this.career = career;
    }

    public List<Career> getCareers() {
        return careers;
    }

    public void setCareers(List<Career> careers) {
        this.careers = careers;
    }
}
