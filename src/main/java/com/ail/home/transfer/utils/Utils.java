package com.ail.home.transfer.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Common utils.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class Utils {

	public static final ZoneOffset ZONE_OFFSET_UTC = ZoneOffset.UTC;
	public static final ZoneId ZONE_UTC = ZoneId.from(ZONE_OFFSET_UTC);
	public static final String ISO8601_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	public static final DateTimeFormatter DATE_TIME_FORMATTER =
		DateTimeFormatter.ofPattern(ISO8601_DATE_TIME_FORMAT).withZone(ZoneId.from(ZONE_OFFSET_UTC));

	public static LocalDateTime localDateTimeNow() {
		return LocalDateTime.now(ZONE_UTC).truncatedTo(ChronoUnit.MILLIS);
	}

	public static LocalDate localDateNow() {
		return LocalDate.now(ZONE_UTC);
	}

	public static String localDateTimeNowString() {
		return DATE_TIME_FORMATTER.format(localDateTimeNow());
	}

	public static String localDateTimeString(final LocalDateTime localDateTime) {
		return DATE_TIME_FORMATTER.format(localDateTime);
	}

	public static String localDateTimeString(final Date date) {
		return DATE_TIME_FORMATTER.format(date.toInstant());
	}

	public static LocalDateTime parseLocalDateTime(final String localDateTimeString) {
		return LocalDateTime.parse(localDateTimeString, DATE_TIME_FORMATTER);
	}

	public static LocalDateTime parseZonedDateTime(final ZonedDateTime zonedDateTime) {
		final Instant instant = zonedDateTime.withZoneSameInstant(ZONE_OFFSET_UTC).toInstant();
		final LocalDateTime localDateTime = instant.atZone(ZONE_OFFSET_UTC).toLocalDateTime();
		return parseLocalDateTime(localDateTime.format(DATE_TIME_FORMATTER));
	}
}
