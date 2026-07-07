package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.dto.request.PriceRuleRequest;
import com.carwash.carwashsystem.dto.response.DynamicPriceRuleResponse;
import com.carwash.carwashsystem.entity.DynamicPriceRule;
import com.carwash.carwashsystem.enums.MembershipTier;
import com.carwash.carwashsystem.exception.ResourceNotFoundException;
import com.carwash.carwashsystem.repository.DynamicPriceRuleRepository;
import com.carwash.carwashsystem.service.interfaces.DynamicPriceRuleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DynamicPriceRuleServiceImpl implements DynamicPriceRuleService {

    private final DynamicPriceRuleRepository ruleRepository;

    @Override
    public List<DynamicPriceRuleResponse> getAllActiveRules() {
        return ruleRepository.findByIsActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DynamicPriceRuleResponse createRule(PriceRuleRequest request) {
        DynamicPriceRule rule = DynamicPriceRule.builder()
                .ruleName(request.getRuleName())
                .isWeekend(request.getIsWeekend())
                .isHoliday(request.getIsHoliday())
                .specificDate(request.getSpecificDate())
                .applicableTier(request.getApplicableTier())
                .percentAdjustment(request.getPercentAdjustment())
                .isActive(true)
                .build();
        return toResponse(ruleRepository.save(rule));
    }

    @Override
    @Transactional
    public DynamicPriceRuleResponse updateRule(Long id, PriceRuleRequest request) {
        DynamicPriceRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Price rule not found"));
        rule.setRuleName(request.getRuleName());
        rule.setIsWeekend(request.getIsWeekend());
        rule.setIsHoliday(request.getIsHoliday());
        rule.setSpecificDate(request.getSpecificDate());
        rule.setApplicableTier(request.getApplicableTier());
        rule.setPercentAdjustment(request.getPercentAdjustment());
        return toResponse(ruleRepository.save(rule));
    }

    @Override
    @Transactional
    public void deleteRule(Long id) {
        DynamicPriceRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Price rule not found"));
        rule.setIsActive(false);
        ruleRepository.save(rule);
    }

    @Override
    public Double applyPriceRules(Long basePrice, LocalDate date, String membershipTierName) {
        double finalPrice = basePrice.doubleValue();

        // Weekend surcharge
        Optional<DynamicPriceRule> weekendRuleOpt = ruleRepository.findByIsWeekendTrueAndIsActiveTrue();
        if (weekendRuleOpt.isPresent() && date.getDayOfWeek().getValue() >= 6) {
            int percent = weekendRuleOpt.get().getPercentAdjustment();
            finalPrice += finalPrice * percent / 100;
        }

        // Membership discount
        if (membershipTierName != null) {
            MembershipTier tier = MembershipTier.valueOf(membershipTierName);
            Optional<DynamicPriceRule> memberRuleOpt = ruleRepository.findByApplicableTierAndIsActiveTrue(tier);
            if (memberRuleOpt.isPresent()) {
                int percent = memberRuleOpt.get().getPercentAdjustment();
                finalPrice += finalPrice * percent / 100;
            }
        }

        return finalPrice;
    }

    // Helper method - đặt đúng vị trí (sau tất cả các method, trước dấu đóng ngoặc của class)
    private DynamicPriceRuleResponse toResponse(DynamicPriceRule rule) {
        return DynamicPriceRuleResponse.builder()
                .id(rule.getId())
                .ruleName(rule.getRuleName())
                .isWeekend(rule.getIsWeekend())
                .isHoliday(rule.getIsHoliday())
                .specificDate(rule.getSpecificDate())
                .applicableTier(rule.getApplicableTier())
                .percentAdjustment(rule.getPercentAdjustment())
                .isActive(rule.getIsActive())
                .build();
    }
}