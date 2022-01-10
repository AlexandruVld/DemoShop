package com.ctrln.practice.onlineShop.handlers;

import com.ctrln.practice.onlineShop.exceptions.InvalidCustomerIdInDbException;
import com.ctrln.practice.onlineShop.exceptions.InvalidOperationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SecurityHandler {

    @ExceptionHandler(InvalidCustomerIdInDbException.class)
    public ResponseEntity<String> handleInvalidCustomerIdInDbException(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Id-ul trimis este invalid!");
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<String> handleInvalidOperationException(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Utilizatorul nu are permisiunea de a executa aceasta operatiune!");
    }
}
