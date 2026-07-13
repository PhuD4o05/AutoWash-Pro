package com.carwash.carwashsystem.service.interfaces;

import com.carwash.carwashsystem.dto.request.CustomerUpdateRequest;
import com.carwash.carwashsystem.dto.request.RegisterRequest;
import com.carwash.carwashsystem.dto.response.CustomerResponse;
import com.carwash.carwashsystem.entity.Customer;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {
    CustomerResponse register(RegisterRequest request);
    CustomerResponse updateProfile(Long customerId, CustomerUpdateRequest request);
    CustomerResponse getCustomerById(Long id);
    CustomerResponse getCustomerByPhone(String phone);
    Page<CustomerResponse> getAllCustomers(Pageable pageable);
    void changePassword(Long customerId, String oldPassword, String newPassword);
    void deactivateCustomer(Long customerId);

    @Transactional
    CustomerResponse updateCustomer(Long id, CustomerUpdateRequest request);

    List<CustomerResponse> getAllCustomers();

    Customer getCustomerEntityById(Long id);

    java.util.List<CustomerResponse> getCustomerAccounts();
    CustomerResponse createCustomerByStaff(String fullName, String phone, String password);
    void resetPassword(Long id, String newPassword);
    CustomerResponse updateUserByAdmin(Long id, String fullName, String phone, String email, String role);
}