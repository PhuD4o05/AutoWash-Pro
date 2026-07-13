package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.dto.request.CustomerUpdateRequest;
import com.carwash.carwashsystem.dto.request.RegisterRequest;
import com.carwash.carwashsystem.dto.response.CustomerResponse;
import com.carwash.carwashsystem.entity.Customer;
import com.carwash.carwashsystem.exception.ResourceNotFoundException;
import com.carwash.carwashsystem.repository.CustomerRepository;
import com.carwash.carwashsystem.service.interfaces.CustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public CustomerResponse register(RegisterRequest request) {
        return null;
    }

    @Override
    public CustomerResponse updateProfile(Long customerId, CustomerUpdateRequest request) {
        return null;
    }

    @Override
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return mapToResponse(customer);
    }

    @Override
    public CustomerResponse getCustomerByPhone(String phone) {
        Customer c = customerRepository.findByPhoneNumber(phone)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách với SĐT " + phone));
        return mapToResponse(c);
    }

    @Override
    public Page<CustomerResponse> getAllCustomers(Pageable pageable) {
        return null;
    }

    @Override
    public void changePassword(Long customerId, String oldPassword, String newPassword) {

    }

    @Override
    public void deactivateCustomer(Long customerId) {

    }

    @Transactional
    @Override
    public CustomerResponse updateCustomer(Long id, CustomerUpdateRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        customer.setFullName(request.getFullName());
        customer.setPhoneNumber(request.getPhone());
        customer.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            customer.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return mapToResponse(customerRepository.save(customer));
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Customer getCustomerEntityById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .avatarUrl(customer.getAvatarUrl())
                .membershipTier(customer.getMembershipTier())
                .totalPoints(customer.getTotalPoints())
                .currentPoints(customer.getCurrentPoints())
                .isActive(customer.getIsActive())
                .role(customer.getRole())
                .build();
    }

    @Override
    public java.util.List<CustomerResponse> getCustomerAccounts() {
        return customerRepository.findAll().stream()
                .filter(c -> c.getRole() == com.carwash.carwashsystem.enums.Role.CUSTOMER)
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional
    public CustomerResponse createCustomerByStaff(String fullName, String phone, String password) {
        if (customerRepository.findByPhoneNumber(phone).isPresent())
            throw new com.carwash.carwashsystem.exception.DuplicateResourceException("SĐT đã tồn tại");
        Customer c = Customer.builder()
                .fullName(fullName)
                .phoneNumber(phone)
                .password(passwordEncoder.encode(password))
                .role(com.carwash.carwashsystem.enums.Role.CUSTOMER)
                .membershipTier(com.carwash.carwashsystem.enums.MembershipTier.MEMBER)
                .totalPoints(0).currentPoints(0)
                .isActive(true)
                .build();
        return mapToResponse(customerRepository.save(c));
    }

    @Override
    @Transactional
    public void resetPassword(Long id, String newPassword) {
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
        c.setPassword(passwordEncoder.encode(newPassword));
        customerRepository.save(c);
    }

    @Override
    @Transactional
    public CustomerResponse updateUserByAdmin(Long id, String fullName, String phone, String email, String role) {
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        if (fullName != null) c.setFullName(fullName);
        if (phone != null) c.setPhoneNumber(phone);
        if (email != null) c.setEmail(email);
        if (role != null && !role.isBlank()) c.setRole(com.carwash.carwashsystem.enums.Role.valueOf(role.toUpperCase()));
        return mapToResponse(customerRepository.save(c));
    }
}