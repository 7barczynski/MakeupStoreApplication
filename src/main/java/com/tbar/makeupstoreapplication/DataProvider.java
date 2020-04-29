package com.tbar.makeupstoreapplication;

import com.tbar.makeupstoreapplication.dao.APIConsumer;
import com.tbar.makeupstoreapplication.model.Product;
import com.tbar.makeupstoreapplication.utility.exceptions.APIConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
@Component
public class DataProvider implements ApplicationRunner {

    private APIConsumer<Product> makeupApiConsumer;
    private EntityManager entityManager;
    private List<Product> productsToSave;

    @Autowired
    public DataProvider(APIConsumer<Product> makeupApiConsumer, EntityManager entityManager) {
        this.makeupApiConsumer = makeupApiConsumer;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("Start downloading data from external MakeupAPI...");
        getData();
        log.info("Download completed.");
        saveData();
        log.info("Makeup data has been saved to database.");
    }

    private void getData() throws APIConnectionException {
        productsToSave = makeupApiConsumer.requestCollection();
    }

    private void saveData() {
        for (Product product : productsToSave) {
            entityManager.persist(product);
        }
    }
}
