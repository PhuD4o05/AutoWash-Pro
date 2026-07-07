package com.carwash.carwashsystem.repository;

import com.carwash.carwashsystem.entity.WashBay;
import com.carwash.carwashsystem.enums.WashBayStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WashBayRepository extends JpaRepository<WashBay, Long> {
    List<WashBay> findByStatus(WashBayStatus status);
    List<WashBay> findByStatusNot(WashBayStatus status);
    @Query("SELECT COUNT(w) FROM WashBay w WHERE w.status = 'AVAILABLE'")
    long countAvailableBays();  // giữ lại nếu cần
}