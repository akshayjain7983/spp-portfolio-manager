package spp.portfolio.rest.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "spp.portfolio")
public class SppServer
{
    public static void main(String[] args)
    {
        SpringApplication.run(SppServer.class);
    }
}
