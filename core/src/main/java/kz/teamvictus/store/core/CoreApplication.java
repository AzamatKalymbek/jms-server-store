package kz.teamvictus.store.core;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class CoreApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(CoreApplication.class, args);
    }
}
