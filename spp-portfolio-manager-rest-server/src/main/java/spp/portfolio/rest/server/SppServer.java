package spp.portfolio.rest.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(info = @Info(title = "Portfolio Manager Open API"))
@SpringBootApplication(scanBasePackages = "spp.portfolio")
@EnableJpaAuditing
@EnableTransactionManagement
@EntityScan(basePackages = {"spp.portfolio"})
@EnableJpaRepositories(basePackages = {"spp.portfolio"})
public class SppServer
{
    public static void main(String[] args)
    {
        SpringApplication.run(SppServer.class);
    }
}
