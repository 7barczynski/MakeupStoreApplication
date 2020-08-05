package com.tbar.makeupstoreapplication.service;

import com.tbar.makeupstoreapplication.model.Product;
import com.tbar.makeupstoreapplication.utility.exceptions.ProductsNotFoundException;
import com.tbar.makeupstoreapplication.utility.exceptions.SingleProductNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface MakeupService {

    Page<Product> findProducts(Specification<Product> specification, Pageable pageable) throws ProductsNotFoundException;

    Product findProduct(Long id) throws SingleProductNotFoundException;

    List<Integer> getPaginationNumbers(Page<Product> currentPage);
}
