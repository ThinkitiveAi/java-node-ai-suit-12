package com.healthfirst.healthfirstserver.domain.dto;

import com.healthfirst.healthfirstserver.domain.enums.AppointmentType;
import com.healthfirst.healthfirstserver.domain.enums.AvailabilityStatus;
import com.healthfirst.healthfirstserver.domain.enums.RecurrencePattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityResponse {
    
    private UUID id;
    private UUID providerId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String timezone;
    private boolean isRecurring;
    private RecurrencePattern recurrencePattern;
    private LocalDate recurrenceEndDate;
    private int slotDurationMinutes;
    private int breakDurationMinutes;
    private AvailabilityStatus status;
    private int maxAppointmentsPerSlot;
    private int currentAppointments;
    private AppointmentType appointmentType;
    private LocationDto location;
    private PricingDto pricing;
    private List<String> specialRequirements;
    private String notes;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationDto {
        private String type;
        private String address;
        private String roomNumber;
        private String meetingUrl;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PricingDto {
        private double baseFee;
        private boolean insuranceAccepted;
        private String currency;
        private String insuranceProvider;
        private Double copayAmount;
        private Integer coveragePercentage;
    }
    
    public static AvailabilityResponse fromEntity(com.healthfirst.healthfirstserver.domain.entity.ProviderAvailability entity) {
        return AvailabilityResponse.builder()
                .id(entity.getId())
                .providerId(entity.getProvider().getId())
                .date(entity.getDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .timezone(entity.getTimezone())
                .isRecurring(entity.isRecurring())
                .recurrencePattern(entity.getRecurrencePattern())
                .recurrenceEndDate(entity.getRecurrenceEndDate())
                .slotDurationMinutes(entity.getSlotDurationMinutes())
                .breakDurationMinutes(entity.getBreakDurationMinutes())
                .status(entity.getStatus())
                .maxAppointmentsPerSlot(entity.getMaxAppointmentsPerSlot())
                .currentAppointments(entity.getCurrentAppointments())
                .appointmentType(entity.getAppointmentType())
                .location(entity.getLocation() != null ? 
                        LocationDto.builder()
                                .type(entity.getLocation().getType().name())
                                .address(entity.getLocation().getAddress())
                                .roomNumber(entity.getLocation().getRoomNumber())
                                .meetingUrl(entity.getLocation().getMeetingUrl())
                                .build() : null)
                .pricing(entity.getPricing() != null ?
                        PricingDto.builder()
                                .baseFee(entity.getPricing().getBaseFee().doubleValue())
                                .insuranceAccepted(entity.getPricing().isInsuranceAccepted())
                                .currency(entity.getPricing().getCurrency())
                                .insuranceProvider(entity.getPricing().getInsuranceProvider())
                                .copayAmount(entity.getPricing().getCopayAmount() != null ? 
                                        entity.getPricing().getCopayAmount().doubleValue() : null)
                                .coveragePercentage(entity.getPricing().getCoveragePercentage())
                                .build() : null)
                .specialRequirements(entity.getSpecialRequirements())
                .notes(entity.getNotes())
                .build();
    }
}
