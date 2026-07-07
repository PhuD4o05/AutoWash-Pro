package com.carwash.carwashsystem.repository;

import com.carwash.carwashsystem.entity.ServicePackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicePackageRepository extends JpaRepository<ServicePackage, Long> {
    Optional<ServicePackage> findByName(String name);
    List<ServicePackage> findByIsActiveTrue();
   // List<ServicePackage> findByIsActiveTrueOrderByPriceAsc();
    @Query("SELECT DISTINCT sp.vehicleType FROM ServicePackage sp")
    List<String> findDistinctVehicleTypes();
    List<ServicePackage> findByIsActiveTrueOrderByBasePriceAsc();
}
