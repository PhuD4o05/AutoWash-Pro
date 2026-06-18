package com.carwash.carwashsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QRCodeConfig {

    // Kích thước QR code mặc định (pixel)
    @Bean
    public int qrCodeWidth() {
        return 300;
    }

    @Bean
    public int qrCodeHeight() {
        return 300;
    }

    // Định dạng ảnh (PNG, JPG...)
    @Bean
    public String qrCodeImageFormat() {
        return "PNG";
    }
}