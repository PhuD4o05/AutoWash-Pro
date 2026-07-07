package com.carwash.carwashsystem.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DateTimeUtils {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }

    public static String formatTime(LocalTime time) {
        return time != null ? time.format(TIME_FORMATTER) : "";
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : "";
    }

    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
    }

    // Sửa method generateTimeSlots - tạo danh sách các khung giờ cách đều nhau
    public static List<LocalDateTime> generateTimeSlots(LocalDate date, int startHour, int endHour, int intervalMinutes) {
        List<LocalDateTime> slots = new ArrayList<>();
        LocalDateTime current = date.atTime(startHour, 0);
        LocalDateTime end = date.atTime(endHour, 0);
        while (current.isBefore(end)) {
            slots.add(current);
            current = current.plusMinutes(intervalMinutes);
        }
        return slots;
    }
}