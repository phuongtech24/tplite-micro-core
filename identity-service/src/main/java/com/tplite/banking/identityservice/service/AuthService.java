package com.tplite.banking.identityservice.service;

import com.tplite.banking.identityservice.dto.AuthRequest;

public interface AuthService {
    String register(AuthRequest request);
    String login(AuthRequest request);
    String verifyEkyc(com.tplite.banking.identityservice.dto.EkycVerifyRequest request);
}
