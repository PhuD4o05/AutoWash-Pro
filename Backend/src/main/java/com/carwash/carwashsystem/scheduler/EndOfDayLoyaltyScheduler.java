package com.carwash.carwashsystem.scheduler;

import com.carwash.carwashsystem.entity.Booking;
import com.carwash.carwashsystem.enums.BookingStatus;
import com.carwash.carwashsystem.enums.LoyaltyTransactionType;
import com.carwash.carwashsystem.repository.BookingRepository;
import com.carwash.carwashsystem.repository.LoyaltyTransactionRepository;
import com.carwash.carwashsystem.service.interfaces.LoyaltyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EndOfDayLoyaltyScheduler {
    private final BookingRepository bookingRepository;
    private final LoyaltyTransactionRepository loyaltyTransactionRepository;
    private final LoyaltyService loyaltyService;

    @Scheduled(cron = "0 0 23 * * ?")
    @Transactional
    public void processEndOfDayLoyalty() {
        log.info("Processing end-of-day loyalty points...");
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        // ✅ Dùng method đã thêm
        List<Booking> completedBookings = bookingRepository.findByStatusAndCompletedTimeBetween(
                BookingStatus.COMPLETED, startOfDay, endOfDay);

        for (Booking booking : completedBookings) {
            // ✅ Dùng existsByBookingIdAndType
            boolean alreadyProcessed = loyaltyTransactionRepository.existsByBookingIdAndType(
                    booking.getId(), LoyaltyTransactionType.EARNED);
            if (!alreadyProcessed && booking.getTotalPrice() != null) {
                loyaltyService.addPoints(booking.getId(), booking.getId(),
                        booking.getTotalPrice().longValue());
                log.info("Added loyalty points for booking {}", booking.getId());
            }
        }
        log.info("Finished processing {} completed bookings", completedBookings.size());
    }
}