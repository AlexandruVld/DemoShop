package com.ctrln.practice.onlineShop.controllers;

import com.ctrln.practice.onlineShop.entities.OrderItem;
import com.ctrln.practice.onlineShop.entities.Orders;
import com.ctrln.practice.onlineShop.entities.Product;
import com.ctrln.practice.onlineShop.entities.User;
import com.ctrln.practice.onlineShop.enums.Roles;
import com.ctrln.practice.onlineShop.repositories.OrderRepository;
import com.ctrln.practice.onlineShop.utils.UtilsComponent;
import com.ctrln.practice.onlineShop.vos.OrderVO;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static com.ctrln.practice.onlineShop.enums.Roles.*;
import static com.ctrln.practice.onlineShop.utils.UtilsComponent.LOCALHOST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerIntegrationTest {

    @TestConfiguration
    static class ProductControllerIntegrationTestContextConfiguration {
        @Bean
        public RestTemplate restTemplateForPatch() {
            return new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        }
    }

    @LocalServerPort
    private int port;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private RestTemplate restTemplateForPatch;

    @Autowired
    private UtilsComponent utilsComponent;

    @Test
    @Transactional
    public void addOrder_whenOrderIsValid_shouldAddOrderToDb() {
        User user = utilsComponent.saveUserWithRole(Roles.CLIENT);
        Product product = utilsComponent.storeTwoProductsInDatabase("code1", "code2");
        OrderVO orderVO = utilsComponent.createOrderVO(user, product);

        testRestTemplate.postForEntity(LOCALHOST + port + "/order/", orderVO, Void.class);
        List<Orders> ordersIterable = (List<Orders>) orderRepository.findAll();
        Optional<OrderItem> orderItemOptional = ordersIterable.stream()
                .map(order -> ((List<OrderItem>) order.getOrderItems()))
                .flatMap(List::stream)
                .filter(orderItem -> orderItem.getProduct().getId() == product.getId())
                .findFirst();
        assertThat(orderItemOptional).isPresent();
    }

    @Test
    public void addOrder_whenRequestIsMadeByAdmin_shouldThrowAnException() {
        User user = utilsComponent.saveUserWithRole(Roles.ADMIN);
        Product product = utilsComponent.storeTwoProductsInDatabase("code1ForAdmin", "code2ForAdmin");
        OrderVO orderVO = utilsComponent.createOrderVO(user, product);
        ResponseEntity<String> stringResponseEntity = testRestTemplate.postForEntity(LOCALHOST + port + "/order/", orderVO, String.class);
        assertThat(stringResponseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(stringResponseEntity.getBody()).isEqualTo("Utilizatorul nu are permisiunea de a executa aceasta operatiune!");
    }

    @Test
    public void addOrder_whenRequestIsMadeByExpeditor_shouldThrowAnException() {
        User user = utilsComponent.saveUserWithRole(EXPEDITOR);
        Product product = utilsComponent.storeTwoProductsInDatabase("code1ForExpeditor", "code2ForExpeditor");
        OrderVO orderVO = utilsComponent.createOrderVO(user, product);
        ResponseEntity<String> stringResponseEntity = testRestTemplate.postForEntity(LOCALHOST + port + "/order/", orderVO, String.class);
        assertThat(stringResponseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(stringResponseEntity.getBody()).isEqualTo("Utilizatorul nu are permisiunea de a executa aceasta operatiune!");
    }

    @Test
    public void deliver_whenHavingAnOrderWitchIsNotCanceled_shouldDeliverOrderByExpeditor() {
        User expeditor = utilsComponent.saveUserWithRole(EXPEDITOR);
        User client = utilsComponent.saveUserWithRole(CLIENT);
        Product product = utilsComponent.storeTwoProductsInDatabase("code1ForDelivery", "code2ForDelivery");

        Orders orderWithProducts = utilsComponent.generateOrderItems(product, client);
        orderRepository.save(orderWithProducts);


        restTemplateForPatch.exchange(LOCALHOST + port + "/order/" + orderWithProducts.getId() + "/"
                + expeditor.getId(), HttpMethod.PATCH, HttpEntity.EMPTY, Void.class);
        Orders orderFromDb = orderRepository.findById(orderWithProducts.getId()).get();
        assertThat(orderFromDb.isDelivered()).isEqualTo(true);
    }

    @Test
    public void deliver_whenHavingAnOrderWitchIsNotCanceled_shouldNotDeliverOrderByAdmin() {
        User expeditorAsAdmin = utilsComponent.saveUserWithRole(ADMIN);
        User client = utilsComponent.saveUserWithRole(CLIENT);
        Product product = utilsComponent.storeTwoProductsInDatabase("code1WhenExpeditorIsAdmin", "code2WhenExpeditorIsAdmin");

        Orders orderWithProducts = utilsComponent.generateOrderItems(product, client);
        orderRepository.save(orderWithProducts);

        try {
            restTemplateForPatch.exchange(LOCALHOST + port + "/order/" + orderWithProducts.getId() + "/"
                    + expeditorAsAdmin.getId(), HttpMethod.PATCH, HttpEntity.EMPTY, String.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400 : [Utilizatorul nu are permisiunea de a executa aceasta operatiune!]");
        }
    }

    @Test
    public void deliver_whenHavingAnOrderWitchIsNotCanceled_shouldNotDeliverOrderByClient() {
        User expeditorAsClient = utilsComponent.saveUserWithRole(CLIENT);
        User client = utilsComponent.saveUserWithRole(CLIENT);
        Product product = utilsComponent.storeTwoProductsInDatabase("code1WhenOrderIsCanceled", "code2WhenOrderIsCanceled");

        Orders orderWithProducts = utilsComponent.generateOrderItems(product, client);
        orderRepository.save(orderWithProducts);

        try {
            restTemplateForPatch.exchange(LOCALHOST + port + "/order/" + orderWithProducts.getId() + "/"
                    + expeditorAsClient.getId(), HttpMethod.PATCH, HttpEntity.EMPTY, String.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400 : [Utilizatorul nu are permisiunea de a executa aceasta operatiune!]");
        }
    }

    @Test
    public void deliver_whenHavingAnOrderWitchIsCanceled_shouldThrowException() {
        User expeditor = utilsComponent.saveUserWithRole(EXPEDITOR);
        User client = utilsComponent.saveUserWithRole(CLIENT);
        Product product = utilsComponent.storeTwoProductsInDatabase("code1WhenExpeditorIsClient", "code2WhenExpeditorIsClient");

        Orders orderWithProducts = utilsComponent.generateOrderItems(product, client);
        orderWithProducts.setCanceled(true);
        orderRepository.save(orderWithProducts);

        try {
            restTemplateForPatch.exchange(LOCALHOST + port + "/order/" + orderWithProducts.getId() + "/"
                    + expeditor.getId(), HttpMethod.PATCH, HttpEntity.EMPTY, String.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400 : [Comanda a fost deja anulata!]");
        }
    }

    @Test
    public void cancelOrder_whenValidOrder_shouldCancelOrder() {
        User client = utilsComponent.saveUserWithRole(CLIENT);
        Product product = utilsComponent.storeTwoProductsInDatabase("code1CancelWhenExpeditorIsClient", "code2CancelWhenExpeditorIsClient");

        Orders orderWithProducts = utilsComponent.generateOrderItems(product, client);
        orderWithProducts.setDelivered(false);
        orderRepository.save(orderWithProducts);

        restTemplateForPatch.exchange(LOCALHOST + port + "/order/cancel/" + orderWithProducts.getId() + "/"
                + client.getId(), HttpMethod.PATCH, HttpEntity.EMPTY, String.class);

        Orders orderFromDb = orderRepository.findById(orderWithProducts.getId()).get();
        assertThat(orderFromDb.isCanceled()).isTrue();
    }

    @Test
    public void cancelOrder_whenOrderIsDelivered_shouldThrowException() {
        User client = utilsComponent.saveUserWithRole(CLIENT);
        Product product = utilsComponent.storeTwoProductsInDatabase("code1CancelWhenOrderIsDelivered", "code2CancelWhenOrderIsDelivered");

        Orders orderWithProducts = utilsComponent.generateOrderItems(product, client);
        orderWithProducts.setDelivered(false);
        orderRepository.save(orderWithProducts);

        try {
            restTemplateForPatch.exchange(LOCALHOST + port + "/order/cancel/" + orderWithProducts.getId() + "/"
                    + client.getId(), HttpMethod.PATCH, HttpEntity.EMPTY, String.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400 : [Comanda a fost deja anulata!]");
        }
    }

    @Test
    public void cancelOrder_whenUserIsAdmin_shouldThrowException() {
        User admin = utilsComponent.saveUserWithRole(ADMIN);
        Product product = utilsComponent.storeTwoProductsInDatabase("code1CancelWhenExpeditorIsAdmin", "code2CancelWhenExpeditorIsAdmin");

        Orders orderWithProducts = utilsComponent.generateOrderItems(product, admin);
        orderWithProducts.setDelivered(false);
        orderRepository.save(orderWithProducts);

        try {
            restTemplateForPatch.exchange(LOCALHOST + port + "/order/cancel/" + orderWithProducts.getId() + "/"
                    + admin.getId(), HttpMethod.PATCH, HttpEntity.EMPTY, String.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400 : [Utilizatorul nu are permisiunea de a executa aceasta operatiune!]");
        }
    }

    @Test
    public void cancelOrder_whenUserIsExpeditor_shouldThrowException() {
        User expeditor = utilsComponent.saveUserWithRole(EXPEDITOR);
        Product product = utilsComponent.storeTwoProductsInDatabase("code1CancelWhenExpeditorIsExpeditor", "code2CancelWhenExpeditorIsExpeditor");

        Orders orderWithProducts = utilsComponent.generateOrderItems(product, expeditor);
        orderWithProducts.setDelivered(false);
        orderRepository.save(orderWithProducts);

        try {
            restTemplateForPatch.exchange(LOCALHOST + port + "/order/cancel/" + orderWithProducts.getId() + "/"
                    + expeditor.getId(), HttpMethod.PATCH, HttpEntity.EMPTY, String.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400 : [Utilizatorul nu are permisiunea de a executa aceasta operatiune!]");
        }
    }


    @Test
    @Transactional
    public void returnOrder_whenOrderIsValid_shouldReturnOrder(){
        User user = utilsComponent.saveUserWithRole(CLIENT);
        Product product = utilsComponent.storeTwoProductsInDatabase("code1ReturnOrder", "code2ReturnOrder");

        Orders orderWithProducts = utilsComponent.saveOrder(user, product, true);
        restTemplateForPatch.exchange(LOCALHOST + port + "/order/return/" + orderWithProducts.getId() + "/"
                + user.getId(), HttpMethod.PATCH, HttpEntity.EMPTY, String.class);
        Orders orderFromDb = orderRepository.findById(orderWithProducts.getId()).get();
        assertThat(orderFromDb.isReturned()).isTrue();
        assertThat(orderFromDb.getOrderItems().get(0).getProduct().getStock()).isEqualTo(product.getStock() + orderWithProducts.getOrderItems().get(0).getQuantity());
    }

    @Test
    public void returnOrder_whenOrderIsNotDelivered_shouldThrowException(){
        User user = utilsComponent.saveUserWithRole(CLIENT);
        Product product = utilsComponent.storeTwoProductsInDatabase("code1ReturnOrderNotDelivered", "code2ReturnOrderNotDelivered");

        Orders orderWithProducts = utilsComponent.saveOrder(user, product, false);
        try{
            restTemplateForPatch.exchange(LOCALHOST + port + "/order/return/" + orderWithProducts.getId() + "/"
                    + user.getId(), HttpMethod.PATCH, HttpEntity.EMPTY, String.class);
        } catch (RestClientException exception){
            assertThat(exception.getMessage()).isEqualTo("400 : [Comanda nu a fost inca livrata!]");
        }
    }

    @Test
    public void returnOrder_whenOrderIsCanceled_shouldThrowException(){
        User user = utilsComponent.saveUserWithRole(CLIENT);
        Product product = utilsComponent.storeTwoProductsInDatabase("code1ReturnOrderIsCanceled", "code2ReturnOrderIsCanceled");

        Orders orderWithProducts = utilsComponent.saveOrderWithOrderCanceled(user, product, true);
        try{
            restTemplateForPatch.exchange(LOCALHOST + port + "/order/return/" + orderWithProducts.getId() + "/"
                    + user.getId(), HttpMethod.PATCH, HttpEntity.EMPTY, String.class);
        } catch (RestClientException exception){
            assertThat(exception.getMessage()).isEqualTo("400 : [Comanda nu a fost inca livrata!]");
        }
    }

    @Test
    public void returnOrder_whenUserIsAdmin_shouldThrowException(){
        User user = utilsComponent.saveUserWithRole(ADMIN);
        Product product = utilsComponent.storeTwoProductsInDatabase("code1ReturnOrderUserAdmin", "code2ReturnOrderUserAdmin");

        Orders orderWithProducts = utilsComponent.saveOrder(user, product,true);
        try {
            restTemplateForPatch.exchange(LOCALHOST + port + "/order/return/" + orderWithProducts.getId() + "/"
                    + user.getId(), HttpMethod.PATCH, HttpEntity.EMPTY, String.class);
        } catch (RestClientException exception){
            assertThat(exception.getMessage()).isEqualTo("400 : [Utilizatorul nu are permisiunea de a executa aceasta operatiune!]");
        }
    }

    @Test
    public void returnOrder_whenUserIsExpeditor_shouldThrowException(){
        User user = utilsComponent.saveUserWithRole(EXPEDITOR);
        Product product = utilsComponent.storeTwoProductsInDatabase("code1ReturnOrderUserExpeditor", "code2ReturnOrderUserExpeditor");

        Orders orderWithProducts = utilsComponent.saveOrder(user, product, true);
        try {
            restTemplateForPatch.exchange(LOCALHOST + port + "/order/return/" + orderWithProducts.getId() + "/"
                    + user.getId(), HttpMethod.PATCH, HttpEntity.EMPTY, String.class);
        } catch (RestClientException exception){
            assertThat(exception.getMessage()).isEqualTo("400 : [Utilizatorul nu are permisiunea de a executa aceasta operatiune!]");
        }
    }


}
