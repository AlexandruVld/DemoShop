package com.ctrln.practice.onlineShop.services;

import com.ctrln.practice.onlineShop.entities.Product;
import com.ctrln.practice.onlineShop.enums.Currencies;
import com.ctrln.practice.onlineShop.exceptions.InvalidProductCodeException;
import com.ctrln.practice.onlineShop.mappers.ProductMapper;
import com.ctrln.practice.onlineShop.repositories.ProductRepository;
import com.ctrln.practice.onlineShop.vos.ProductVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
public class ProductServiceTest {

    @TestConfiguration
    static class ProductServiceTestContextConfiguration{
        @MockBean
        private ProductMapper productMapper;

        @MockBean
        private ProductRepository productRepository;

        @Bean
        public ProductService productService(){
            return new ProductService(productMapper, productRepository);
        }
    }

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void addProduct(){

        Product product = generateProduct("someProduct");

        when(productMapper.toEntity(any())).thenReturn(product);

        ProductVO productVO = generateProductVO("someCode");

        Long customerId = 99L;
        productService.addProduct(productVO, customerId);

        verify(productMapper).toEntity(productVO);
        verify(productRepository).save(product);
    }

    @Test
    public void getProduct_whenProductIsNotInDb_shouldThrowInvalidProductCodeException(){
        try {
            productService.getProduct("asd");
        } catch (InvalidProductCodeException e) {
            assert true;
            return;
        }

        assert false;
    }

    @Test
    public void getProduct_whenProductIsInDb_shouldReturnIt() throws InvalidProductCodeException {
        Product product = generateProduct("aCode");
        when(productRepository.findByCode(any())).thenReturn(Optional.of(product));
        ProductVO productVO = new ProductVO();
        productVO.setCode("aCode");
        when(productMapper.toVO(any())).thenReturn(productVO);

        ProductVO returnedProduct = productService.getProduct("aCode");

        assertThat(returnedProduct.getCode()).isEqualTo("aCode");

        verify(productRepository).findByCode("aCode");
        verify(productMapper).toVO(product);
    }

    @Test
    public void getProducts(){
        Product product1 = generateProduct("aCode1");
        Product product2 = generateProduct("aCode2");
        ArrayList<Product> products = new ArrayList<>(Arrays.asList(product1, product2));

        ProductVO productVO1 = generateProductVO("aCode1");
        ProductVO productVO2 = generateProductVO("aCode2");

        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.toVO(product1)).thenReturn(productVO1);
        when(productMapper.toVO(product2)).thenReturn(productVO2);

        List<ProductVO> productsList = productService.getProducts();
        assertThat(productsList).hasSize(2).containsOnly(productVO1, productVO2);

        verify(productRepository).findAll();
        verify(productMapper).toVO(product1);
        verify(productMapper).toVO(product2);
    }

    @Test
    public void updateProduct_whenProductCodeIsNull_shoudThrowAnException(){
        ProductVO productVO = new ProductVO();
        try {
            productService.updateProduct(productVO, 1L);
        } catch (InvalidProductCodeException e) {
            assert true;
            return;
        }
        assert false;
    }

    @Test
    public void updateProduct_whenProductCodeIsValid_shouldUpdateProduct() throws InvalidProductCodeException {
        Product product = generateProduct("testProductCode");
        product.setDescription("an old description");
        ProductVO productVO = generateProductVO("otherTestProductCode");
        productVO.setDescription("a new description");
        when(productRepository.findByCode(any())).thenReturn(Optional.of(product));
        productService.updateProduct(productVO, 1L);

        verify(productRepository).findByCode(productVO.getCode());
        ArgumentCaptor<Product> argumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(argumentCaptor.capture());

        Product productSentAsCapture = argumentCaptor.getValue();

        assertThat(productSentAsCapture.getDescription()).isEqualTo(productVO.getDescription());
    }

    @Test
    public void deleteProduct_whenProductCodeIsNull_shouldThrowInvalidProductCodeException(){
        ProductVO productVO = new ProductVO();
        try {
            productService.deleteProduct(productVO.getCode(), 1L);
        } catch (InvalidProductCodeException e) {
            assert true;
            return;
        }
        assert false;
    }

    @Test
    public void deleteProduct_whenProductCodeIsValid_shouldDeleteProduct() throws InvalidProductCodeException {
        Product product = generateProduct("deletionProductCode");

        when(productRepository.findByCode(any())).thenReturn(Optional.of(product));
        productService.deleteProduct(product.getCode(), 1L);

        verify(productRepository).findByCode(product.getCode());
        verify(productRepository).delete(product);
    }

    private ProductVO generateProductVO(String productVOCode) {
        ProductVO productVO = new ProductVO();
        productVO.setValid(true);
        productVO.setDescription("A description");
        productVO.setStock(1);
        productVO.setPrice(11);
        productVO.setCurrency(Currencies.RON);
        productVO.setId(1);
        productVO.setCode(productVOCode);
        return productVO;
    }

    private Product generateProduct(String productCode) {
        Product product = new Product();
        product.setValid(true);
        product.setDescription("A description");
        product.setStock(1);
        product.setPrice(11);
        product.setCurrency(Currencies.RON);
        product.setId(1);
        product.setCode(productCode);
        return product;
    }

}