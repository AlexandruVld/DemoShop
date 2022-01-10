package com.ctrln.practice.onlineShop.services;

import com.ctrln.practice.onlineShop.entities.Product;
import com.ctrln.practice.onlineShop.exceptions.InvalidProductCodeException;
import com.ctrln.practice.onlineShop.mappers.ProductMapper;
import com.ctrln.practice.onlineShop.repositories.ProductRepository;
import com.ctrln.practice.onlineShop.vos.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    public void addProduct(ProductVO productVO, Long customerId){
        System.out.println("Customer with id " + customerId + " is in service");
        Product product = productMapper.toEntity(productVO);
        productRepository.save(product);
    }

    public ProductVO getProduct(String productCode) throws InvalidProductCodeException {
        Product product = getProductEntity(productCode);

        return productMapper.toVO(product);

    }

    public List<ProductVO> getProducts(){
        List<ProductVO> products = new ArrayList<>();
        Iterable<Product> productsFromDb = productRepository.findAll();
        Iterator<Product> iterator = productsFromDb.iterator();

        while (iterator.hasNext()){
            Product product = iterator.next();
            ProductVO productVO = productMapper.toVO(product);
            products.add(productVO);
        }

        return products;
    }

    public void updateProduct(ProductVO productVO, Long customerId) throws InvalidProductCodeException {
        System.out.println("Customer with id " + customerId + " is in service for update");

        verifyProductCode(productVO.getCode());

        Product product = getProductEntity(productVO.getCode());
        product.setDescription(productVO.getDescription());
        product.setPrice(productVO.getPrice());
        product.setStock(productVO.getStock());
        product.setValid(productVO.isValid());
        product.setCurrency(productVO.getCurrency());

        productRepository.save(product);
    }

    public void deleteProduct(String productCode, Long customerId) throws InvalidProductCodeException {
        System.out.println("Customer with id " + customerId + " is in service for deleting " + productCode);

        verifyProductCode(productCode);

        Product product = getProductEntity(productCode);
        productRepository.delete(product);
    }

    @Transactional
    public void addStock(String productCode, Integer quantity, Long customerId) throws InvalidProductCodeException {
        System.out.println("Customer with id " + customerId + " is in service for adding stock to " + productCode + ", number of items " + quantity);
        verifyProductCode(productCode);
        Product product = getProductEntity(productCode);
        int oldStock = product.getStock();
        product.setStock(oldStock + quantity);
    }

    private Product getProductEntity(String productCode) throws InvalidProductCodeException {
        Optional<Product> productOptional = productRepository.findByCode(productCode);

        if(!productOptional.isPresent()){
            throw new InvalidProductCodeException();
        }

        return productOptional.get();
    }

    private void verifyProductCode(String productCode) throws InvalidProductCodeException {
        if(productCode == null){
            throw new InvalidProductCodeException();
        }
    }


}
