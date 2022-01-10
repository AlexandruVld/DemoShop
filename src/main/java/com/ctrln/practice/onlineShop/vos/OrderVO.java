package com.ctrln.practice.onlineShop.vos;

import lombok.Data;

import java.util.Map;

@Data
public class OrderVO {
    private Integer customerId;
    private Map<Integer, Integer> productsIdsToQuantity;
}
