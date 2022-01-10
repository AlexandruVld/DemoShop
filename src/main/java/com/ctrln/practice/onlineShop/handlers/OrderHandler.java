package com.ctrln.practice.onlineShop.handlers;

import com.ctrln.practice.onlineShop.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.ResponseEntity.status;

@ControllerAdvice
public class OrderHandler {

    @ExceptionHandler(InvalidProductsException.class)
    public ResponseEntity<String> handleInvalidProductsException(){
        return status(HttpStatus.BAD_REQUEST).body("Comanda dumneavoastra nu contine niciun produs!");
    }

    @ExceptionHandler(InvalidCustomerIdException.class)
    public ResponseEntity<String> handleInvalidCustomerIdException(){
        return status(HttpStatus.BAD_REQUEST).body("Comanda dumneavoastra nu este asignata unui user valid!");
    }

    @ExceptionHandler(InvalidProductIdException.class)
    public ResponseEntity<String> handleInvalidProductIdException(){
        return status(HttpStatus.BAD_REQUEST).body("Id-ul unui produs nu este valid in comanda curenta!");
    }

    @ExceptionHandler(NotEnoughStockException.class)
    public ResponseEntity<String> handleNotEnoughStockException(){
        return status(HttpStatus.BAD_REQUEST).body("Un produs nu a avut stocul necesar!");
    }

    @ExceptionHandler(InvalidOrderIdException.class)
    public ResponseEntity<String> handleInvalidOrderIdException(){
        return status(HttpStatus.BAD_REQUEST).body("Id-ul comenzii nu este valid!");
    }

    @ExceptionHandler(OrderAlreadyDeliveredException.class)
    public ResponseEntity<String> handleOrderAlreadyDeliveredException(){
        return status(HttpStatus.BAD_REQUEST).body("Comanda a fost deja livrata!");
    }

    @ExceptionHandler(CanceledOrderException.class)
    public ResponseEntity<String> handleCanceledOrderException(){
        return status(HttpStatus.BAD_REQUEST).body("Comanda a fost deja anulata!");
    }

    @ExceptionHandler(OrderNotDeliveredYetException.class)
    public ResponseEntity<String> handleOrderNotDeliveredYetException(){
        return status(HttpStatus.BAD_REQUEST).body("Comanda nu a fost inca livrata!");
    }
}
