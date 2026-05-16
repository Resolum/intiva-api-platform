package com.resolum.intiva.platform.shared.domain.valueobjects;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/**
 * InstantComparer is a utility class that provides methods for comparing Instant objects, specifically for calculating the number of days between a given Instant and the current time.
 * @summary
 * The compareDays method takes an Instant as input, converts it to a LocalDate, and calculates the number of days between that date and the current date. It returns the number of days as an integer.
 */
public class InstantComparer {

    private static LocalDate convertInstantToLocalDate(Instant instant) {
        return instant.atZone(ZoneId.of("UTC")).toLocalDate();
    }

    /**
     * Compares the given Instant with the current time and calculates the number of days between them. The method converts both Instants to LocalDate objects and uses ChronoUnit.DAYS to calculate the difference in days.
     * @param instant The Instant to compare with the current time. It should be in UTC format for accurate conversion to LocalDate.
     * @return The number of days between the given Instant and the current time as an integer. If the given Instant is in the past, the result will be positive; if it is in the future, the result will be negative.
     */
    public static int daysBetweenInstantAndNow(Instant instant) {
        var now = Instant.now();

        return daysBetweenTwoInstants(instant, now);
    }

    /**
     * Compares two Instants and calculates the number of days between them. The method converts both Instants to LocalDate objects and uses ChronoUnit.DAYS to calculate the difference in days.
     * @param firstInstant The first Instant to compare. It should be in UTC format for accurate conversion to LocalDate.
     * @param secondInstant The second Instant to compare. It should also be in UTC format for accurate conversion to LocalDate.
     * @return The number of days between the two Instants as an integer. If the first Instant is before the second Instant, the result will be positive; if the first Instant is after the second Instant, the result will be negative.
     */
    public static int daysBetweenTwoInstants(Instant firstInstant, Instant secondInstant) {
        var firstDate = convertInstantToLocalDate(firstInstant);
        var secondDate = convertInstantToLocalDate(secondInstant);

        long days = ChronoUnit.DAYS.between(firstDate, secondDate);

        return (int) days;
    }
}
