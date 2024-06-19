package com.poc.function.pub.ploomes.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.function.pub.ploomes.dto.DealsDTO;
import com.poc.function.pub.ploomes.dto.DealsHeadDTO;
import com.poc.function.pub.ploomes.feignclient.PloomesClientMS;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PloomesClientGatewayImpl implements PloomesClientGateway {

    private final PloomesClientMS ploomesClientMS;

    public PloomesClientGatewayImpl(PloomesClientMS ploomesClientMS) {
        this.ploomesClientMS = ploomesClientMS;
    }

    @Override
    public List<String> getDeals() {
        final String select = "$select=Id, Title, ContactId, ContactName, StageId, StatusId";
        final String filter = "$filter=StageId+eq+10004211+and+(StatusId+eq+1+or+StatusId+eq+2)";
        final DealsHeadDTO deals = toObject(ploomesClientMS.getDeals(select, filter));
        return deals.value().stream().map(DealsDTO::ContactId).toList();
    }

    @Override
    public String getContacts(String dealsFilter) {
        final String expand = "$expand=City($expand=State), Phones, Contacts($expand=Role($select=Id,Name)), OtherProperties($select=Id, FieldId, FieldKey, ContactId, StringValue, BigStringValue, IntegerValue, DecimalValue, DateTimeValue, BoolValue, ObjectValueId, ObjectValueName)";
        final String filter = "&$filter=Id+eq+%s".formatted(dealsFilter);
        return ploomesClientMS.getContacts(expand, filter);
    }

    private DealsHeadDTO toObject(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, DealsHeadDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
