package com.caiohportella.patientservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BillingServiceGrpcClient {
    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;

    public BillingServiceGrpcClient(
        @Value("${billing.service.address:localhost}") String serviceAddress,
        @Value("${billing.service.grpc.port:9001}") int serverPort
    ) {
        System.out.printf("Connecting to Billing Service GRPC at {}:{}", serviceAddress, serverPort);
    
        ManagedChannel channel = ManagedChannelBuilder.forAddress(serviceAddress, serverPort).usePlaintext().build();
    
        blockingStub = BillingServiceGrpc.newBlockingStub(channel);
    }

    public BillingResponse createBillingAccount(String patientId, String name, String email) {
        BillingRequest request = BillingRequest.newBuilder()
            .setPatientId(patientId)
            .setName(name)
            .setEmail(email)
            .build();

        BillingResponse response = blockingStub.createBillingAccount(request);
        System.out.printf("Received response from Billing Service via GRPC: %s", response.toString());
    
        return response;
    }
}
