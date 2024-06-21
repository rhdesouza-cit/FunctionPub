package com.poc.function.pub.external.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.poc.function.pub.external.dto.MessageDTO;
import com.poc.function.pub.external.feignclient.ExternalClientMS;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExternalClientGatewayImpl implements ExternalClientGateway {

    private final ExternalClientMS externalClientMS;

    public ExternalClientGatewayImpl(ExternalClientMS externalClientMS) {
        this.externalClientMS = externalClientMS;
    }

    @Override
    public List<String> getMessage() {
        return externalClientMS.getMessage().stream()
            .map(this::toJson)
            .toList();
    }

    private String toJson(MessageDTO messageDTO) {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = null;
        try {
            json = ow.writeValueAsString(messageDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }

}
