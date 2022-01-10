package com.ctrln.practice.onlineShop.controllers;

import com.ctrln.practice.onlineShop.exceptions.*;
import com.ctrln.practice.onlineShop.services.OrderService;
import com.ctrln.practice.onlineShop.vos.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public void addOrder(@RequestBody OrderVO orderVO) throws InvalidCustomerIdException, InvalidProductsException, InvalidProductIdException, NotEnoughStockException {
        orderService.addOrder(orderVO);
    }

    @PatchMapping("/{orderId}/{customerId}")
    public void deliver(@PathVariable Integer orderId, @PathVariable Long customerId) throws InvalidOrderIdException, CanceledOrderException {
        orderService.deliver(orderId, customerId);
    }

    @PatchMapping("/cancel/{orderId}/{customerId}")
    public void cancelOrder(@PathVariable Integer orderId, @PathVariable Long customerId) throws InvalidOrderIdException, OrderAlreadyDeliveredException {
        orderService.cancelOrder(orderId, customerId);
    }

    @PatchMapping("/return/{orderId}/{customerId}")
    public void returnOrder(@PathVariable Integer orderId, @PathVariable Long customerId) throws OrderNotDeliveredYetException, InvalidOrderIdException, CanceledOrderException {
        orderService.returnOrder(orderId, customerId);
    }
}
