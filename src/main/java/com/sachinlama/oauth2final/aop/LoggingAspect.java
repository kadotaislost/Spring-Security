package com.sachinlama.oauth2final.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // Pointcut for all controller methods
    @Pointcut("execution(* com.sachinlama.oauth2final.controller.*.*(..))")
    public void controllerMethods() {}

    // Pointcut for all service methods
    @Pointcut("execution(* com.sachinlama.oauth2final.service.*.*(..))")
    public void serviceMethods() {}

    // Log before controllers are called
    @Before("controllerMethods()")
    public void logBeforeController(JoinPoint joinPoint) {
        log.info("Entering: {} with arguments: {}",
                joinPoint.getSignature().toShortString(),
                Arrays.toString(joinPoint.getArgs()));
    }

    // Log after controllers return
    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfterController(JoinPoint joinPoint, Object result) {
        log.info("Exiting: {} with result: {}",
                joinPoint.getSignature().toShortString(),
                result);
    }

    // Log exceptions in controllers
    @AfterThrowing(pointcut = "controllerMethods()", throwing = "exception")
    public void logExceptionInController(JoinPoint joinPoint, Throwable exception) {
        log.error("Exception in {}: {} - {}",
                joinPoint.getSignature().toShortString(),
                exception.getClass().getName(),
                exception.getMessage());
    }

    // Log services with execution time
    @Around("serviceMethods()")
    public Object logAroundService(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;

            log.debug("Service: {} executed in {}ms",
                    joinPoint.getSignature().toShortString(),
                    executionTime);

            return result;
        } catch (Exception e) {
            log.error("Exception in service method: {} - {}",
                    joinPoint.getSignature().toShortString(),
                    e.getMessage());
            throw e;
        }
    }
}