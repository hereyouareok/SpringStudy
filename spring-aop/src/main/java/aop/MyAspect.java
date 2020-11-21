package aop;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @author YanQin
 * @version v1.0.0
 * @Description : TODO
 * @Create on : 2020/11/21 17:25
 **/
@Component("myAspect")
@Aspect //标记当前MyAspect是一个切面类
public class MyAspect {
    //前置
    @Before("pointCut()")
    public void before() {
        System.out.println("前置增强....");
    }

    @After("MyAspect.pointCut()")
    public void afterReturn() {
        System.out.println("后置增强....");
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("环绕前增强....");
        Object proceed = pjp.proceed();
        System.out.println("环绕后增强....");
        return proceed;
    }

    @Pointcut("execution( * aop.*.*(..))")
    public void pointCut() {

    }
}
