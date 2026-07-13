package com.carwash.carwashsystem.repository;

import com.carwash.carwashsystem.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    List<Voucher> findByCustomerId(Long customerId);

    Optional<Voucher> findByCode(String code);

    // Thay isUsed bằng isActive, expiryDate bằng validUntil
    List<Voucher> findByIsActiveTrueAndValidUntilAfter(LocalDateTime now);

    @Query("SELECT v FROM Voucher v WHERE v.customer.id = :customerId AND v.isActive = true AND v.validUntil > :now")
    List<Voucher> findUnusedValidVouchersByCustomer(@Param("customerId") Long customerId, @Param("now") LocalDateTime now);

    @Query("SELECT v FROM Voucher v " +
            "WHERE v.code = :code " +
            "AND v.isActive = true " +
            "AND v.validFrom <= :now " +
            "AND v.validUntil >= :now")
    Optional<Voucher> findValidByCode(
            @Param("code") String code,
            @Param("now") LocalDateTime now
    );
}