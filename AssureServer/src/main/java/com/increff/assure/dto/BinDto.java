package com.increff.assure.dto;

import com.increff.assure.pojo.BinPojo;
import com.increff.assure.service.ApiException;
import com.increff.assure.service.BinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BinDto {
    @Autowired
    private BinService binService;

    public ArrayList<Long> add(int numberOfBins) throws ApiException {
        return binService.addBins(numberOfBins);
    }

    public List<Long> getAll() throws ApiException {
        List<BinPojo> pojoList = binService.getAll();
        return pojoList.stream().map(BinPojo::getId).collect(Collectors.toList());
    }

    public String httpTest() {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = "http://localhost:7070/channel";
        ResponseEntity<String> response
                = restTemplate.getForEntity(fooResourceUrl + "/1", String.class);
        return response.toString();
    }
}