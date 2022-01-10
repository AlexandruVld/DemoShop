package com.ctrln.practice.onlineShop.mappers;

import com.ctrln.practice.onlineShop.entities.OrderItem;
import com.ctrln.practice.onlineShop.entities.Orders;
import com.ctrln.practice.onlineShop.entities.Product;
import com.ctrln.practice.onlineShop.entities.User;
import com.ctrln.practice.onlineShop.exceptions.InvalidCustomerIdException;
import com.ctrln.practice.onlineShop.exceptions.InvalidProductIdException;
import com.ctrln.practice.onlineShop.exceptions.InvalidProductsException;
import com.ctrln.practice.onlineShop.repositories.ProductRepository;
import com.ctrln.practice.onlineShop.repositories.UserRepository;
import com.ctrln.practice.onlineShop.vos.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public Orders toEntity(OrderVO orderVO) throws InvalidCustomerIdException, InvalidProductsException, InvalidProductIdException {
        if(orderVO == null){
            return null;
        }
        validateOrder(orderVO);
        Orders order = new Orders();

        Optional<User> userOptional = userRepository.findById(orderVO.getCustomerId().longValue());
        if(!userOptional.isPresent()){
            throw new InvalidCustomerIdException();
        }
        order.setUser(userOptional.get());
        List<OrderItem> orderItemList = new ArrayList<>();
        Map<Integer, Integer> productsIdsToQuantityMap = orderVO.getProductsIdsToQuantity();
        Set<Integer> productsIds = productsIdsToQuantityMap.keySet();

        for (Integer productId : productsIds){
            OrderItem orderItem = new OrderItem();
            Optional<Product> productOptional = productRepository.findById(productId.longValue());
            if(!productOptional.isPresent()){
                throw new InvalidProductIdException();
            }

            orderItem.setProduct(productOptional.get());
            Integer productQuantity = productsIdsToQuantityMap.get(productId);
            orderItem.setQuantity(productQuantity);
            orderItemList.add(orderItem);
        }
        order.setOrderItems(orderItemList);
        return order;
    }

    private void validateOrder(OrderVO orderVO) throws InvalidCustomerIdException, InvalidProductsException {


        if(orderVO.getProductsIdsToQuantity().keySet().isEmpty()){
            throw new InvalidProductsException();
        }
    }
}
