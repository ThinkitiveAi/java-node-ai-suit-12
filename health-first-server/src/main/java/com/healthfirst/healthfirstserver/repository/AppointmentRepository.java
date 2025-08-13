package com.healthfirst.healthfirstserver.repository;

import com.healthfirst.healthfirstserver.domain.entity.Appointment;
import com.healthfirst.healthfirstserver.domain.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Appointment entities.
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    
    /**
     * Find an appointment by its booking reference.
     *
     * @param bookingReference the unique booking reference
     * @return an Optional containing the appointment if found
     */
    Optional<Appointment> findByBookingReference(String bookingReference);
    
    /**
     * Find all appointments for a specific patient.
     *
     * @param patientId the ID of the patient
     * @return a list of appointments for the patient
     */
    List<Appointment> findByPatientId(UUID patientId);
    
    /**
     * Find all appointments for a specific provider.
     *
     * @param providerId the ID of the provider
     * @return a list of appointments for the provider
     */
    List<Appointment> findByProviderId(UUID providerId);
    
    /**
     * Find appointments by status.
     *
     * @param status the status to search for
     * @return a list of appointments with the given status
     */
    List<Appointment> findByStatus(AppointmentStatus status);
    
    /**
     * Find appointments for a specific patient within a time range.
     *
     * @param patientId the ID of the patient
     * @param startTime the start of the time range (inclusive)
     * @param endTime the end of the time range (exclusive)
     * @return a list of appointments matching the criteria
     */
    @Query("SELECT a FROM Appointment a " +
           "WHERE a.patient.id = :patientId " +
           "AND a.startTime >= :startTime " +
           "AND a.startTime < :endTime " +
           "ORDER BY a.startTime")
    List<Appointment> findByPatientIdAndStartTimeBetween(
            @Param("patientId") UUID patientId,
            @Param("startTime") ZonedDateTime startTime,
            @Param("endTime") ZonedDateTime endTime
    );
    
    /**
     * Find appointments for a specific provider within a time range with pagination.
     *
     * @param providerId the ID of the provider
     * @param startTime the start of the time range (inclusive)
     * @param endTime the end of the time range (exclusive)
     * @param pageable the pagination information
     * @return a page of appointments matching the criteria
     */
    @Query("SELECT a FROM Appointment a " +
           "WHERE a.provider.id = :providerId " +
           "AND a.startTime >= :startTime " +
           "AND a.startTime < :endTime " +
           "ORDER BY a.startTime")
    Page<Appointment> findByProviderIdAndStartTimeBetween(
            @Param("providerId") UUID providerId,
            @Param("startTime") ZonedDateTime startTime,
            @Param("endTime") ZonedDateTime endTime,
            Pageable pageable
    );
    
    /**
     * Find appointments for a specific provider within a date range.
     *
     * @param providerId the ID of the provider
     * @param startDate the start of the date range (inclusive)
     * @param endDate the end of the date range (exclusive)
     * @return a list of appointments matching the criteria
     */
    @Query("SELECT a FROM Appointment a " +
           "WHERE a.provider.id = :providerId " +
           "AND a.startTime >= :startDate " +
           "AND a.startTime < :endDate " +
           "ORDER BY a.startTime")
    List<Appointment> findProviderAppointmentsInDateRange(
            @Param("providerId") UUID providerId,
            @Param("startDate") ZonedDateTime startDate,
            @Param("endDate") ZonedDateTime endDate
    );
    
    /**
     * Check if a provider has any overlapping appointments.
     *
     * @param providerId the ID of the provider
     * @param startTime the start time to check
     * @param endTime the end time to check
     * @param excludedAppointmentId the ID of an appointment to exclude (e.g., when updating)
     * @return true if there are any overlapping appointments
     */
    @Query("SELECT COUNT(a) > 0 FROM Appointment a " +
           "WHERE a.provider.id = :providerId " +
           "AND a.id != COALESCE(:excludedAppointmentId, '00000000-0000-0000-0000-000000000000') " +
           "AND ((a.startTime < :endTime AND a.endTime > :startTime))")
    boolean hasOverlappingAppointments(
            @Param("providerId") UUID providerId,
            @Param("startTime") ZonedDateTime startTime,
            @Param("endTime") ZonedDateTime endTime,
            @Param("excludedAppointmentId") UUID excludedAppointmentId
    );
    
    /**
     * Find upcoming appointments for a patient.
     *
     * @param patientId the ID of the patient
     * @param now the current time
     * @param limit the maximum number of appointments to return
     * @return a list of upcoming appointments
     */
    @Query("SELECT a FROM Appointment a " +
           "WHERE a.patient.id = :patientId " +
           "AND a.startTime > :now " +
           "AND a.status NOT IN (com.healthfirst.healthfirstserver.domain.enums.AppointmentStatus.CANCELLED, " +
           "com.healthfirst.healthfirstserver.domain.enums.AppointmentStatus.COMPLETED, " +
           "com.healthfirst.healthfirstserver.domain.enums.AppointmentStatus.NO_SHOW) " +
           "ORDER BY a.startTime ASC")
    List<Appointment> findUpcomingAppointments(
            @Param("patientId") Long patientId,
            @Param("now") ZonedDateTime now,
            @Param("limit") int limit
    );
}
