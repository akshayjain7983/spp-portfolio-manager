package spp.portfolio.rest.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = "spp.portfolio")
@EnableJpaAuditing
@EnableTransactionManagement
public class SppServer
{
    public static void main(String[] args)
    {
        SpringApplication.run(SppServer.class);
    }
}
