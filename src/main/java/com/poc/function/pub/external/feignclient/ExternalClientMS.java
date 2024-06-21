package com.poc.function.pub.external.feignclient;

import com.poc.function.pub.external.dto.MessageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "external-client", url = "${br.com.external.url}")
public interface ExternalClientMS {

    @GetMapping(value = "/messages")
    List<MessageDTO> getMessage();

}
