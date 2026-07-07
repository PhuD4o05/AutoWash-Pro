package com.carwash.carwashsystem.repository;

import com.carwash.carwashsystem.entity.Customer;
import com.carwash.carwashsystem.enums.MembershipTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    List<Customer> findByMembershipTier(MembershipTier tier);
    @Query("SELECT c FROM Customer c WHERE c.totalPoints >= :points")
    List<Customer> findCustomersWithMinPoints(@Param("points") int points);
    @Modifying
    @Query("UPDATE Customer c SET c.membershipTier = :tier WHERE c.id = :id")
    void updateMembershipTier(@Param("id") Long id, @Param("tier") MembershipTier tier);
}