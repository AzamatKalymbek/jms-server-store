package kz.teamvictus.store.core;


import kz.teamvictus.store.core.config.ApplicationSwaggerConfig;
import kz.teamvictus.store.core.repository.ResourceUtilRepositoryImpl;
import kz.teamvictus.store.core.util.audit.AuditorAwareImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@SpringBootApplication
//@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableEurekaServer
@EnableMongoAuditing
//@Import(ApplicationSwaggerConfig.class)
@EnableMongoRepositories(repositoryBaseClass = ResourceUtilRepositoryImpl.class)
public class CoreApplication {

    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl();
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(CoreApplication.class, args);
    }
}
