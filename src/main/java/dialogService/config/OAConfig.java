package dialogService.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class OAConfig {
    @Value("${application.author.name}")
    private String name;
    @Value("${application.author.tg}")
    private String email;


    @Bean
    public OpenAPI openAPIDescription() {
        Server localhostServer = new Server();
        localhostServer.setUrl("http://localhost:8080");
        localhostServer.setDescription("Local environment");

        Contact contact = new Contact();
        contact.setName(name);
        contact.setEmail(email);

        Info info = new Info()
                .title("micro service dialog")
                .version("v5")
                .contact(contact)
                .description("API for interaction with message and dialog");
        return new OpenAPI().info(info).servers(List.of(localhostServer));
    }
}
