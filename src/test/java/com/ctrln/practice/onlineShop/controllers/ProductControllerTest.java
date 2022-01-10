package com.ctrln.practice.onlineShop.controllers;

import com.ctrln.practice.onlineShop.entities.Product;
import com.ctrln.practice.onlineShop.entities.User;
import com.ctrln.practice.onlineShop.enums.Currencies;
import com.ctrln.practice.onlineShop.enums.Roles;
import com.ctrln.practice.onlineShop.repositories.ProductRepository;
import com.ctrln.practice.onlineShop.utils.UtilsComponent;
import com.ctrln.practice.onlineShop.vos.ProductVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static com.ctrln.practice.onlineShop.utils.UtilsComponent.LOCALHOST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerIntegrationTest {

    @TestConfiguration
    static class ProductControllerIntegrationTestContextConfiguration{
        @Bean
        public RestTemplate restTemplateForPatch(){
            return new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        }
    }



    @LocalServerPort
    private int port;

    @Autowired
    private ProductController productController;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RestTemplate restTemplateForPatch;

    @Autowired
    private UtilsComponent utilsComponent;

    @Test
    public void contextLoads(){
        assertThat(productController).isNotNull();
    }

    @Test
    public void addProduct_whenUserIsAdmin_shouldStoreTheProduct(){

        productRepository.deleteAll();

        User userEntity = utilsComponent.saveUserWithRole(Roles.ADMIN);

        ProductVO productVO = new ProductVO();
        productVO.setCode("aProductCode");
        productVO.setPrice(100);
        productVO.setCurrency(Currencies.RON);
        productVO.setStock(12);
        productVO.setDescription("A product description");
        productVO.setValid(true);

        testRestTemplate.postForEntity(LOCALHOST + port + "/product/" + userEntity.getId(), productVO, Void.class);

        Iterable<Product> products = productRepository.findAll();
        assertThat(products).hasSize(1);

        Product product = products.iterator().next();

        assertThat(product.getCode()).isEqualTo(productVO.getCode());

    }

    @Test
    public void addProduct_whenUserIsNotInDb_shouldThrowInvalidCustomerIdInDbException(){
        productRepository.deleteAll();
        ProductVO productVO = new ProductVO();
        productVO.setCode("aProductCode");
        productVO.setPrice(100);
        productVO.setCurrency(Currencies.RON);
        productVO.setStock(12);
        productVO.setDescription("A product description");
        productVO.setValid(true);

        ResponseEntity<String> response = testRestTemplate.postForEntity(LOCALHOST + port + "/product/123", productVO, String.class);
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Id-ul trimis este invalid!");
    }

    @Test
    public void addProduct_whenUserIsNotAdmin_shouldThrowInvalidOperationException(){

        User userEntity = utilsComponent.saveUserWithRole(Roles.CLIENT);

        ProductVO productVO = new ProductVO();
        productVO.setCode("aProductCode");
        productVO.setPrice(100);
        productVO.setCurrency(Currencies.RON);
        productVO.setStock(12);
        productVO.setDescription("A product description");
        productVO.setValid(true);

        ResponseEntity<String> response = testRestTemplate.postForEntity(LOCALHOST + port + "/product/" + userEntity.getId(), productVO, String.class);

        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Utilizatorul nu are permisiunea de a executa aceasta operatiune!");

    }


    @Test
    public void getProductByCode_whenCodeIsPresentInDb_shouldReturnTheProduct(){
        Product product = utilsComponent.storeTwoProductsInDatabase("aWonderfulCode", "aWonderfulCode2");

        ProductVO productResponse = testRestTemplate.getForObject(LOCALHOST + port + "/product/" + product.getCode(), ProductVO.class);

        assertThat(productResponse.getCode()).isEqualTo(product.getCode());
    }

    @Test
    public void getProductByCode_whenCodeIsNotPresentInDb_shouldReturnErrorMessage(){
        String productResponse = testRestTemplate.getForObject(LOCALHOST + port + "/product/123", String.class);
        assertThat(productResponse).isEqualTo("Codul produsului trimis este invalid!");
    }

    @Test
    public void getProducts(){
        productRepository.deleteAll();
        utilsComponent.storeTwoProductsInDatabase("aWonderfulCode500", "anotherWonderfulCode500");
        ProductVO[] products = testRestTemplate.getForObject(LOCALHOST + port + "/product", ProductVO[].class);
        assertThat(products).hasSize(2);
        assertThat(products[0].getCode()).contains("aWonderfulCode500");
        assertThat(products[1].getCode()).contains("anotherWonderfulCode500");
    }

    @Test
    public void updateProduct_whenUserIsEditor_shouldUpdateProduct(){
        User user = utilsComponent.saveUserWithRole(Roles.EDITOR);
        Product product = utilsComponent.generateProduct("aProduct100");

        productRepository.save(product);
        ProductVO productVO = new ProductVO();
        productVO.setCode(product.getCode());
        productVO.setPrice(200);
        productVO.setCurrency(Currencies.RON);
        productVO.setStock(200);
        productVO.setDescription("Another product description");
        productVO.setValid(false);

        testRestTemplate.put(LOCALHOST + port + "/product/" + user.getId(), productVO);
        Optional<Product> updatedProduct = productRepository.findByCode(productVO.getCode());
        assertThat(updatedProduct.get().getDescription()).isEqualTo(productVO.getDescription());
        assertThat(updatedProduct.get().getPrice()).isEqualTo(productVO.getPrice());
        assertThat(updatedProduct.get().getStock()).isEqualTo(productVO.getStock());
        assertThat(updatedProduct.get().getCurrency()).isEqualTo(productVO.getCurrency());
        assertThat(updatedProduct.get().isValid()).isEqualTo(productVO.isValid());
    }

    @Test
    public void updateProduct_whenUserIsAdmin_shouldUpdateProduct(){
        User user = utilsComponent.saveUserWithRole(Roles.ADMIN);
        Product product = utilsComponent.generateProduct("aProduct200");
        
        productRepository.save(product);
        ProductVO productVO = new ProductVO();
        productVO.setCode(product.getCode());
        productVO.setPrice(200);
        productVO.setCurrency(Currencies.RON);
        productVO.setStock(200);
        productVO.setDescription("Another product description");
        productVO.setValid(false);

        testRestTemplate.put(LOCALHOST + port + "/product/" + user.getId(), productVO);
        Optional<Product> updatedProduct = productRepository.findByCode(productVO.getCode());
        assertThat(updatedProduct.get().getDescription()).isEqualTo(productVO.getDescription());
        assertThat(updatedProduct.get().getPrice()).isEqualTo(productVO.getPrice());
        assertThat(updatedProduct.get().getStock()).isEqualTo(productVO.getStock());
        assertThat(updatedProduct.get().getCurrency()).isEqualTo(productVO.getCurrency());
        assertThat(updatedProduct.get().isValid()).isEqualTo(productVO.isValid());
    }

    @Test
    public void updateProduct_whenUserIsClient_shouldNotUpdateTheProduct(){
        User user = utilsComponent.saveUserWithRole(Roles.CLIENT);
        Product product = utilsComponent.generateProduct("aProduct300");

        productRepository.save(product);
        ProductVO productVO = new ProductVO();
        productVO.setCode(product.getCode());
        productVO.setPrice(200);
        productVO.setCurrency(Currencies.RON);
        productVO.setStock(200);
        productVO.setDescription("Another product description");
        productVO.setValid(false);

        testRestTemplate.put(LOCALHOST + port + "/product/" + user.getId(), productVO);


        Optional<Product> updatedProduct = productRepository.findByCode(productVO.getCode());
        assertThat(updatedProduct.get().getDescription()).isEqualTo(product.getDescription());
        assertThat(updatedProduct.get().getPrice()).isEqualTo(product.getPrice());
        assertThat(updatedProduct.get().getStock()).isEqualTo(product.getStock());
        assertThat(updatedProduct.get().getCurrency()).isEqualTo(product.getCurrency());
        assertThat(updatedProduct.get().isValid()).isEqualTo(product.isValid());
    }

    @Test
    public void deleteProduct_whenUserIsAdmin_shouldDeleteProduct(){
        User user = utilsComponent.saveUserWithRole(Roles.ADMIN);
        Product product = utilsComponent.generateProduct("aProduct600");
        productRepository.save(product);

        testRestTemplate.delete(LOCALHOST + port + "/product/" + product.getCode() + "/" + user.getId());

        assertThat(productRepository.findByCode(product.getCode())).isNotPresent();

    }

    @Test
    public void deleteProduct_whenUserIsClient_shouldNotDeleteProduct(){
        User user = utilsComponent.saveUserWithRole(Roles.CLIENT);
        Product product = utilsComponent.generateProduct("aProduct700");
        productRepository.save(product);

        testRestTemplate.delete(LOCALHOST + port + "/product/" + product.getCode() + "/" + user.getId());

        assertThat(productRepository.findByCode(product.getCode())).isPresent();

    }

    @Test
    public void addStock_whenUserIsAdmin_shouldIncreaseStock(){
        User user = utilsComponent.saveUserWithRole(Roles.ADMIN);
        Product product = utilsComponent.generateProduct("aProductCodeStock");
        productRepository.save(product);

        restTemplateForPatch.exchange(LOCALHOST + port + "/product/" + product.getCode() + "/3/" + user.getId(), HttpMethod.PATCH, HttpEntity.EMPTY, Void.class);
        Product productFromDb = productRepository.findByCode(product.getCode()).get();
        assertThat(productFromDb.getStock()).isEqualTo(4);
    }

    @Test
    public void addStock_whenUserIsNotAdmin_shouldThrowException(){
        User user = utilsComponent.saveUserWithRole(Roles.CLIENT);
        Product product = utilsComponent.generateProduct("aProductCodeStock");
        productRepository.save(product);

        try {
            restTemplateForPatch.exchange(LOCALHOST + port + "/product/" + product.getCode() + "/3/" + user.getId(), HttpMethod.PATCH, HttpEntity.EMPTY, Void.class);
        } catch (RestClientException exception){
            assertThat(exception.getMessage()).isEqualTo("400 : [Utilizatorul nu are permisiunea de a executa aceasta operatiune!]");
        }

    }
}