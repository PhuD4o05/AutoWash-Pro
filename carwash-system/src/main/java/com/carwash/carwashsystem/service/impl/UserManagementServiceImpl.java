package com.carwash.carwashsystem.service.impl;

import com.carwash.carwashsystem.dto.response.UserGroupResponse;
import com.carwash.carwashsystem.dto.response.UserManagementResponse;
import com.carwash.carwashsystem.entity.Account;
import com.carwash.carwashsystem.repository.AccountRepository;
import com.carwash.carwashsystem.repository.specification.UserSpecification;
import com.carwash.carwashsystem.service.interfaces.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl
        implements UserManagementService {


    private final AccountRepository accountRepository;



    @Override
    public Page<UserManagementResponse> searchUsers(
            String keyword,
            String role,
            String status,
            Pageable pageable
    ){


        Specification<Account> spec =
                UserSpecification.filter(
                        keyword,
                        role,
                        status
                );



        return accountRepository
                .findAll(spec,pageable)
                .map(account -> {


                    UserManagementResponse dto =
                            new UserManagementResponse();



                    dto.setId(account.getId());

                    dto.setEmail(account.getEmail());

                    dto.setRole(
                            account.getRole().name()
                    );


                    dto.setStatus(
                            account.getStatus().name()
                    );



                    if(account.getCustomer()!=null){

                        dto.setFullName(
                                account.getCustomer().getFullName()
                        );


                        dto.setPhone(
                                account.getCustomer().getPhoneNumber()
                        );


                        dto.setLoyaltyPoint(
                                account.getCustomer()
                                        .getCurrentPoints()
                        );


                    }


                    dto.setCreatedAt(
                            account.getCreatedAt()
                    );



                    return dto;


                });


    }

    @Override
    public List<UserGroupResponse> groupByRole() {

        List<Object[]> result =
                accountRepository.countUsersByRole();

        return result.stream()

                .map(obj -> new UserGroupResponse(

                        obj[0].toString(),

                        (Long) obj[1]

                ))

                .toList();

    }

}