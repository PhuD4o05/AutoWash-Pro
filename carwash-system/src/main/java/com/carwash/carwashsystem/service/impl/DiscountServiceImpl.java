package com.carwash.carwashsystem.service.impl;


import com.carwash.carwashsystem.entity.Voucher;
import com.carwash.carwashsystem.repository.PromotionRepository;
import com.carwash.carwashsystem.repository.VoucherRepository;
import com.carwash.carwashsystem.service.interfaces.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {


    private final VoucherRepository voucherRepository;
    private final PromotionRepository promotionRepository;


    @Override
    public double calculateDiscount(
            double price,
            String voucherCode,
            String membershipTier
    ){

        double discount = 0;


        // Membership discount

        if(membershipTier != null){

            switch(membershipTier){

                case "SILVER":
                    discount += price * 0.05;
                    break;

                case "GOLD":
                    discount += price * 0.10;
                    break;

                case "PLATINUM":
                    discount += price * 0.15;
                    break;

            }

        }
        // Promotion discount

        for (var promotion : promotionRepository.findByIsActiveTrue()) {


            boolean applicable = true;


            // kiểm tra tier

            if(promotion.getApplicableTier() != null
                    && membershipTier != null){

                applicable =
                        promotion.getApplicableTier()
                                .name()
                                .equals(membershipTier);

            }


            if(applicable){


                // giảm theo %

                if(promotion.getDiscountPercent() != null){

                    discount +=
                            price *
                                    promotion.getDiscountPercent()
                                    / 100.0;

                }


                // giảm tiền cố định

                if(promotion.getDiscountAmount() != null){

                    discount +=
                            promotion.getDiscountAmount();

                }

            }

        }



        // Voucher discount

        if(voucherCode != null){

            Voucher voucher =
                    voucherRepository.findByCode(voucherCode)
                            .orElse(null);


            if(voucher != null
                    && Boolean.TRUE.equals(voucher.getIsActive())){


                // giảm theo %

                if(voucher.getDiscountPercent() != null){

                    discount +=
                            price *
                                    voucher.getDiscountPercent()
                                    /100.0;

                }


                // giảm số tiền cố định

                if(voucher.getDiscountAmount() != null){

                    discount +=
                            voucher.getDiscountAmount();

                }

            }

        }


        // tránh giá giảm vượt quá giá gốc

        if(discount > price){
            discount = price;
        }


        return discount;
    }
}