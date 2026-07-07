package com.carwash.carwashsystem.scheduler;

import com.carwash.carwashsystem.entity.Booking;
import com.carwash.carwashsystem.enums.BookingStatus;
import com.carwash.carwashsystem.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingCleanupScheduler {
    private final BookingRepository bookingRepository;

    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanupExpiredBookings() {
        log.info("Starting booking cleanup scheduler...");
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);
        List<BookingStatus> statuses = List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);

        // Gọi method mới
        List<Booking> expiredBookings = bookingRepository.findByStatusInAndScheduledTimeBefore(statuses, threshold);

        for (Booking booking : expiredBookings) {
            booking.setStatus(BookingStatus.NO_SHOW);
            log.info("Booking {} marked as NO_SHOW", booking.getId());
        }
        bookingRepository.saveAll(expiredBookings);
        log.info("Cleaned up {} expired bookings", expiredBookings.size());
    }
}