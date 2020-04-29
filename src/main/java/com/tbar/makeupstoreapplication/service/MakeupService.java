package com.tbar.makeupstoreapplication.service;

import com.tbar.makeupstoreapplication.model.Product;
import com.tbar.makeupstoreapplication.utility.exceptions.ProductsNotFoundException;
import com.tbar.makeupstoreapplication.utility.exceptions.SingleProductNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

public interface MakeupService {

    Page<Product> getPaginatedProducts(@Nullable Map<String, String> requestParameters, int page)
            throws ProductsNotFoundException;

    List<Product> getProductCollection(@Nullable Map<String, String> requestParameters)
            throws ProductsNotFoundException;

    Product getProduct(@NonNull Long id) throws SingleProductNotFoundException;

    List<Integer> getPaginationNumbers(Page<Product> currentPage);
}
