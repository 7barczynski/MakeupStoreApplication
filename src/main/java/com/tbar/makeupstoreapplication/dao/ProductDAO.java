package com.tbar.makeupstoreapplication.dao;

import com.tbar.makeupstoreapplication.model.Product;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Map;

public interface ProductDAO {

    List<Product> getProducts(@NonNull Map<String, String> parameters);

    Product getProduct(@NonNull Long id);
}
