//package com.carwash.carwashsystem.dto.response;
//
//import com.carwash.carwashsystem.enums.MembershipTier;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class LoyaltyResponse {
//    private Integer currentPoints;
//    private MembershipTier membershipTier;
//    private Integer totalEarnedPoints;
//    private Integer totalRedeemedPoints;
//}
package com.carwash.carwashsystem.dto.response;


import com.carwash.carwashsystem.enums.MembershipTier;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class LoyaltyResponse {


    private Integer currentPoints;


    private Integer totalEarnedPoints;


    private Integer totalRedeemedPoints;


    private MembershipTier membershipTier;

}