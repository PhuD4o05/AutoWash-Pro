package com.carwash.carwashsystem.repository;

import com.carwash.carwashsystem.entity.DynamicPriceRule;
import com.carwash.carwashsystem.enums.MembershipTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DynamicPriceRuleRepository extends JpaRepository<DynamicPriceRule, Long> {

    List<DynamicPriceRule> findByIsActiveTrue();

    Optional<DynamicPriceRule> findByIsWeekendTrueAndIsActiveTrue();

    List<DynamicPriceRule> findByIsHolidayTrueAndIsActiveTrue();

    Optional<DynamicPriceRule> findByApplicableTierAndIsActiveTrue(MembershipTier tier);

    // Có thể giữ thêm method này nếu cần logic theo ngày (không cần Holiday entity)
    Optional<DynamicPriceRule> findBySpecificDateAndIsActiveTrue(LocalDate date);
}