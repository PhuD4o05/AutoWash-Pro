package com.carwash.carwashsystem.controller;

import com.carwash.carwashsystem.dto.response.PromotionResponse;
import com.carwash.carwashsystem.entity.Promotion;
import com.carwash.carwashsystem.service.interfaces.PromotionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
@Tag(name = "Promotion Controller", description = "Xem khuyến mãi")
public class PromotionController {

    private final PromotionService promotionService;

    @GetMapping
    public ResponseEntity<List<PromotionResponse>> getActivePromotions() {
        // Lấy tất cả promotion đang hoạt động (không phân trang)
        var page = promotionService.getActivePromotions(Pageable.unpaged());
        List<PromotionResponse> responses = page.getContent().stream()
                .map(p -> PromotionResponse.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .description(p.getDescription())
                        .discountPercent(p.getDiscountPercent())
                        .applicableTier(p.getApplicableTier())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/validate")
    public ResponseEntity<Promotion> validatePromotion(@RequestParam String code) {
        Promotion promotion = promotionService.validatePromotion(code);
        return ResponseEntity.ok(promotion);
    }
}