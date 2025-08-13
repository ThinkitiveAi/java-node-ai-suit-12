package com.healthfirst.healthfirstserver.repository;

import com.healthfirst.healthfirstserver.domain.entity.Provider;
import com.healthfirst.healthfirstserver.domain.entity.ProviderAvailability;
import com.healthfirst.healthfirstserver.domain.enums.AvailabilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProviderAvailabilityRepository extends JpaRepository<ProviderAvailability, UUID> {
    
    List<ProviderAvailability> findByProviderIdAndDateBetween(
            UUID providerId, LocalDate startDate, LocalDate endDate);
    
    List<ProviderAvailability> findByProviderIdAndDateAndStatus(
            UUID providerId, LocalDate date, AvailabilityStatus status);
    
    @Query("SELECT pa FROM ProviderAvailability pa " +
           "WHERE pa.provider.id = :providerId " +
           "AND pa.date = :date " +
           "AND ((pa.startTime <= :endTime AND pa.endTime >= :startTime) " +
           "OR (pa.recurrencePattern IS NOT NULL AND pa.recurrenceEndDate >= :date))")
    List<ProviderAvailability> findOverlappingSlots(
            @Param("providerId") UUID providerId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);
    
    @Query("SELECT pa FROM ProviderAvailability pa " +
           "WHERE pa.provider.id = :providerId " +
           "AND pa.date BETWEEN :startDate AND :endDate " +
           "AND pa.status = 'AVAILABLE' " +
           "AND (pa.maxAppointmentsPerSlot > pa.currentAppointments OR pa.maxAppointmentsPerSlot IS NULL)")
    List<ProviderAvailability> findAvailableSlotsInRange(
            @Param("providerId") UUID providerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT DISTINCT pa.provider.id FROM ProviderAvailability pa " +
           "WHERE pa.date BETWEEN :startDate AND :endDate " +
           "AND pa.status = 'AVAILABLE' " +
           "AND (pa.maxAppointmentsPerSlot > pa.currentAppointments OR pa.maxAppointmentsPerSlot IS NULL)")
    List<UUID> findProvidersWithAvailabilityInRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
