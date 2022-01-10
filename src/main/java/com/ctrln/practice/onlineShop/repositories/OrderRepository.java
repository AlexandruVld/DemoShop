package com.ctrln.practice.onlineShop.repositories;

import com.ctrln.practice.onlineShop.entities.Orders;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Orders, Long> {
}
