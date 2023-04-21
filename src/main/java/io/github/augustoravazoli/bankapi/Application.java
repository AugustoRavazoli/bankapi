package io.github.augustoravazoli.bankapi;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.CLASS;
import java.lang.annotation.Target;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@ConfigurationPropertiesScan
@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public WebClient webClient() {
    return WebClient.create();
  }

  @Target(CONSTRUCTOR)
  @Retention(CLASS)
  public static @interface Default {}

}
