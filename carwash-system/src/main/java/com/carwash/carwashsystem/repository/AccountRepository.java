package com.carwash.carwashsystem.repository;


import com.carwash.carwashsystem.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface AccountRepository
        extends JpaRepository<Account,Long>,
        JpaSpecificationExecutor<Account> {
    @Query("""
SELECT a.role, COUNT(a)
FROM Account a
GROUP BY a.role
""")
    List<Object[]> countUsersByRole();

}
