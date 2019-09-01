package com.dxn;

import com.dxn.bussiness.ClientCompanyService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(ClientApplication.class, args);
        ClientCompanyService bean = ctx.getBean(ClientCompanyService.class);
        try {
            System.out.println("test = " + bean.test());
            System.out.println("test = " + bean.test());
            System.out.println("test = " + bean.test());
            System.out.println("test = " + bean.test());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
