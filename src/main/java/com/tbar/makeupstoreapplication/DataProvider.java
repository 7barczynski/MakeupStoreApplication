package com.tbar.makeupstoreapplication;

import com.tbar.makeupstoreapplication.service.MakeupService;
import com.tbar.makeupstoreapplication.service.consumer.model.Product;
import com.tbar.makeupstoreapplication.utility.exceptions.ProductsNotFoundException;
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

    private MakeupService makeupService;
    private EntityManager entityManager;
    private List<Product> productsToSave;

    @Autowired
    public DataProvider(MakeupService makeupService, EntityManager entityManager) {
        this.makeupService = makeupService;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        log.info("Start downloading data from external MakeupAPI...");
        getData();
        log.info("Download completed.");
        saveData();
        log.info("Makeup data has been saved to database.");
    }

    private void getData() throws ProductsNotFoundException {
        productsToSave = makeupService.getProductCollection(null);
    }

    private void saveData() {
        for (Product product : productsToSave) {
            entityManager.persist(product);
        }
    }
}
