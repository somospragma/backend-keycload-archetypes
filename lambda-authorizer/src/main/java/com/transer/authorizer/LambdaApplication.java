package com.transer.authorizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class LambdaApplication {

    public static void main(String[] args) {
      SpringApplication.run(LambdaApplication.class, args);
    }
}
