package com.ctrln.practice.onlineShop.services;

import com.ctrln.practice.onlineShop.entities.Product;
import com.ctrln.practice.onlineShop.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockService {

    private final ProductRepository productRepository;

    public boolean isHavingEnoughStock(Integer productId, Integer quantity){
        Product product = productRepository.findById(productId.longValue()).get();
        return product.getStock() >= quantity;
    }
}
