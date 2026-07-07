package com.carwash.carwashsystem.controller;

import com.carwash.carwashsystem.dto.response.ServicePackageResponse;
import com.carwash.carwashsystem.service.interfaces.ServicePackageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@Tag(name = "Public Controller", description = "Các API công khai không cần đăng nhập")
public class PublicController {

    private final ServicePackageService packageService;

    @GetMapping("/services")
    public ResponseEntity<List<ServicePackageResponse>> getAllServices() {
        return ResponseEntity.ok(packageService.getAllActivePackages());
    }
}