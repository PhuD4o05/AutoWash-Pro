package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.dto.request.PromotionRequest;
import com.carwash.carwashsystem.dto.response.PromotionResponse;
import com.carwash.carwashsystem.entity.Promotion;
import com.carwash.carwashsystem.repository.PromotionRepository;
import com.carwash.carwashsystem.service.interfaces.PromotionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    @Override
    public List<Promotion> getActivePromotions() {
        return promotionRepository.findActivePromotions(LocalDateTime.now());
    }

//    @Transactional
//    @Override
//    public PromotionResponse createPromotion(PromotionRequest request) {
//        Promotion promotion = Promotion.builder()
//                .code(request.getCode())
//                .description(request.getDescription())
//                .discountPercent(request.getDiscountPercent())
//                .applicableTier(request.getApplicableTier())
//                .startDate(request.getStartDate())
//                .endDate(request.getEndDate())
//                .isActive(true)
//                .build();
//        Promotion saved = promotionRepository.save(promotion);
//        return promotionRepository.save(promotion);
//
//    }
@Transactional
@Override
public PromotionResponse createPromotion(PromotionRequest request) {

    Promotion promotion = Promotion.builder()
            .code(request.getCode())
            .name(request.getName())
            .description(request.getDescription())
            .discountPercent(request.getDiscountPercent())
            .applicableTier(request.getApplicableTier())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .isActive(true)
            .build();

    Promotion saved = promotionRepository.save(promotion);

    return PromotionResponse.builder()
            .id(saved.getId())
            .name(saved.getName())
            .description(saved.getDescription())
            .discountPercent(saved.getDiscountPercent())
            .discountAmount(saved.getDiscountAmount())
            .startDate(saved.getStartDate())
            .endDate(saved.getEndDate())
            .isActive(saved.getIsActive())
            .applicableTier(saved.getApplicableTier())
            .build();
}


    @Override
    public PromotionResponse updatePromotion(Long id, PromotionRequest request) {
        return null;
    }

    @Override
    public void deletePromotion(Long id) {

    }

    @Override
    public Page<PromotionResponse> getActivePromotions(Pageable pageable) {
        return null;
    }

    @Override
    public double applyPromotionIfAvailable(Long customerId, Long servicePackageId, LocalDateTime bookingTime) {
        return 0;
    }

    @Override
    public Promotion validatePromotion(String code) {
        return promotionRepository.findValidByCode(code, LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("Invalid promotion"));
    }
}
