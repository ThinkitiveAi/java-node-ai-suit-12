package com.healthfirst.healthfirstserver.repository;

import com.healthfirst.healthfirstserver.domain.entity.AppointmentSlot;
import com.healthfirst.healthfirstserver.domain.entity.Provider;
import com.healthfirst.healthfirstserver.domain.enums.AvailabilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, UUID> {
    
    List<AppointmentSlot> findByProviderIdAndSlotStartTimeBetween(
            UUID providerId, ZonedDateTime start, ZonedDateTime end);
    
    List<AppointmentSlot> findByPatientIdAndSlotStartTimeAfter(
            UUID patientId, ZonedDateTime start);
    
    List<AppointmentSlot> findByProviderIdAndStatusAndSlotStartTimeBetween(
            UUID providerId, AvailabilityStatus status, ZonedDateTime start, ZonedDateTime end);
    
    @Query("SELECT a FROM AppointmentSlot a " +
           "WHERE a.availability.id = :availabilityId " +
           "AND a.slotStartTime >= :startTime " +
           "AND a.slotEndTime <= :endTime " +
           "AND a.status = 'AVAILABLE'")
    List<AppointmentSlot> findAvailableSlotsByAvailabilityAndTimeRange(
            @Param("availabilityId") UUID availabilityId,
            @Param("startTime") ZonedDateTime startTime,
            @Param("endTime") ZonedDateTime endTime);
    
    @Query("SELECT a FROM AppointmentSlot a " +
           "WHERE a.provider.id = :providerId " +
           "AND a.slotStartTime >= :startTime " +
           "AND a.slotEndTime <= :endTime " +
           "AND a.status = 'AVAILABLE'")
    List<AppointmentSlot> findAvailableSlotsByProviderAndTimeRange(
            @Param("providerId") UUID providerId,
            @Param("startTime") ZonedDateTime startTime,
            @Param("endTime") ZonedDateTime endTime);
    
    @Query("SELECT a FROM AppointmentSlot a " +
           "WHERE a.patient.id = :patientId " +
           "AND a.slotStartTime >= :startTime " +
           "AND a.status = 'BOOKED'")
    List<AppointmentSlot> findUpcomingBookedSlotsForPatient(
            @Param("patientId") UUID patientId,
            @Param("startTime") ZonedDateTime startTime);
    
    Optional<AppointmentSlot> findByBookingReference(String bookingReference);
    
    boolean existsByProviderIdAndSlotStartTimeBetweenAndStatusNot(
            UUID providerId, ZonedDateTime start, ZonedDateTime end, AvailabilityStatus status);
    
    @Query("SELECT COUNT(a) > 0 FROM AppointmentSlot a " +
           "WHERE a.provider.id = :providerId " +
           "AND a.status = 'BOOKED' " +
           "AND a.slotStartTime < :endTime " +
           "AND a.slotEndTime > :startTime")
    boolean hasConflictingAppointments(
            @Param("providerId") UUID providerId,
            @Param("startTime") ZonedDateTime startTime,
            @Param("endTime") ZonedDateTime endTime);
}
