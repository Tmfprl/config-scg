package org.example.web_mng_authentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient  // eureka client service
@SpringBootApplication
public class WebMngAuthenticationApplication {

    public static void main(String[] args) {
        SpringApplication.
                run(WebMngAuthenticationApplication.class, args);
    }

}
