package com.carwash.carwashsystem.service.interfaces;

import com.carwash.carwashsystem.dto.request.CheckinRequest;
import com.carwash.carwashsystem.dto.response.CheckinInfoResponse;

public interface CheckinService {
    CheckinInfoResponse scanQRCode(String qrCode);
    CheckinInfoResponse walkinCheckin(String phone, CheckinRequest request);
}