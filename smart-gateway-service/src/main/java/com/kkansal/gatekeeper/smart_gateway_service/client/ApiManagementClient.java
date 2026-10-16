package com.kkansal.gatekeeper.smart_gateway_service.client;

import com.kkansal.gatekeeper.smart_gateway_service.dto.response.SnapshotResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ApiManagementClient {

    private RestClient managementRestClient;

    public ApiManagementClient(@Qualifier("managementRestClient") RestClient managementRestClient) {
        this.managementRestClient = managementRestClient;
    }

    public SnapshotResponse getSnaphotData() {

        ResponseEntity<SnapshotResponse> response = managementRestClient.get()
                .uri("/internal/routes/snapshot")
                .retrieve()
                .toEntity(SnapshotResponse.class);

        return response.getBody();
    }
}
