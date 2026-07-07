package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.dto.request.*;
import com.carwash.carwashsystem.dto.response.*;
import com.carwash.carwashsystem.entity.*;
import com.carwash.carwashsystem.enums.Role;
import com.carwash.carwashsystem.enums.WashBayStatus;
import com.carwash.carwashsystem.exception.ResourceNotFoundException;
import com.carwash.carwashsystem.repository.*;
import com.carwash.carwashsystem.service.interfaces.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final CustomerRepository customerRepository;
    private final ReceptionistRepository receptionistRepository;
    private final WasherRepository washerRepository;
    private final ServicePackageRepository servicePackageRepository;
    private final DynamicPriceRuleRepository priceRuleRepository;
    private final PromotionRepository promotionRepository;
    private final WorkShiftRepository workShiftRepository;
    private final WashBayRepository washBayRepository;
    private final AssignmentRepository assignmentRepository;
    private final PasswordEncoder passwordEncoder;

    // ========== Quản lý tài khoản ==========
    @Override
    public void createCustomerAccount(RegisterRequest request) {
        Customer customer = Customer.builder()
                .fullName(request.getFullName())
                .phoneNumber(request.getPhone())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .isActive(true)
                .build();
        customerRepository.save(customer);
    }

    @Override
    public void createReceptionistAccount(String fullName, String email, String phone, String password) {
        Receptionist receptionist = Receptionist.builder()
                .fullName(fullName)
                .email(email)
                .phoneNumber(phone)
                .password(passwordEncoder.encode(password))
                .role(Role.RECEPTIONIST)
                .isActive(true)
                .build();
        receptionistRepository.save(receptionist);
    }

    @Override
    public void createWasherAccount(String fullName, String email, String phone, String password) {
        Washer washer = Washer.builder()
                .fullName(fullName)
                .email(email)
                .phoneNumber(phone)
                .password(passwordEncoder.encode(password))
                .role(Role.WASHER)
                .isActive(true)
                .build();
        washerRepository.save(washer);
    }

    @Override
    public void deactivateUser(Long userId, String role) {
        if (Role.CUSTOMER.name().equals(role)) {
            Customer c = customerRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
            c.setIsActive(false);
            customerRepository.save(c);
        } else if (Role.RECEPTIONIST.name().equals(role)) {
            Receptionist r = receptionistRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Receptionist not found"));
            r.setIsActive(false);
            receptionistRepository.save(r);
        } else if (Role.WASHER.name().equals(role)) {
            Washer w = washerRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Washer not found"));
            w.setIsActive(false);
            washerRepository.save(w);
        }
    }

    @Override
    public void activateUser(Long userId, String role) {
        if (Role.CUSTOMER.name().equals(role)) {
            Customer c = customerRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
            c.setIsActive(true);
            customerRepository.save(c);
        } else if (Role.RECEPTIONIST.name().equals(role)) {
            Receptionist r = receptionistRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Receptionist not found"));
            r.setIsActive(true);
            receptionistRepository.save(r);
        } else if (Role.WASHER.name().equals(role)) {
            Washer w = washerRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Washer not found"));
            w.setIsActive(true);
            washerRepository.save(w);
        }
    }

    // ========== Service Package ==========
    @Override
    public ServicePackageResponse createServicePackage(ServicePackageRequest request) {
        ServicePackage sp = ServicePackage.builder()
                .name(request.getName())
                .description(request.getDescription())
                .estimatedMinutes(request.getDurationMinutes())  // mapping durationMinutes -> estimatedMinutes
                .basePrice(request.getPrice() != null ? request.getPrice().longValue() : 0L)   //báo sai               // mapping price -> basePrice
                .vehicleType(request.getVehicleType())
                .isActive(true)
                .build();
        return toServicePackageResponse(servicePackageRepository.save(sp));
    }

    @Override
    public ServicePackageResponse updateServicePackage(Long id, ServicePackageRequest request) {
        ServicePackage sp = servicePackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service package not found"));
        sp.setName(request.getName());
        sp.setDescription(request.getDescription());
        sp.setEstimatedMinutes(request.getDurationMinutes());
        sp.setBasePrice(request.getPrice() != null ? request.getPrice().longValue() : 0L);//báo sai
        sp.setVehicleType(request.getVehicleType());//báo sai
        return toServicePackageResponse(servicePackageRepository.save(sp));
    }

    @Override
    public void deleteServicePackage(Long id) {
        ServicePackage sp = servicePackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service package not found"));
        sp.setIsActive(false);
        servicePackageRepository.save(sp);
    }

    // ========== Price Rule ==========
    @Override
    public DynamicPriceRuleResponse createPriceRule(PriceRuleRequest request) {
        DynamicPriceRule rule = DynamicPriceRule.builder()
                .ruleName(request.getRuleName())
                .isWeekend(request.getIsWeekend())
                .isHoliday(request.getIsHoliday())
                .specificDate(request.getSpecificDate())
                .applicableTier(request.getApplicableTier())
                .percentAdjustment(request.getPercentAdjustment())
                .isActive(true)
                .build();
        return toDynamicPriceRuleResponse(priceRuleRepository.save(rule));
    }

    @Override
    public void deletePriceRule(Long id) {
        DynamicPriceRule rule = priceRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Price rule not found"));
        rule.setIsActive(false);
        priceRuleRepository.save(rule);
    }

    // ========== Promotion ==========
    @Override
    public PromotionResponse createPromotion(PromotionRequest request) {
        Promotion p = Promotion.builder()
                .name(request.getName())
                .description(request.getDescription())
                .applicableTier(request.getApplicableTier())
                .discountPercent(request.getDiscountPercent())
                .discountAmount(0L)  // PromotionRequest không có discountAmount, set mặc định
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isActive(true)
                .build();
        return toPromotionResponse(promotionRepository.save(p));
    }

    @Override
    public void deletePromotion(Long id) {
        Promotion p = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));
        p.setIsActive(false);
        promotionRepository.save(p);
    }

    // ========== Work Shift ==========
    @Override
    public List<WorkShiftResponse> getAllWorkShifts() {
        return workShiftRepository.findAll().stream()
                .map(this::toWorkShiftResponse)
                .collect(Collectors.toList());
    }

    @Override
    public WorkShiftResponse assignWorkShift(ShiftAssignmentRequest request) {
        Washer washer = washerRepository.findById(request.getWasherId())
                .orElseThrow(() -> new ResourceNotFoundException("Washer not found"));
        WorkShift workShift = workShiftRepository.findById(request.getWasherId())
                .orElseThrow(() -> new ResourceNotFoundException("Work shift not found"));
        WashBay washBay = washBayRepository.findById(request.getWasherId())
                .orElseThrow(() -> new ResourceNotFoundException("Wash bay not found"));

        Assignment assignment = Assignment.builder()
                .washer(washer)
                .workShift(workShift)
                .washBay(washBay)
                .build();
        assignmentRepository.save(assignment);
        return toWorkShiftResponse(workShift);
    }

    // ========== Wash Bay ==========
    @Override
    public List<WashBayResponse> getAllWashBays() {
        return washBayRepository.findAll().stream()
                .map(this::toWashBayResponse)
                .collect(Collectors.toList());
    }

    @Override
    public WashBayResponse updateWashBayStatus(Long bayId, String status) {
        WashBay bay = washBayRepository.findById(bayId)
                .orElseThrow(() -> new ResourceNotFoundException("Wash bay not found"));
        bay.setStatus(WashBayStatus.valueOf(status.toUpperCase()));
        return toWashBayResponse(washBayRepository.save(bay));
    }

    // ========== Mapping Helpers ==========
    private ServicePackageResponse toServicePackageResponse(ServicePackage sp) {
        return ServicePackageResponse.builder()
                .id(sp.getId())
                .name(sp.getName())
                .description(sp.getDescription())
                .estimatedMinutes(sp.getEstimatedMinutes())
                .basePrice(sp.getBasePrice())
                .vehicleType(sp.getVehicleType())//báo sai
                .isActive(sp.getIsActive())
                .build();
    }

    private DynamicPriceRuleResponse toDynamicPriceRuleResponse(DynamicPriceRule rule) {
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

    private PromotionResponse toPromotionResponse(Promotion p) {
        return PromotionResponse.builder()
                .id(p.getId())
                .name(p.getName())//báo sai
                .description(p.getDescription())
                .applicableTier(p.getApplicableTier())
                .discountPercent(p.getDiscountPercent())
                .discountAmount(p.getDiscountAmount())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .isActive(p.getIsActive())
                .build();
    }

    private WorkShiftResponse toWorkShiftResponse(WorkShift ws) {
        return WorkShiftResponse.builder()
                .id(ws.getId())
                .date(ws.getShiftDate())
                .shiftType(ws.getShiftType())
                .startTime(ws.getStartTime())
                .endTime(ws.getEndTime())
                .build();
    }

    private WashBayResponse toWashBayResponse(WashBay bay) {
        return WashBayResponse.builder()
                .id(bay.getId())
                .bayNumber(bay.getBayNumber())
                .status(bay.getStatus())
                .build();
    }
}