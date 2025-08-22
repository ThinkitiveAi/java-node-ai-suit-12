package com.healthfirst.healthfirstserver.domain.dto;

import com.healthfirst.healthfirstserver.domain.enums.AppointmentType;
import com.healthfirst.healthfirstserver.domain.enums.RecurrencePattern;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AvailabilityRequest {

    @NotBlank(message = "Provider ID is required")
    private String providerId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @NotBlank(message = "Timezone is required")
    private String timezone;

    private boolean isRecurring = false;

    private RecurrencePattern recurrencePattern;

    private LocalDate recurrenceEndDate;

    @Min(value = 5, message = "Slot duration must be at least 5 minutes")
    @Max(value = 240, message = "Slot duration cannot exceed 240 minutes")
    private int slotDurationMinutes = 30;

    @Min(value = 0, message = "Break duration cannot be negative")
    @Max(value = 60, message = "Break duration cannot exceed 60 minutes")
    private int breakDurationMinutes = 0;

    @NotNull(message = "Appointment type is required")
    private AppointmentType appointmentType;

    @Valid
    private LocationDto location;

    private PricingDto pricing;

    private List<@NotBlank(message = "Special requirement cannot be blank") String> specialRequirements;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    // Getters and Setters
    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public boolean isRecurring() { return isRecurring; }
    public void setRecurring(boolean recurring) { isRecurring = recurring; }

    public RecurrencePattern getRecurrencePattern() { return recurrencePattern; }
    public void setRecurrencePattern(RecurrencePattern recurrencePattern) { this.recurrencePattern = recurrencePattern; }

    public LocalDate getRecurrenceEndDate() { return recurrenceEndDate; }
    public void setRecurrenceEndDate(LocalDate recurrenceEndDate) { this.recurrenceEndDate = recurrenceEndDate; }

    public int getSlotDurationMinutes() { return slotDurationMinutes; }
    public void setSlotDurationMinutes(int slotDurationMinutes) { this.slotDurationMinutes = slotDurationMinutes; }

    public int getBreakDurationMinutes() { return breakDurationMinutes; }
    public void setBreakDurationMinutes(int breakDurationMinutes) { this.breakDurationMinutes = breakDurationMinutes; }

    public AppointmentType getAppointmentType() { return appointmentType; }
    public void setAppointmentType(AppointmentType appointmentType) { this.appointmentType = appointmentType; }

    public LocationDto getLocation() { return location; }
    public void setLocation(LocationDto location) { this.location = location; }

    public PricingDto getPricing() { return pricing; }
    public void setPricing(PricingDto pricing) { this.pricing = pricing; }

    public List<String> getSpecialRequirements() { return specialRequirements; }
    public void setSpecialRequirements(List<String> specialRequirements) { this.specialRequirements = specialRequirements; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public static class LocationDto {
        @NotNull(message = "Location type is required")
        private String type;

        @Size(max = 500, message = "Address cannot exceed 500 characters")
        private String address;

        @Size(max = 50, message = "Room number cannot exceed 50 characters")
        private String roomNumber;

        // Getters and Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getRoomNumber() { return roomNumber; }
        public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    }

    public static class PricingDto {
        @DecimalMin(value = "0.0", message = "Base fee cannot be negative")
        private double baseFee;

        private boolean insuranceAccepted = false;

        @Size(min = 3, max = 3, message = "Currency must be a 3-letter code")
        private String currency = "USD";

        // Getters and Setters
        public double getBaseFee() { return baseFee; }
        public void setBaseFee(double baseFee) { this.baseFee = baseFee; }

        public boolean isInsuranceAccepted() { return insuranceAccepted; }
        public void setInsuranceAccepted(boolean insuranceAccepted) { this.insuranceAccepted = insuranceAccepted; }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
    }
}