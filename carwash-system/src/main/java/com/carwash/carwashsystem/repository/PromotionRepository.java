package com.carwash.carwashsystem.repository;

import com.carwash.carwashsystem.entity.Promotion;
import com.carwash.carwashsystem.enums.MembershipTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {


    List<Promotion> findByIsActiveTrue();


    List<Promotion> findByApplicableTier(MembershipTier tier);


    @Query("SELECT p FROM Promotion p " +
            "WHERE p.startDate <= :now " +
            "AND p.endDate >= :now " +
            "AND p.isActive = true")
    List<Promotion> findActivePromotions(
            @Param("now") LocalDateTime now
    );


    @Query("SELECT p FROM Promotion p " +
            "WHERE p.code = :code " +
            "AND p.startDate <= :now " +
            "AND p.endDate >= :now " +
            "AND p.isActive = true")
    Optional<Promotion> findValidByCode(
            @Param("code") String code,
            @Param("now") LocalDateTime now
    );

}