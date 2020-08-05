package com.tbar.makeupstoreapplication.service;

import com.tbar.makeupstoreapplication.model.Product;
import com.tbar.makeupstoreapplication.repository.ProductRepository;
import com.tbar.makeupstoreapplication.utility.exceptions.ProductsNotFoundException;
import com.tbar.makeupstoreapplication.utility.exceptions.SingleProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MakeupServiceImpl implements MakeupService {

    private final ProductRepository productRepository;
    private final PaginationNumbersBuilder paginationNumbersBuilder;

    @Override
    public Page<Product> findProducts(Specification<Product> specification, Pageable pageable)
            throws ProductsNotFoundException {
        log.debug("findProducts method. specification = {}, pageable = {}", specification, pageable);
        Page<Product> products = productRepository.findAll(specification, pageable);
        throwExceptionIfNullOrEmpty(products);
        return products;
    }

    private void throwExceptionIfNullOrEmpty(Page<Product> products) throws ProductsNotFoundException {
        if (products == null || products.isEmpty()) {
            throw new ProductsNotFoundException();
        }
    }

    @Override
    public Product findProduct(Long id) throws SingleProductNotFoundException {
        log.debug("findProduct method. id = {}", id);
        return productRepository.findById(id).orElseThrow(SingleProductNotFoundException::new);
    }

    @Override
    public List<Integer> getPaginationNumbers(Page<Product> currentPage) {
        List<Integer> paginationNumbers = paginationNumbersBuilder.build(currentPage);
        log.debug("getPaginationNumbers method. paginationNumbers = {}", paginationNumbers);
        return paginationNumbers;
    }
}