package com.carwash.carwashsystem.controller;

import com.carwash.carwashsystem.dto.response.UserGroupResponse;
import com.carwash.carwashsystem.dto.response.UserManagementResponse;
import com.carwash.carwashsystem.service.interfaces.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserManagementService userManagementService;

    @GetMapping
    public Page<UserManagementResponse> searchUsers(

            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            String role,

            @RequestParam(required = false)
            String status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "createdAt")
            String sortBy,

            @RequestParam(defaultValue = "desc")
            String direction

    ) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable =
                PageRequest.of(page, size, sort);

        return userManagementService.searchUsers(
                keyword,
                role,
                status,
                pageable
        );

    }
    @GetMapping("/group-by-role")
    public List<UserGroupResponse> groupByRole(){

        return userManagementService.groupByRole();

    }
}