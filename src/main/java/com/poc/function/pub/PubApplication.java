package com.poc.function.pub;

import com.poc.function.pub.ploomes.gateway.PloomesClientGateway;
import com.poc.function.pub.publisher.PublisherMessage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.function.Consumer;

@SpringBootApplication
@EnableFeignClients
public class PubApplication {

    private final PloomesClientGateway ploomesClientGateway;
    private final PublisherMessage publisherMessage;

    public PubApplication(PloomesClientGateway ploomesClientGateway, PublisherMessage publisherMessage) {
        this.ploomesClientGateway = ploomesClientGateway;
        this.publisherMessage = publisherMessage;
    }

    public static void main(String[] args) {
        SpringApplication.run(PubApplication.class, args);
    }

    @Bean
    public Consumer<String> getPloomes() {
        return topic -> {
            final List<String> deals = ploomesClientGateway.getDeals();
            for (String deal : deals) {
                String messages = ploomesClientGateway.getContacts(deal);
                try {
                    publisherMessage.publisher(topic, messages);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

}
