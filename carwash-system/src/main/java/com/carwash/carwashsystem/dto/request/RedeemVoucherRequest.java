//package com.carwash.carwashsystem.dto.request;
//
//import jakarta.validation.constraints.Min;
//import jakarta.validation.constraints.NotBlank;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class RedeemVoucherRequest {
//    @Min(1)
//    private int points;
//    @NotBlank
//    private String voucherCode;
//}
package com.carwash.carwashsystem.dto.request;


import lombok.Data;


@Data
public class RedeemVoucherRequest {


    private Integer points;


    private String voucherCode;

}