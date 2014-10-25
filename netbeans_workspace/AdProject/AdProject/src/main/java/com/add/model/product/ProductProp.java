package com.add.model.product;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("productProp")
public class ProductProp {

    private String name;
    private List<String> values = new ArrayList<>();

    public ProductProp() {

    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    private void addValue(String value) {
        this.values.add(value);
    }
}
