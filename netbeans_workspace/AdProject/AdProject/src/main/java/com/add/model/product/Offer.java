package com.add.model.product;

import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("offer")
public class Offer {
    
    private String sellersName;
    private String price;
    private String sellersURL;
    
    public Offer(){
    
    }

    public Offer(String sellersName, String price, String sellersURL){
     this.sellersName = sellersName;
     this.price = price;
     this.sellersURL = sellersURL;
    }
    
    public String getSellersName() {
        return sellersName;
    }

    public void setSellersName(String sellersName) {
        this.sellersName = sellersName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSellersURL() {
        return sellersURL;
    }

    public void setSellersURL(String sellersURL) {
        this.sellersURL = sellersURL;
    }
    
    
}
