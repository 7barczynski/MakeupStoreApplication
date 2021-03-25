package com.tbar.makeupstoreapplication;

import com.tbar.makeupstoreapplication.dao.APIConsumer;
import com.tbar.makeupstoreapplication.model.Product;
import com.tbar.makeupstoreapplication.repository.ProductRepository;
import com.tbar.makeupstoreapplication.utility.exceptions.APIConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class DataProvider implements ApplicationRunner {

    private final APIConsumer<Product> makeupApiConsumer;
    private final ProductRepository productRepository;
    private List<Product> productsToSave;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        try {
            log.info("Start downloading data from external MakeupAPI...");
            getData();
            log.info("Download completed.");
            saveData();
            log.info("Makeup data has been saved to database.");
        } catch (APIConnectionException e) {
            log.info(e.getLocalizedMessage());
        }
    }

    private void getData() throws APIConnectionException {
        productsToSave = makeupApiConsumer.requestCollection();
    }

    private void saveData() {
        productRepository.saveAll(productsToSave);
    }
}
