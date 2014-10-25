package com.add.service;

import com.add.apidao.ebay.SearchEbayItems;
import com.add.model.page.FullSearch;
import com.add.model.product.Product;
import com.add.model.product.Products;
import com.ebay.services.finding.SearchItem;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("ebayService")
public class EbayService {

    @Resource(name = "searchEbayItems")
    private SearchEbayItems searchEbayItems;

    public EbayService() {

    }

    public Products searchEbayProduct(String keyword, String sortOrderType, String maxPrice, String minPrice) {

        if("best_match".equals(sortOrderType)){
         sortOrderType = "BEST_MATCH";
        }else if("price_desc".equals(sortOrderType)){
         sortOrderType = "PRICE_PLUS_SHIPPING_HIGHEST";
        }else if("price_asc".equals(sortOrderType)){
         sortOrderType = "PRICE_PLUS_SHIPPING_LOWEST";
        }
        
        List<SearchItem> ebayProducts = searchEbayItems.search(keyword, sortOrderType, maxPrice, minPrice);

        Products products = convertEbayProduct(ebayProducts);

        return products;
    }
    
    public Products searchEbayProductByCat(String category, String sortOrderType, String maxPrice, String minPrice){
    
        if("best_match".equals(sortOrderType)){
         sortOrderType = "BEST_MATCH";
        }else if("price_desc".equals(sortOrderType)){
         sortOrderType = "PRICE_PLUS_SHIPPING_HIGHEST";
        }else if("price_asc".equals(sortOrderType)){
         sortOrderType = "PRICE_PLUS_SHIPPING_LOWEST";
        }
        
        List<SearchItem> ebayProducts = searchEbayItems.searchItemsByCategory(category, sortOrderType, maxPrice, minPrice,null);
        
        Products products = convertEbayProduct(ebayProducts);
        
        return products;
    }
    
    private Products convertEbayProduct(List<SearchItem> items) {

        Products products = new Products();

        for (SearchItem i : items) {
            Product p = new Product();
            p.setTitle(i.getTitle());
            p.setCurrentPrice(String.valueOf(i.getSellingStatus().getCurrentPrice().getValue()));
            p.setSellersURL(i.getViewItemURL());
            p.setImageURL(i.getGalleryURL());

            products.addProduct(p);
        }

        return products;
    }
    
    public FullSearch getFullSearchPage(FullSearch fullSearch){
    
        Products products = searchEbayProduct(fullSearch.getKeyword(), 
                                           fullSearch.getSelectedSortOrder(), 
                                           fullSearch.getMaxSelectedPrice(), 
                                           fullSearch.getMinSelectedPrice());
        
        
        fullSearch.setProducts(products);
        
        //Set first page and pagination properties
        fullSearch.paginateProduct("1");
        
        //get product props
        return fullSearch;
    }
}
