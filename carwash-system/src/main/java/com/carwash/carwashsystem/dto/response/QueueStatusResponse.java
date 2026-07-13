//package com.carwash.carwashsystem.dto.response;
//
//import lombok.Builder;
//import lombok.Data;
//
//@Data
//@Builder
//public class QueueStatusResponse {
//    private Long bookingId;
//    private Integer position;
//    private String status;
//    private Integer queuePosition;
//}
package com.carwash.carwashsystem.dto.response;

import com.carwash.carwashsystem.enums.BookingStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueStatusResponse {

    private Long bookingId;

    private String customerName;

    private String licensePlate;

    private Integer queuePosition;

    private BookingStatus status;

    private LocalDateTime enqueuedAt;
}