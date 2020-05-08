package com.tbar.makeupstoreapplication.dao;

import com.tbar.makeupstoreapplication.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

@Repository
public class ProductDAOImpl implements ProductDAO {

    private EntityManager entityManager;

    @Autowired
    public ProductDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Product> getProducts(@NonNull Map<String, String> parameters) {
        return null;
    }

    @Override
    public Product getProduct(@NonNull Long id) {
        return null;
    }
}
