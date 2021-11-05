package SpringDemo.auto.aspect;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class Cut {

    @Pointcut("execution(* SpringDemo.auto.dao.*.*(..))")
    public void mycut(){

    }


    @After(value = "mycut()")
    public void after(){
        System.out.println("after.......");
    }
}
