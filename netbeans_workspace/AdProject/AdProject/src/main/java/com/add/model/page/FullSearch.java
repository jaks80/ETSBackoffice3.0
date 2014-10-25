package com.add.model.page;

import com.add.model.product.Product;
import com.add.model.product.ProductProp;
import com.add.model.product.Products;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("fullSearch")
public class FullSearch extends KeywordSearch{

    private final int perPageItemQty = 20;
    
    private String minOriginalPrice;
    private String maxOriginalPrice;
    private String minSelectedPrice;
    private String maxSelectedPrice;
    private String selectedSortOrder;
    private Products products;
    private List<String> relatedSearch = new ArrayList<>();
    private List<String> sortOrder = new ArrayList<>();
    private List<ProductProp> refineAttributes = new ArrayList<>();
    private List<Product> itemsToShow = new ArrayList<>();
    private int currentPage = 1;
    private int numberOfPage = 1;
    
    public FullSearch(){
     super();
     this.sortOrder.add("BEST_MATCH");
     this.sortOrder.add("CURRENT_PRICE_HIGHEST");
    }

    public void paginateProduct(String pageNumber) {
        List<Product> totalItems = products.getProducts();        
        itemsToShow = new ArrayList<>();
        int itemQty = totalItems.size();

        if (pageNumber != null) {
            this.currentPage = Integer.valueOf(pageNumber);
        }
        int lastIndex = currentPage * perPageItemQty;//Last prodcut of the page
        int firstIndex = lastIndex - perPageItemQty; //First product of the page

        if (!totalItems.isEmpty()) {
            this.numberOfPage = itemQty / perPageItemQty;
            if (totalItems.size() < perPageItemQty) {
                getItemsToShow().addAll(totalItems);
            } else {
                for (int i = firstIndex; i < lastIndex; i++) {
                    getItemsToShow().add(totalItems.get(i));
                }
            }
        }
    }

    public String getMinOriginalPrice() {
        return minOriginalPrice;
    }

    public void setMinOriginalPrice(String minOriginalPrice) {
        this.minOriginalPrice = minOriginalPrice;
    }

    public String getMaxOriginalPrice() {
        return maxOriginalPrice;
    }

    public void setMaxOriginalPrice(String maxOriginalPrice) {
        this.maxOriginalPrice = maxOriginalPrice;
    }

    public String getMinSelectedPrice() {
        return minSelectedPrice;
    }

    public void setMinSelectedPrice(String minSelectedPrice) {
        this.minSelectedPrice = minSelectedPrice;
    }

    public String getMaxSelectedPrice() {
        return maxSelectedPrice;
    }

    public void setMaxSelectedPrice(String maxSelectedPrice) {
        this.maxSelectedPrice = maxSelectedPrice;
    }

    public List<String> getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(List<String> sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Products getProducts() {
        return products;
    }

    public void setProducts(Products products) {
        this.products = products;
    }

    public List<ProductProp> getRefineAttributes() {
        return refineAttributes;
    }

    public void setRefineAttributes(List<ProductProp> refineAttributes) {
        this.refineAttributes = refineAttributes;
    }

    public String getSelectedSortOrder() {
        return selectedSortOrder;
    }

    public void setSelectedSortOrder(String selectedSortOrder) {
        this.selectedSortOrder = selectedSortOrder;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getNumberOfPage() {
        return numberOfPage;
    }

    public void setNumberOfPage(int numberOfPage) {
        this.numberOfPage = numberOfPage;
    }

    public List<Product> getItemsToShow() {
        return itemsToShow;
    }

    public void setItemsToShow(List<Product> itemsToShow) {
        this.itemsToShow = itemsToShow;
    }

    public List<String> getRelatedSearch() {
        return relatedSearch;
    }

    public void setRelatedSearch(List<String> relatedSearch) {
        this.relatedSearch = relatedSearch;
    }

}
