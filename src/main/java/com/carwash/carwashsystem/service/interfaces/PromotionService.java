package com.carwash.carwashsystem.service.interfaces;

import com.carwash.carwashsystem.dto.request.PromotionRequest;
import com.carwash.carwashsystem.dto.response.PromotionResponse;
import com.carwash.carwashsystem.entity.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface PromotionService {
    List<Promotion> getActivePromotions();

    PromotionResponse createPromotion(PromotionRequest request);
    PromotionResponse updatePromotion(Long id, PromotionRequest request);
    void deletePromotion(Long id);
    Page<PromotionResponse> getActivePromotions(Pageable pageable);
    double applyPromotionIfAvailable(Long customerId, Long servicePackageId, LocalDateTime bookingTime);

    Promotion validatePromotion(String code);
}