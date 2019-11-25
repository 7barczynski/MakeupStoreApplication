package com.tbar.MakeupStoreApplication.service;

import com.tbar.MakeupStoreApplication.service.consumer.MakeupAPIConsumer;
import com.tbar.MakeupStoreApplication.service.consumer.model.Item;
import com.tbar.MakeupStoreApplication.utility.exceptions.ExternalAPIException;
import com.tbar.MakeupStoreApplication.utility.exceptions.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class MakeupServiceImpl implements MakeupService {

    // === fields ===
    private MakeupAPIConsumer makeupAPIConsumer;

    // === constructors ===
    @Autowired
    public MakeupServiceImpl(MakeupAPIConsumer makeupAPIConsumer) {
        this.makeupAPIConsumer = makeupAPIConsumer;
    }

    @Override
    public List<Item> getProducts(Map<String, String> parameters) throws ProductNotFoundException, ExternalAPIException {
        return null;
    }
}
