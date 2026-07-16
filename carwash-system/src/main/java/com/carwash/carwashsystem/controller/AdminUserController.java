package com.carwash.carwashsystem.controller;

import com.carwash.carwashsystem.dto.response.UserGroupResponse;
import com.carwash.carwashsystem.dto.response.UserManagementResponse;
import com.carwash.carwashsystem.service.interfaces.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/user-management")
@RequiredArgsConstructor
public class AdminUserController {


    private final UserManagementService userService;



    @GetMapping
    public Page<UserManagementResponse> getUsers(


            @RequestParam(required=false)
            String keyword,


            @RequestParam(required=false)
            String role,


            @RequestParam(required=false)
            String status,


//            @PageableDefault(
//                    size = 10,
//                    sort="createdAt",
//                    direction = Sort.Direction.DESC
//            )
//            Pageable pageable
            @ParameterObject
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable


    ){


        return userService.searchUsers(
                keyword,
                role,
                status,
                pageable
        );



    }
    @GetMapping("/group-by-role")
    public List<UserGroupResponse> groupByRole() {
        return userService.groupByRole();
    }



}