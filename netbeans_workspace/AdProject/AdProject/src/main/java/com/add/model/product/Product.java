package com.add.model.product;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("product")
public class Product {
    
    private String title;
    private String originalPrice;
    private String currentPrice;
    private String sellersURL;
    private String imageURL;
    private String sellersLogoURL;
    private List<Offer> offers = new ArrayList<>();
    
    
    public Product(){
    
    }


    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getSellersURL() {
        return sellersURL;
    }

    public void setSellersURL(String sellersURL) {
        this.sellersURL = sellersURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSellersLogoURL() {
        return sellersLogoURL;
    }

    public void setSellersLogoURL(String sellersLogoURL) {
        this.sellersLogoURL = sellersLogoURL;
    }        

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }
    
    public void addOffer(Offer offer){
     this.offers.add(offer);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
