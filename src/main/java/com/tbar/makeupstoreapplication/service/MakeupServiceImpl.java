package com.tbar.makeupstoreapplication.service;

import com.tbar.makeupstoreapplication.dao.ProductDAO;
import com.tbar.makeupstoreapplication.model.Product;
import com.tbar.makeupstoreapplication.utility.exceptions.ProductsNotFoundException;
import com.tbar.makeupstoreapplication.utility.exceptions.SingleProductNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MakeupServiceImpl implements MakeupService {

    private final ProductDAO productDao;
    private final ProductPageCreator productPageCreator;
    private final DAOMapCreator daoMapCreator;
    private final PaginationNumbersBuilder paginationNumbersBuilder;

    @Autowired
    public MakeupServiceImpl(ProductDAO productDao, ProductPageCreator productPageCreator,
                             DAOMapCreator daoMapCreator,
                             PaginationNumbersBuilder paginationNumbersBuilder) {
        this.productDao = productDao;
        this.productPageCreator = productPageCreator;
        this.daoMapCreator = daoMapCreator;
        this.paginationNumbersBuilder = paginationNumbersBuilder;
    }

    @Override
    public Page<Product> getPaginatedProducts(@Nullable Map<String, String> requestParameters, int page)
            throws ProductsNotFoundException {
        List<Product> allRequestedProducts = getProducts(requestParameters);
        Page<Product> productPage = productPageCreator.createProductPage(allRequestedProducts, page);
        log.debug("getPaginatedProducts method. ProductPage = {}", productPage);
        return productPage;
    }

    private List<Product> getProducts(@Nullable Map<String, String> requestParameters)
            throws ProductsNotFoundException {
        Map<String, String> validDaoParameters = daoMapCreator.createMap(requestParameters);
        List<Product> products = productDao.getProducts(validDaoParameters);
        isProductsNullOrEmpty(products);
        return products;
    }

    private void isProductsNullOrEmpty(List<Product> products) throws ProductsNotFoundException {
        if (products == null || products.isEmpty()) {
            throw new ProductsNotFoundException();
        }
    }

    @Override
    public Product getProduct(@NonNull Long id) throws SingleProductNotFoundException {
        Product product = productDao.getProduct(id);
        isProductNull(product);
        log.debug("getProduct method. Product = {}", product);
        return product;
    }

    private void isProductNull(Product product) throws SingleProductNotFoundException {
        if (product == null) {
            throw new SingleProductNotFoundException();
        }
    }

    @Override
    public List<Integer> getPaginationNumbers(Page<Product> currentPage) {
        List<Integer> paginationNumbers = paginationNumbersBuilder.build(currentPage);
        log.debug("getPaginationNumbers method. paginationNumbers = {}", paginationNumbers);
        return paginationNumbers;
    }
}