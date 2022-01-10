package com.ctrln.practice.onlineShop.entities;

import com.ctrln.practice.onlineShop.enums.Currencies;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String code;

    private String description;

    private double price;

    private int stock;

    private boolean valid;

    @Enumerated(EnumType.STRING)
    private Currencies currency;
}
