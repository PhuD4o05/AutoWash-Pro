package com.carwash.carwashsystem.controller;

import com.carwash.carwashsystem.entity.ServicePackage;
import com.carwash.carwashsystem.service.interfaces.ServicePackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-packages")
@RequiredArgsConstructor
public class ServicePackageController {

    private final ServicePackageService servicePackageService;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ServicePackage>> getAllForAdmin() {
        return ResponseEntity.ok(servicePackageService.getAllPackages());
    }

    @GetMapping("/active")
    public ResponseEntity<List<ServicePackage>> getActivePackages() {
        return ResponseEntity.ok(servicePackageService.getActivePackages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicePackage> getPackageById(@PathVariable Long id) {
        return ResponseEntity.ok(servicePackageService.getPackageById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServicePackage> createPackage(@RequestBody ServicePackage servicePackage) {
        return new ResponseEntity<>(servicePackageService.createPackage(servicePackage), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServicePackage> updatePackage(@PathVariable Long id,
                                                        @RequestBody ServicePackage servicePackage) {
        return ResponseEntity.ok(servicePackageService.updatePackage(id, servicePackage));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePackage(@PathVariable Long id) {
        servicePackageService.deletePackage(id);
        return ResponseEntity.noContent().build();
    }
}