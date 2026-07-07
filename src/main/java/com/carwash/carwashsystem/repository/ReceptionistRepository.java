package com.carwash.carwashsystem.repository;

import com.carwash.carwashsystem.entity.Receptionist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReceptionistRepository extends JpaRepository<Receptionist, Long> {
    //  Xóa dòng này nếu có: Optional<Receptionist> findByPhone(String phone);
    // Thêm hoặc sửa thành:
    Optional<Receptionist> findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
}