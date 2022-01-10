package com.ctrln.practice.onlineShop.mappers;

import com.ctrln.practice.onlineShop.entities.Product;
import com.ctrln.practice.onlineShop.vos.ProductVO;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductVO productVO){

        if(productVO == null){
            return null;
        }

        Product product = new Product();
        product.setId(productVO.getId());
        product.setCode(productVO.getCode());
        product.setDescription(productVO.getDescription());
        product.setPrice(productVO.getPrice());
        product.setStock(productVO.getStock());
        product.setValid(productVO.isValid());
        product.setCurrency(productVO.getCurrency());

        return product;
    }

    public ProductVO toVO(Product product){
        if(product == null){
            return null;
        }

        ProductVO productVO = new ProductVO();
        productVO.setId(product.getId());
        productVO.setCode(product.getCode());
        productVO.setDescription(product.getDescription());
        productVO.setPrice(product.getPrice());
        productVO.setStock(product.getStock());
        productVO.setValid(product.isValid());
        productVO.setCurrency(product.getCurrency());

        return productVO;
    }
}
