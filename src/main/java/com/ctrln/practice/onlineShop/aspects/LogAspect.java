package com.ctrln.practice.onlineShop.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Aspect
@Component
public class LogAspect {

    @Pointcut("execution(* com.ctrln.practice.onlineShop.controllers.ProductController.addProduct(..))")
    public void addProductPointcut(){}

    @Pointcut("execution(* com.ctrln.practice.onlineShop.controllers.ProductController.updateProduct(..))")
    public void updateProductPointcut(){}

    @Pointcut("execution(* com.ctrln.practice.onlineShop.controllers.OrderController.addOrder(..))")
    public void addOrderPointcut(){}

    @Pointcut("execution(* com.ctrln.practice.onlineShop.controllers.OrderController.deliver(..))")
    public void deliverOrderPointcut(){}

    @Pointcut("execution(* com.ctrln.practice.onlineShop.controllers.OrderController.cancelOrder(..))")
    public void cancelOrderPointcut(){}

    @Pointcut("execution(* com.ctrln.practice.onlineShop.controllers.OrderController.returnOrder(..))")
    public void returnOrderPointcut(){}

    @Pointcut("execution(* com.ctrln.practice.onlineShop.controllers.ProductController.addStock(..))")
    public void addStockPointcut(){}

    @Before("com.ctrln.practice.onlineShop.aspects.LogAspect.addProductPointcut()")
    public void before(JoinPoint joinPoint){
        System.out.println("In before aspect at " + new Date());
        System.out.println("ProductVO: " + joinPoint.getArgs()[0]);
        System.out.println("Customer had id: " + joinPoint.getArgs()[1]);
    }

    @Before("com.ctrln.practice.onlineShop.aspects.LogAspect.updateProductPointcut()")
    public void beforeUpdate(JoinPoint joinPoint){
        System.out.println("In before aspect at " + new Date() + " for doing an update");
        System.out.println("ProductVO: " + joinPoint.getArgs()[0]);
        System.out.println("Customer had id: " + joinPoint.getArgs()[1]);
    }

    @Before("com.ctrln.practice.onlineShop.aspects.LogAspect.addOrderPointcut()")
    public void beforeAddingAnOrder(JoinPoint joinPoint){
        System.out.println("In before aspect at " + new Date() + " for adding an order");
        System.out.println("OrderVO: " + joinPoint.getArgs()[0]);
    }

    @Before("com.ctrln.practice.onlineShop.aspects.LogAspect.deliverOrderPointcut()")
    public void beforeDeliveringAnOrder(JoinPoint joinPoint){
        System.out.println("In before aspect at " + new Date() + " for delivering order");
        System.out.println("OrderId: " + joinPoint.getArgs()[0]);
        System.out.println("Customer had id: " + joinPoint.getArgs()[1]);
    }

    @Before("com.ctrln.practice.onlineShop.aspects.LogAspect.cancelOrderPointcut()")
    public void beforeCancelingAnOrder(JoinPoint joinPoint){
        System.out.println("In before aspect at " + new Date() + " for canceling order");
        System.out.println("OrderVO: " + joinPoint.getArgs()[0]);
        System.out.println("Customer had id: " + joinPoint.getArgs()[1]);
    }

    @Before("com.ctrln.practice.onlineShop.aspects.LogAspect.returnOrderPointcut()")
    public void beforeReturningAnOrder(JoinPoint joinPoint){
        System.out.println("In before aspect at " + new Date() + " for returning an order");
        System.out.println("OrderVO: " + joinPoint.getArgs()[0]);
        System.out.println("Customer had id: " + joinPoint.getArgs()[1]);
    }

    @Before("com.ctrln.practice.onlineShop.aspects.LogAspect.addStockPointcut()")
    public void beforeAddingStock(JoinPoint joinPoint){
        System.out.println("In before aspect at " + new Date() + " before adding stock");
        System.out.println("Product code: " + joinPoint.getArgs()[0]);
        System.out.println("Quantity: " + joinPoint.getArgs()[1]);
        System.out.println("Customer had id: " + joinPoint.getArgs()[2]);
    }

    @After("com.ctrln.practice.onlineShop.aspects.LogAspect.addProductPointcut()")
    public void after(JoinPoint joinPoint){
        System.out.println("In after aspect at " + new Date());
    }

    @After("com.ctrln.practice.onlineShop.aspects.LogAspect.addOrderPointcut()")
    public void afterAddingOrder(JoinPoint joinPoint){
        System.out.println("In after aspect at " + new Date());
    }

    @After("com.ctrln.practice.onlineShop.aspects.LogAspect.deliverOrderPointcut()")
    public void afterDeliveringOrder(JoinPoint joinPoint){
        System.out.println("In after aspect at " + new Date());
    }

    @After("com.ctrln.practice.onlineShop.aspects.LogAspect.cancelOrderPointcut()")
    public void afterCancelingOrder(JoinPoint joinPoint){
        System.out.println("In after aspect at " + new Date());
    }

    @After("com.ctrln.practice.onlineShop.aspects.LogAspect.returnOrderPointcut()")
    public void afterReturningOrder(JoinPoint joinPoint){
        System.out.println("In after aspect at " + new Date());
    }

    @After("com.ctrln.practice.onlineShop.aspects.LogAspect.addStockPointcut()")
    public void afterAddingStock(JoinPoint joinPoint){
        System.out.println("In after aspect at " + new Date());
    }
}
