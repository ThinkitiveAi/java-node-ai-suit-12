package com.healthfirst.healthfirstserver.service;

import org.springframework.stereotype.Service;

import java.time.*;
import java.time.zone.ZoneRules;
import java.util.Date;

/**
 * Service for handling timezone conversions and time-related operations.
 */
@Service
public class TimeZoneService {

    /**
     * Convert a local date and time to a specific timezone.
     *
     * @param localDate The local date
     * @param localTime The local time
     * @param fromZone The source timezone ID (e.g., "America/New_York")
     * @param toZone The target timezone ID (e.g., "UTC")
     * @return The converted ZonedDateTime in the target timezone
     */
    public ZonedDateTime convertToTimeZone(LocalDate localDate, LocalTime localTime, 
                                         String fromZone, String toZone) {
        ZoneId fromZoneId = ZoneId.of(fromZone);
        ZoneId toZoneId = ZoneId.of(toZone);
        
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        ZonedDateTime zonedDateTime = localDateTime.atZone(fromZoneId);
        
        return zonedDateTime.withZoneSameInstant(toZoneId);
    }

    /**
     * Convert a ZonedDateTime to a specific timezone.
     *
     * @param zonedDateTime The source ZonedDateTime
     * @param toZone The target timezone ID
     * @return The converted ZonedDateTime in the target timezone
     */
    public ZonedDateTime convertToTimeZone(ZonedDateTime zonedDateTime, String toZone) {
        return zonedDateTime.withZoneSameInstant(ZoneId.of(toZone));
    }

    /**
     * Get the current date and time in the specified timezone.
     *
     * @param zoneId The timezone ID (e.g., "America/New_York")
     * @return The current ZonedDateTime in the specified timezone
     */
    public ZonedDateTime nowInTimeZone(String zoneId) {
        return ZonedDateTime.now(ZoneId.of(zoneId));
    }

    /**
     * Convert a Date to a specific timezone.
     *
     * @param date The source Date
     * @param toZone The target timezone ID
     * @return The converted ZonedDateTime in the target timezone
     */
    public ZonedDateTime convertToTimeZone(Date date, String toZone) {
        return date.toInstant().atZone(ZoneId.of(toZone));
    }

    /**
     * Check if a timezone is valid.
     *
     * @param zoneId The timezone ID to validate
     * @return true if the timezone is valid, false otherwise
     */
    public boolean isValidTimeZone(String zoneId) {
        try {
            ZoneId.of(zoneId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get the current timezone offset in hours from UTC.
     *
     * @param zoneId The timezone ID
     * @return The offset in hours (e.g., -5 for EST, -4 for EDT)
     */
    public int getCurrentOffsetHours(String zoneId) {
        return ZonedDateTime.now(ZoneId.of(zoneId))
                .getOffset()
                .getTotalSeconds() / 3600;
    }

    /**
     * Convert a LocalDateTime from one timezone to another.
     *
     * @param localDateTime The source LocalDateTime
     * @param fromZone The source timezone ID
     * @param toZone The target timezone ID
     * @return The converted LocalDateTime in the target timezone
     */
    public LocalDateTime convertLocalDateTime(LocalDateTime localDateTime, 
                                            String fromZone, String toZone) {
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of(fromZone));
        return zonedDateTime.withZoneSameInstant(ZoneId.of(toZone)).toLocalDateTime();
    }

    /**
     * Check if a specific date is in daylight saving time for the given timezone.
     *
     * @param zoneId The timezone ID
     * @param date The date to check
     * @return true if the date is in DST, false otherwise
     */
    public boolean isDaylightSavingsTime(String zoneId, LocalDate date) {
        ZoneId zone = ZoneId.of(zoneId);
        ZoneRules rules = zone.getRules();
        return rules.isDaylightSavings(Instant.from(date.atStartOfDay(zone)));
    }
    
    /**
     * Get the system's default timezone ID as a string.
     *
     * @return the system's default timezone ID (e.g., "America/New_York")
     */
    public String getSystemZoneId() {
        return ZoneId.systemDefault().getId();
    }
}
