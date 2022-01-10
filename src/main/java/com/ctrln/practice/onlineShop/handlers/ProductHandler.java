package com.ctrln.practice.onlineShop.handlers;

import com.ctrln.practice.onlineShop.exceptions.InvalidProductCodeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ProductHandler {

    @ExceptionHandler(InvalidProductCodeException.class)
    public ResponseEntity<String> handleInvalidProductCodeException(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Codul produsului trimis este invalid!");
    }

}
