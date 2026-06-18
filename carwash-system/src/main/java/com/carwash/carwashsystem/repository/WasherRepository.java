package com.carwash.carwashsystem.repository;

import com.carwash.carwashsystem.entity.Washer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WasherRepository extends JpaRepository<Washer, Long> {
    // XÓA dòng: Optional<Washer> findByUserId(Long userId);
    List<Washer> findByIsActiveTrue();
    @Query("SELECT w FROM Washer w WHERE w.id NOT IN (SELECT a.washer.id FROM Assignment a WHERE a.endTime > :currentTime AND a.status = 'ACTIVE')")
    List<Washer> findAvailableWashersAtTime(@Param("currentTime") String currentTime);
    Optional<Washer> findByPhoneNumber(String phoneNumber);
}