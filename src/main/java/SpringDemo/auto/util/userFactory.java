package SpringDemo.auto.util;

import SpringDemo.auto.dao.user;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class userFactory {
    @Autowired
    private user user;

//    public void setUser(user user) {
//        this.user = user;
//    }

    public void text(){
        System.out.println("hello world");
        System.out.println(user);
    }

    public static void main(String[] args) {
        ApplicationContext ac = new ClassPathXmlApplicationContext("spring.xml");
        userFactory userFactory = (SpringDemo.auto.util.userFactory) ac.getBean("userFactory");
        userFactory.text();
    }
}
