package kz.teamvictus.store.core.config;

import kz.teamvictus.store.core.controller.UserController;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

@Component
@ApplicationPath("/boot-jersey")
public class JerseyConfiguration  extends ResourceConfig {
    public JerseyConfiguration() {
        register(UserController.class);
    }
}
