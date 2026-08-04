package com.tplite.banking.identityservice.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    // Bắt (Intercept) tất cả các hàm nằm trong package controller
    @Around("execution(* com.tplite.banking.identityservice.controller.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        log.info("🚀 [AOP LOG] Bắt đầu gọi API: {}.{}()", className, methodName);

        // Cho phép Request đi tiếp vào Controller thực sự
        Object result = joinPoint.proceed(); 

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        log.info("✅ [AOP LOG] API {}.{}() chạy xong trong {} ms", className, methodName, executionTime);

        return result;
    }
}
