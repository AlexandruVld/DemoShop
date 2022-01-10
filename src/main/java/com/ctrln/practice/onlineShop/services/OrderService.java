package com.ctrln.practice.onlineShop.services;

import com.ctrln.practice.onlineShop.entities.Orders;
import com.ctrln.practice.onlineShop.entities.Product;
import com.ctrln.practice.onlineShop.exceptions.*;
import com.ctrln.practice.onlineShop.mappers.OrderMapper;
import com.ctrln.practice.onlineShop.repositories.OrderRepository;
import com.ctrln.practice.onlineShop.vos.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final StockService stockService;

    public void addOrder(OrderVO orderVO) throws InvalidCustomerIdException, InvalidProductsException, InvalidProductIdException, NotEnoughStockException {
        validateStock(orderVO);

        Orders order = orderMapper.toEntity(orderVO);
        order.getOrderItems().forEach(orderItem -> {
            int oldProductStock = orderItem.getProduct().getStock();
            int productId = (int) orderItem.getProduct().getId();
            int sellStock = orderVO.getProductsIdsToQuantity().get(productId);
            orderItem.getProduct().setStock(oldProductStock - sellStock);
        });
        orderRepository.save(order);
    }

    @Transactional
    public void deliver(Integer orderId, Long customerId) throws InvalidOrderIdException, CanceledOrderException {
        System.out.println("Customer-ul cu id " + customerId + " este in service!");
        throwExceptionIfOrderIdIsAbsent(orderId);
        Orders order = getOrderOrThrowException(orderId);
        if(order.isCanceled()){
            throw new CanceledOrderException();
        }
        order.setDelivered(true);
//        orderRepository.save(order);
    }

    @Transactional
    public void cancelOrder(Integer orderId, Long customerId) throws InvalidOrderIdException, OrderAlreadyDeliveredException {
        System.out.println("Customer-ul cu id " + customerId + " este in service pentru a anula comanda " + orderId);
        throwExceptionIfOrderIdIsAbsent(orderId);
        Orders order = getOrderOrThrowException(orderId);
        if(order.isDelivered()){
            throw new OrderAlreadyDeliveredException();
        }
        order.setCanceled(true);
//        orderRepository.save(order);
    }

    @Transactional
    public void returnOrder(Integer orderId, Long customerId) throws InvalidOrderIdException, OrderNotDeliveredYetException, CanceledOrderException {
        System.out.println("Customer-ul cu id " + customerId + " este in service pentru a returna comanda " + orderId);
        throwExceptionIfOrderIdIsAbsent(orderId);
        Orders order = getOrderOrThrowException(orderId);
        if(!order.isDelivered()){
            throw new OrderNotDeliveredYetException();
        }
        if(order.isCanceled()){
            throw new CanceledOrderException();
        }
        order.setReturned(true);
        order.getOrderItems().forEach(orderItem -> {
            Product product = orderItem.getProduct();
            int oldStock = product.getStock();
            product.setStock(oldStock + orderItem.getQuantity());
        });
    }

    private void validateStock(OrderVO orderVO) throws NotEnoughStockException {
        Map<Integer, Integer> productsIdsToQuantityMap = orderVO.getProductsIdsToQuantity();
        Set<Integer> productsIds = productsIdsToQuantityMap.keySet();
        for(Integer productId : productsIds){
            Integer quantity = productsIdsToQuantityMap.get(productId);
            boolean havingEnoughStock = stockService.isHavingEnoughStock(productId, quantity);
            if(!havingEnoughStock){
                throw new NotEnoughStockException();
            }
        }
    }

    private void throwExceptionIfOrderIdIsAbsent(Integer orderId) throws InvalidOrderIdException {
        if(orderId == null){
            throw new InvalidOrderIdException();
        }
    }

    private Orders getOrderOrThrowException(Integer orderId) throws InvalidOrderIdException {
        Optional<Orders> orderOptional = orderRepository.findById(orderId.longValue());
        if (!orderOptional.isPresent()) {
            throw new InvalidOrderIdException();
        }
        return orderOptional.get();
    }


}
