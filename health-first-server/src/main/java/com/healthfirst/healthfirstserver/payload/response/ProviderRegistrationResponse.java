package com.healthfirst.healthfirstserver.payload.response;

import com.healthfirst.healthfirstserver.domain.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ProviderRegistrationResponse {
    private boolean success;
    private String message;
    private ProviderData data;

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProviderData {
        private String providerId;
        private String email;
        private VerificationStatus verificationStatus;
    }

    public static ProviderRegistrationResponse success(UUID providerId, String email) {
        return ProviderRegistrationResponse.builder()
                .success(true)
                .message("Provider registered successfully. Verification email sent.")
                .data(ProviderData.builder()
                        .providerId(providerId.toString())
                        .email(email)
                        .verificationStatus(VerificationStatus.PENDING)
                        .build())
                .build();
    }
}
