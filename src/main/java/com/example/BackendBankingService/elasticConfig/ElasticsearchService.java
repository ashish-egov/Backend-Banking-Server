package com.example.BackendBankingService.elasticConfig;

import com.example.BackendBankingService.model.Client;
import com.example.BackendBankingService.model.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ElasticsearchService {

    private static final String INDEX_CLIENT = "client";
    private static final String INDEX_TRANSACTION = "transaction";

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ObjectMapper objectMapper; // Inject the custom ObjectMapper bean

    public void addClient(Client client) throws IOException {
        IndexRequest indexRequest = new IndexRequest(INDEX_CLIENT);
        indexRequest.id(String.valueOf(client.getAccountId()));

        // Use the custom ObjectMapper to serialize the client object
        String clientJson = objectMapper.writeValueAsString(client);

        indexRequest.source(clientJson, XContentType.JSON);
        restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    }

}
