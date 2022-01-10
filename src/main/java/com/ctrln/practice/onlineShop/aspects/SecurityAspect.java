package com.ctrln.practice.onlineShop.aspects;

import com.ctrln.practice.onlineShop.entities.User;
import com.ctrln.practice.onlineShop.enums.Roles;
import com.ctrln.practice.onlineShop.exceptions.InvalidCustomerIdException;
import com.ctrln.practice.onlineShop.exceptions.InvalidCustomerIdInDbException;
import com.ctrln.practice.onlineShop.exceptions.InvalidOperationException;
import com.ctrln.practice.onlineShop.repositories.UserRepository;
import com.ctrln.practice.onlineShop.vos.OrderVO;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
public class SecurityAspect {

    private final UserRepository userRepository;

    @Pointcut("execution(* com.ctrln.practice.onlineShop.services.ProductService.addProduct(..))")
    public void addProduct(){}

    @Pointcut("execution(* com.ctrln.practice.onlineShop.services.ProductService.updateProduct(..))")
    public void updateProduct(){}

    @Pointcut("execution(* com.ctrln.practice.onlineShop.services.ProductService.deleteProduct(..))")
    public void deleteProduct(){}

    @Pointcut("execution(* com.ctrln.practice.onlineShop.services.ProductService.addStock(..))")
    public void addingStock(){}

    @Pointcut("execution(* com.ctrln.practice.onlineShop.services.OrderService.addOrder(..))")
    public void addOrder(){}

    @Pointcut("execution(* com.ctrln.practice.onlineShop.services.OrderService.deliver(..))")
    public void deliver(){}

    @Pointcut("execution(* com.ctrln.practice.onlineShop.services.OrderService.cancelOrder(..))")
    public void cancelOrder(){}

    @Pointcut("execution(* com.ctrln.practice.onlineShop.services.OrderService.returnOrder(..))")
    public void returnOrder(){}



    @Before("com.ctrln.practice.onlineShop.aspects.SecurityAspect.addProduct()")
    public void checkSecurityBeforeAddingProduct(JoinPoint joinPoint) throws InvalidCustomerIdInDbException, InvalidOperationException {
        Long customerId = (Long) joinPoint.getArgs()[1];
        Optional<User> userOptional = userRepository.findById(customerId);
        if(!userOptional.isPresent()){
            throw new InvalidCustomerIdInDbException();
        }
        User user = userOptional.get();
        if(userIsNotAllowedToAddProduct(user.getRoles())){
            throw new InvalidOperationException();
        }
        System.out.println(customerId);
    }

    @Before("com.ctrln.practice.onlineShop.aspects.SecurityAspect.updateProduct()")
    public void checkSecurityBeforeUpdatingProduct(JoinPoint joinPoint) throws InvalidCustomerIdException, InvalidOperationException {
        Long customerId = (Long) joinPoint.getArgs()[1];
        Optional<User> userOptional = userRepository.findById(customerId);
        if(!userOptional.isPresent()){
            throw new InvalidCustomerIdException();
        }
        User user = userOptional.get();
        if(userIsNotAllowedToUpdateProduct(user.getRoles())){
            throw new InvalidOperationException();
        }
        System.out.println(customerId);
    }

    @Before("com.ctrln.practice.onlineShop.aspects.SecurityAspect.deleteProduct()")
    public void checkSecurityBeforeDeletingProduct(JoinPoint joinPoint) throws InvalidCustomerIdException, InvalidOperationException {
        Long customerId = (Long) joinPoint.getArgs()[1];
        Optional<User> userOptional = userRepository.findById(customerId);
        if(!userOptional.isPresent()){
            throw new InvalidCustomerIdException();
        }
        User user = userOptional.get();
        if(userIsNotAllowedToDeleteProduct(user.getRoles())){
            throw new InvalidOperationException();
        }
        System.out.println(customerId);
    }

