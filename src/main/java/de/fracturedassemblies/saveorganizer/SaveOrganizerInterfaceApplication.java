package de.fracturedassemblies.saveorganizer;

import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;

//@formatter:off
@OpenAPIDefinition(
        info = @Info(
                title = "Save Organizer Interface for Elden Ring",
                description = "REST Interface for save organizer implementations",
                contact = @Contact(
                        name = "Fractured Assemblies",
                        email = "info@fractured-assemblies.de"
                )),
        servers = {
                @Server(url = "http://localhost:8080/save-organizer-interface")
        }
)
//@formatter:on
@SpringBootApplication
public class SaveOrganizerInterfaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaveOrganizerInterfaceApplication.class, args);
    }
}
