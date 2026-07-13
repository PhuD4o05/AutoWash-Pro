package com.carwash.carwashsystem.controller;


import com.carwash.carwashsystem.dto.request.RedeemVoucherRequest;
import com.carwash.carwashsystem.dto.response.LoyaltyResponse;
import com.carwash.carwashsystem.service.interfaces.LoyaltyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/loyalty")
@RequiredArgsConstructor
public class LoyaltyController {


    private final LoyaltyService loyaltyService;



    @GetMapping("/{customerId}")
    public ResponseEntity<LoyaltyResponse> getInfo(
            @PathVariable Long customerId
    ){

        return ResponseEntity.ok(
                loyaltyService.getCustomerLoyalty(customerId)
        );
    }



    @GetMapping("/{customerId}/transactions")
    public ResponseEntity<?> history(
            @PathVariable Long customerId,
            Pageable pageable
    ){

        return ResponseEntity.ok(
                loyaltyService
                        .getLoyaltyTransactions(customerId,pageable)
        );
    }



    @PostMapping("/{customerId}/redeem")
    public ResponseEntity<?> redeem(
            @PathVariable Long customerId,
            @RequestBody RedeemVoucherRequest request
    ){

        return ResponseEntity.ok(
                loyaltyService
                        .redeemVoucher(request,customerId)
        );

    }


}