    @Before("com.ctrln.practice.onlineShop.aspects.SecurityAspect.addOrder()")
    public void checkSecurityBeforeAddingOrder(JoinPoint joinPoint) throws InvalidCustomerIdException, InvalidOperationException {
        OrderVO orderVO = (OrderVO) joinPoint.getArgs()[0];
        if(orderVO.getCustomerId() == null){
            throw new InvalidCustomerIdException();
        }
        Optional<User> userOptional = userRepository.findById(orderVO.getCustomerId().longValue());

        if(!userOptional.isPresent()){
            throw new InvalidCustomerIdException();
        }
        User user = userOptional.get();
        if(userIsNotAllowedToAddAnOrder(user.getRoles())){
            throw new InvalidOperationException();
        }
    }

    @Before("com.ctrln.practice.onlineShop.aspects.SecurityAspect.deliver()")
    public void checkSecurityBeforeDelivering(JoinPoint joinPoint) throws InvalidCustomerIdException, InvalidOperationException {
        Long customerId = (Long) joinPoint.getArgs()[1];
        Optional<User> userOptional = userRepository.findById(customerId);
        if(!userOptional.isPresent()){
            throw new InvalidCustomerIdException();
        }
        User user = userOptional.get();
        if(userIsNotAllowedToDeliver(user.getRoles())){
            throw new InvalidOperationException();
        }
        System.out.println(customerId);
    }

    @Before("com.ctrln.practice.onlineShop.aspects.SecurityAspect.cancelOrder()")
    public void checkSecurityBeforeCancelingOrder(JoinPoint joinPoint) throws InvalidCustomerIdException, InvalidOperationException {
        Long customerId = (Long) joinPoint.getArgs()[1];
        Optional<User> userOptional = userRepository.findById(customerId);
        if(!userOptional.isPresent()){
            throw new InvalidCustomerIdException();
        }
        User user = userOptional.get();
        if(userIsNotAllowedToCancelOrder(user.getRoles())){
            throw new InvalidOperationException();
        }
        System.out.println(customerId);
    }

    @Before("com.ctrln.practice.onlineShop.aspects.SecurityAspect.returnOrder()")
    public void checkSecurityBeforeReturningOrder(JoinPoint joinPoint) throws InvalidCustomerIdException, InvalidOperationException {
        Long customerId = (Long) joinPoint.getArgs()[1];
        Optional<User> userOptional = userRepository.findById(customerId);
        if(!userOptional.isPresent()){
            throw new InvalidCustomerIdException();
        }
        User user = userOptional.get();
        if(userIsNotAllowedToReturnOrder(user.getRoles())){
            throw new InvalidOperationException();
        }
        System.out.println(customerId);
    }

    @Before("com.ctrln.practice.onlineShop.aspects.SecurityAspect.addingStock()")
    public void checkSecurityBeforeAddingStock(JoinPoint joinPoint) throws InvalidCustomerIdException, InvalidOperationException {
        Long customerId = (Long) joinPoint.getArgs()[2];
        Optional<User> userOptional = userRepository.findById(customerId);
        if(!userOptional.isPresent()){
            throw new InvalidCustomerIdException();
        }
        User user = userOptional.get();
        if(userIsNotAllowedToAddStock(user.getRoles())){
            throw new InvalidOperationException();
        }
        System.out.println(customerId);
    }



    private boolean userIsNotAllowedToAddAnOrder(Collection<Roles> roles) {
        return !roles.contains(Roles.CLIENT);
    }

    private boolean userIsNotAllowedToAddProduct(Collection<Roles> roles) {
        return !roles.contains(Roles.ADMIN);
    }

    private boolean userIsNotAllowedToUpdateProduct(Collection<Roles> roles) {
        return !roles.contains(Roles.ADMIN) && !roles.contains(Roles.EDITOR);
    }

    private boolean userIsNotAllowedToDeleteProduct(Collection<Roles> roles) {
        return !roles.contains(Roles.ADMIN);
    }

    private boolean userIsNotAllowedToDeliver(Collection<Roles> roles) {
        return !roles.contains(Roles.EXPEDITOR);
    }

    private boolean userIsNotAllowedToCancelOrder(Collection<Roles> roles) {
        return !roles.contains(Roles.CLIENT);
    }

    private boolean userIsNotAllowedToReturnOrder(Collection<Roles> roles) {
        return !roles.contains(Roles.CLIENT);
    }

    private boolean userIsNotAllowedToAddStock(Collection<Roles> roles) {
        return !roles.contains(Roles.ADMIN);
    }
}
