package com.carwash.carwashsystem.service.interfaces;

import com.carwash.carwashsystem.dto.request.PriceRuleRequest;
import com.carwash.carwashsystem.dto.response.DynamicPriceRuleResponse;
import java.time.LocalDate;
import java.util.List;

public interface DynamicPriceRuleService {
    DynamicPriceRuleResponse createRule(PriceRuleRequest request);
    DynamicPriceRuleResponse updateRule(Long id, PriceRuleRequest request);
    void deleteRule(Long id);
    List<DynamicPriceRuleResponse> getAllActiveRules();
    Double applyPriceRules(Long basePrice, LocalDate date, String membershipTier);
}