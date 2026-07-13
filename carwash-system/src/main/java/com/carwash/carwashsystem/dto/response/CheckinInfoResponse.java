package com.carwash.carwashsystem.dto.response;

import com.carwash.carwashsystem.enums.BookingStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

//@Data
//@Builder
//public class CheckinInfoResponse {
//    private Long bookingId;
//    private Long customerId;
//    private Long vehicleId;
//    private Long servicePackageId;
//    private LocalDateTime scheduledTime;
//    private BookingStatus status;
//    private Integer queuePosition;
//}
@Data
@Builder
public class CheckinInfoResponse {

    private Long bookingId;

    private String customerName;

    private String phoneNumber;

    private String vehicle;

    private String servicePackage;

    private Double totalPrice;

    private LocalDateTime scheduledTime;

    private LocalDateTime checkinTime;

    private BookingStatus status;

    private Integer queuePosition;

    private String message;

    private String bayNumber;

}