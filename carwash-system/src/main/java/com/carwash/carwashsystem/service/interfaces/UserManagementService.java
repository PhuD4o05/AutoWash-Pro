package com.carwash.carwashsystem.service.interfaces;


import com.carwash.carwashsystem.dto.response.UserGroupResponse;
import com.carwash.carwashsystem.dto.response.UserManagementResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface UserManagementService {


    Page<UserManagementResponse> searchUsers(
            String keyword,
            String role,
            String status,
            Pageable pageable
    );
    List<UserGroupResponse> groupByRole();


}