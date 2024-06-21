package com.poc.function.pub;

import com.poc.function.pub.external.gateway.ExternalClientGateway;
import com.poc.function.pub.publisher.PublisherMessage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.function.Consumer;

@SpringBootApplication
@EnableFeignClients
public class PubApplication {

    private final ExternalClientGateway externalClientGateway;
    private final PublisherMessage publisherMessage;

    public PubApplication(
        ExternalClientGateway externalClientGateway,
        PublisherMessage publisherMessage
                         ) {
        this.externalClientGateway = externalClientGateway;
        this.publisherMessage = publisherMessage;
    }

    public static void main(String[] args) {
        SpringApplication.run(PubApplication.class, args);
    }

    @Bean
    public Consumer<String> getMessages() {
        return topic ->
            externalClientGateway.getMessage()
                .forEach(message -> publisherMessage.publishMessage(topic, message));
    }

}
