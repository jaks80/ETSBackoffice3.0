package com.add.model.product;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("products")
public class Products {

    private List<Product> products = new ArrayList<>();
    private List<ProductProp> productProps = new ArrayList<>(); 
    
    public Products() {

    }

    public List<ProductProp> getProductProps() {
        return productProps;
    }

    public void setProductProps(List<ProductProp> productProps) {
        this.productProps = productProps;
    }

    public void addProductProperty(ProductProp prop) {
        this.productProps.add(prop);
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void addProduct(Product p) {
        this.products.add(p);
    }
}
