package com.poc.function.pub.ploomes.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "bullla-ms-core-ploomes-client", url = "${br.com.bullla.ploomes.url}")
public interface PloomesClientMS {

    @GetMapping(value = "/Deals")
    String getDeals(@RequestParam(value = "select") String select, @RequestParam(value = "filter") String filter);

    @GetMapping(value = "/Contacts")
    String getContacts(@RequestParam(value = "expand") String expand, @RequestParam(value = "filter") String filter);

}
