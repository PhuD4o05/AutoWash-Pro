package com.carwash.carwashsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CheckoutController {

    @GetMapping("/payment/return")
    public String paymentReturn(@RequestParam("orderCode") Long orderCode,
                                @RequestParam("status") String status,
                                Model model) {
        if ("PAID".equals(status)) {
            model.addAttribute("message", "Thanh toán thành công! Đơn hàng " + orderCode + " đã được xác nhận.");
        } else {
            model.addAttribute("message", "Thanh toán thất bại hoặc chưa hoàn tất.");
        }
        return "payment-result";
    }

    @GetMapping("/payment/cancel")
    public String paymentCancel(@RequestParam("orderCode") Long orderCode,
                                @RequestParam("status") String status,
                                Model model) {
        model.addAttribute("message", "Thanh toán đã bị hủy. Bạn có thể thử lại.");
        return "payment-result";
    }
}