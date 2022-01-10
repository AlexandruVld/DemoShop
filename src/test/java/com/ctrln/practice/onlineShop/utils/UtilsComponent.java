package com.ctrln.practice.onlineShop.utils;

import com.ctrln.practice.onlineShop.entities.*;
import com.ctrln.practice.onlineShop.enums.Currencies;
import com.ctrln.practice.onlineShop.enums.Roles;
import com.ctrln.practice.onlineShop.repositories.OrderRepository;
import com.ctrln.practice.onlineShop.repositories.ProductRepository;
import com.ctrln.practice.onlineShop.repositories.UserRepository;
import com.ctrln.practice.onlineShop.vos.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@RequiredArgsConstructor
public class UtilsComponent {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public static final String LOCALHOST = "http://localhost:";

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public User saveUserWithRole(Roles role) {
        User userEntity = new User();
        userEntity.setFirstname("adminFirstName");
        Collection<Roles> roles = new ArrayList<>();
        roles.add(role);
        userEntity.setRoles(roles);
        Address address = new Address();
        address.setCity("Bucuresti");
        address.setStreet("A wonderful street");
        address.setNumber(2);
        address.setZipcode("123");
        userEntity.setAddress(address);
        userRepository.save(userEntity);
        return userEntity;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Product generateProduct(String productCode) {
        Product product = new Product();
        product.setCode(productCode);
        product.setPrice(100);
        product.setCurrency(Currencies.RON);
        product.setStock(1);
        product.setDescription("A product description");
        product.setValid(true);
        return product;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public  Product storeTwoProductsInDatabase(String code1, String code2) {
        Product product = generateProduct(code1);
        Product product2 = generateProduct(code2);

        ArrayList<Product> products = new ArrayList<>();
        products.add(product);
        products.add(product2);

        productRepository.saveAll(products);
        return product;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public OrderVO createOrderVO(User user, Product product) {
        OrderVO orderVO = new OrderVO();
        orderVO.setCustomerId((int) user.getId());
        Map<Integer, Integer> orderMap = new HashMap<>();
        orderMap.put((int) product.getId(), 1);
        orderVO.setProductsIdsToQuantity(orderMap);
        return orderVO;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public OrderItem generateOrderItem(Product product) {
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(1);
        orderItem.setProduct(product);
        return orderItem;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Orders generateOrderItems(Product product, User user) {
        Orders order = new Orders();
        order.setUser(user);
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem = generateOrderItem(product);
        orderItems.add(orderItem);
        order.setOrderItems(orderItems);
        return order;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Orders saveOrder(User user, Product product, boolean isDelivered) {
        Orders orderWithProducts = generateOrderItems(product, user);
        orderWithProducts.setDelivered(isDelivered);
        orderRepository.save(orderWithProducts);
        return orderWithProducts;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Orders saveOrderWithOrderCanceled(User user, Product product, boolean isCanceled) {
        Orders orderWithProducts = generateOrderItems(product, user);
        orderWithProducts.setCanceled(isCanceled);
        orderRepository.save(orderWithProducts);
        return orderWithProducts;
    }
}
