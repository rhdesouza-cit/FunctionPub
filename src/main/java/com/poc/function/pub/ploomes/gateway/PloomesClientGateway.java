package com.poc.function.pub.ploomes.gateway;

import java.util.List;

public interface PloomesClientGateway {

    List<String> getDeals();

    String getContacts(String dealsFilter);

}
