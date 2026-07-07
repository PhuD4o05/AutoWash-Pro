package com.carwash.carwashsystem.entity;

import com.carwash.carwashsystem.enums.MembershipTier;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dynamic_price_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DynamicPriceRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ruleName;        // "Weekend surcharge", "Tet surcharge", "Gold discount"

    private Boolean isWeekend;      // true = áp cho thứ 7, CN
    private Boolean isHoliday;      // true = ngày lễ

    private LocalDate specificDate; // nếu rule áp cho ngày cụ thể (lễ)

    @Enumerated(EnumType.STRING)
    private MembershipTier applicableTier; // null cho tất cả

    private Integer percentAdjustment;    // +10 (tăng 10%) hoặc -5 (giảm 5%)

    @Builder.Default
    private Boolean isActive = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
