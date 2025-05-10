package com.caiohportella.billing_service.grpc;

import billing.BillingResponse;
import billing.BillingServiceGrpc.BillingServiceImplBase;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class BillingGrpcService extends BillingServiceImplBase {
    @Override
    public void createBillingAccount(billing.BillingRequest billingRequest,
            StreamObserver<billing.BillingResponse> responseObserver) {
        System.out.println("Received request to create billing account: " + billingRequest.toString());

        // Business logic to create billing account would go here

        BillingResponse response = BillingResponse.newBuilder()
                .setAccountId("12345")
                .setStatus("Account created successfully")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
