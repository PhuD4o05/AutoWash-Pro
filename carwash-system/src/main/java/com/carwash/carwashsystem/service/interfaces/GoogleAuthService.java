package com.carwash.carwashsystem.service.interfaces;

import com.carwash.carwashsystem.entity.Customer;

public interface GoogleAuthService {

    Customer authenticate(String idToken);

}
