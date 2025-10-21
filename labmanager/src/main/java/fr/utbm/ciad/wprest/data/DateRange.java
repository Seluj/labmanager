package fr.utbm.ciad.wprest.data;

import java.time.LocalDate;

/**
 * Simple definition of a range of dates.
 *
 * @param startDate the start date of the range
 * @param endDate   the end date of the range
 */
public record DateRange(LocalDate startDate,
                        LocalDate endDate) {
}