package kz.teamvictus.store.core;


import kz.teamvictus.store.core.config.ApplicationSwaggerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableEurekaClient
@Import(ApplicationSwaggerConfig.class)
public class CoreApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(CoreApplication.class, args);
    }
}
