package com.caiohportella.pm.patientservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import com.caiohportella.pm.patientservice.kafka.KafkaProducer;
import com.google.api.Billing;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BillingServiceGrpcClient {
    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;
    private final KafkaProducer kafkaProducer;

    public BillingServiceGrpcClient(
        @Value("${billing.service.address:localhost}") String serviceAddress,
        @Value("${billing.service.grpc.port:9001}") int serverPort,
        KafkaProducer kafkaProducer) {
        System.out.printf("Connecting to Billing Service GRPC at {}:{}", serviceAddress, serverPort);
    
        ManagedChannel channel = ManagedChannelBuilder.forAddress(serviceAddress, serverPort).usePlaintext().build();
    
        blockingStub = BillingServiceGrpc.newBlockingStub(channel);
        this.kafkaProducer = kafkaProducer;
    }

    @CircuitBreaker(name = "billing-service", fallbackMethod = "billingFallback")
    @Retry(name = "billingRetry")
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

    public BillingResponse billingFallback(String patientId, String name, String email, Throwable throwable) {
        System.out.printf("Billing service is unavailable. Triggered " + "fallback: {}", throwable.getMessage());

        kafkaProducer.sendBillingAccountEvent(patientId, name, email);

        return BillingResponse.newBuilder()
                .setAccountId("")
                .setStatus("PENDING")
                .build();
    }
}
